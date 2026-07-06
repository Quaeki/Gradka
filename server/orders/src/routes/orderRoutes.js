const crypto = require("crypto");
const express = require("express");
const { createAdminAuth } = require("../security/adminAuth");
const { createUserAuth } = require("../security/userAuth");

const ORDER_STATUSES = ["created", "confirmed", "delivering", "delivered", "cancelled"];
const MAX_ORDER_POSITIONS = 50;
const MAX_ITEM_QTY = 99;
const MAX_ADDRESS_LENGTH = 300;

function createOrderRoutes({ config, pool }) {
  const router = express.Router();
  const adminAuth = createAdminAuth(config.adminPassword);

  router.post("/admin/login", adminAuth.login);
  router.post("/admin/logout", adminAuth.logout);

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
  // curl -X PATCH .../orders/<id>/status -H 'X-Orders-Admin-Password: ...' -d '{"status":"delivering"}'
  router.patch("/:id/status", adminAuth.requireAdmin, async (req, res, next) => {
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
  // Optional ?q= searches by order number, phone, address, and status.
  router.get("/all", adminAuth.requireAdmin, async (req, res, next) => {
    try {
      const query = typeof req.query.q === "string" ? req.query.q.trim().slice(0, 100) : "";
      const result = await pool.query(
        `SELECT o.id, o.user_phone, o.number, o.status, o.address_text,
                o.subtotal, o.delivery, o.total, o.created_at,
                COALESCE(json_agg(json_build_object(
                  'name', i.product_name,
                  'price', i.price,
                  'qty', i.qty
                )) FILTER (WHERE i.order_id IS NOT NULL), '[]') AS items
         FROM orders o
         LEFT JOIN order_items i ON i.order_id = o.id
         WHERE $1 = ''
            OR o.number::text ILIKE '%' || $1 || '%'
            OR o.user_phone ILIKE '%' || $1 || '%'
            OR o.address_text ILIKE '%' || $1 || '%'
            OR o.status ILIKE '%' || $1 || '%'
         GROUP BY o.id
         ORDER BY o.created_at DESC
         LIMIT 100`,
        [query],
      );
      res.json(result.rows.map((row) => ({
        id: row.id,
        number: Number(row.number),
        phone: row.user_phone,
        status: row.status,
        addressText: row.address_text,
        subtotal: row.subtotal,
        delivery: row.delivery,
        total: row.total,
        createdAtMillis: new Date(row.created_at).getTime(),
        items: row.items,
      })));
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

module.exports = { createOrderRoutes };
