const crypto = require("crypto");
const express = require("express");
const { createUserAuth } = require("../security/userAuth");

const ORDER_STATUSES = ["created", "confirmed", "delivering", "delivered", "cancelled"];
const MAX_ORDER_POSITIONS = 50;
const MAX_ITEM_QTY = 99;
const MAX_ADDRESS_LENGTH = 300;

function createOrderRoutes({ config, pool }) {
  const router = express.Router();

  router.get("/", createUserAuth(config.jwtSecret), async (req, res, next) => {
    try {
      res.json(await loadOrders(pool, req.userId));
    } catch (error) {
      next(error);
    }
  });

  router.post("/", createUserAuth(config.jwtSecret), async (req, res, next) => {
    const client = await pool.connect();
    try {
      const items = parseItems(req.body.items);
      if ("error" in items) {
        res.status(400).json({ error: items.error });
        return;
      }
      const addressText = typeof req.body.addressText === "string"
        ? req.body.addressText.trim().slice(0, MAX_ADDRESS_LENGTH)
        : "";

      const productRows = await client.query(
        "SELECT id, name, price FROM products WHERE id = ANY($1)",
        [items.value.map((item) => item.productId)],
      );
      const products = new Map(productRows.rows.map((row) => [row.id, row]));
      if (products.size !== items.value.length) {
        res.status(400).json({ error: "UNKNOWN_PRODUCT" });
        return;
      }

      // Totals are computed from server-side prices only.
      const subtotal = items.value.reduce(
        (sum, item) => sum + products.get(item.productId).price * item.qty,
        0,
      );
      const delivery = subtotal > config.freeDeliveryThreshold ? 0 : config.deliveryPrice;
      const orderId = crypto.randomUUID();

      await client.query("BEGIN");
      await client.query(
        `INSERT INTO orders (id, user_id, user_phone, address_text, subtotal, delivery, total)
         VALUES ($1, $2, $3, $4, $5, $6, $7)`,
        [orderId, req.userId, req.userPhone, addressText, subtotal, delivery, subtotal + delivery],
      );
      for (const item of items.value) {
        const product = products.get(item.productId);
        await client.query(
          `INSERT INTO order_items (order_id, product_id, product_name, price, qty)
           VALUES ($1, $2, $3, $4, $5)`,
          [orderId, product.id, product.name, product.price, item.qty],
        );
      }
      await client.query("COMMIT");

      const orders = await loadOrders(pool, req.userId, orderId);
      res.status(201).json(orders[0]);
    } catch (error) {
      await client.query("ROLLBACK").catch(() => {});
      next(error);
    } finally {
      client.release();
    }
  });

  // Operator endpoint: update order status.
  // curl -X PATCH .../orders/<id>/status -H 'X-Orders-Admin-Token: ...' -d '{"status":"delivering"}'
  router.patch("/:id/status", requireAdmin(config.adminToken), async (req, res, next) => {
    try {
      const status = req.body.status;
      if (!ORDER_STATUSES.includes(status)) {
        res.status(400).json({ error: "UNKNOWN_STATUS", allowed: ORDER_STATUSES });
        return;
      }
      const result = await pool.query(
        "UPDATE orders SET status = $1 WHERE id::text = $2 OR number::text = $2 RETURNING id, number, status",
        [status, String(req.params.id)],
      );
      if (result.rowCount === 0) {
        res.status(404).json({ error: "ORDER_NOT_FOUND" });
        return;
      }
      res.json(result.rows[0]);
    } catch (error) {
      next(error);
    }
  });

  // Operator endpoint: list latest orders across all users.
  router.get("/all", requireAdmin(config.adminToken), async (_req, res, next) => {
    try {
      const result = await pool.query(
        `SELECT id, user_id, user_phone, number, status, address_text, total, created_at
         FROM orders ORDER BY created_at DESC LIMIT 100`,
      );
      res.json(result.rows);
    } catch (error) {
      next(error);
    }
  });

  return router;
}

async function loadOrders(pool, userId, onlyOrderId = null) {
  const result = await pool.query(
    `SELECT o.id, o.number, o.status, o.address_text, o.subtotal, o.delivery, o.total,
            o.created_at,
            COALESCE(json_agg(json_build_object(
              'productId', i.product_id,
              'name', i.product_name,
              'price', i.price,
              'qty', i.qty
            )) FILTER (WHERE i.order_id IS NOT NULL), '[]') AS items
     FROM orders o
     LEFT JOIN order_items i ON i.order_id = o.id
     WHERE o.user_id = $1 AND ($2::uuid IS NULL OR o.id = $2::uuid)
     GROUP BY o.id
     ORDER BY o.created_at DESC`,
    [userId, onlyOrderId],
  );

  return result.rows.map((row) => ({
    id: row.id,
    number: Number(row.number),
    status: row.status,
    addressText: row.address_text,
    subtotal: row.subtotal,
    delivery: row.delivery,
    total: row.total,
    itemsCount: row.items.reduce((sum, item) => sum + item.qty, 0),
    createdAtMillis: new Date(row.created_at).getTime(),
    items: row.items,
  }));
}

function parseItems(rawItems) {
  if (!Array.isArray(rawItems) || rawItems.length === 0 || rawItems.length > MAX_ORDER_POSITIONS) {
    return { error: "ITEMS_ARE_REQUIRED" };
  }
  const seen = new Set();
  const value = [];
  for (const raw of rawItems) {
    const productId = typeof raw?.productId === "string" ? raw.productId.trim() : "";
    const qty = raw?.qty;
    if (!productId || seen.has(productId)) return { error: "ITEMS_ARE_INVALID" };
    if (!Number.isInteger(qty) || qty < 1 || qty > MAX_ITEM_QTY) return { error: "ITEMS_ARE_INVALID" };
    seen.add(productId);
    value.push({ productId, qty });
  }
  return { value };
}

function requireAdmin(adminToken) {
  return function requireAdminToken(req, res, next) {
    if (!adminToken) {
      res.status(500).json({ error: "ORDERS_ADMIN_TOKEN_IS_MISSING" });
      return;
    }
    if (!timingSafeEqualStrings(req.get("x-orders-admin-token") || "", adminToken)) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }
    next();
  };
}

function timingSafeEqualStrings(left, right) {
  const leftHash = crypto.createHash("sha256").update(left).digest();
  const rightHash = crypto.createHash("sha256").update(right).digest();
  return crypto.timingSafeEqual(leftHash, rightHash);
}

module.exports = { createOrderRoutes };
