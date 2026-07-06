const { Pool } = require("pg");
const { CATALOG } = require("./catalog");

function createPool(databaseUrl) {
  return new Pool({ connectionString: databaseUrl });
}

async function migrate(pool) {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS products (
      id    TEXT PRIMARY KEY,
      name  TEXT NOT NULL,
      price INTEGER NOT NULL CHECK (price >= 0)
    );

    CREATE SEQUENCE IF NOT EXISTS order_number_seq START 10000;

    CREATE TABLE IF NOT EXISTS orders (
      id           UUID PRIMARY KEY,
      user_id      TEXT NOT NULL,
      user_phone   TEXT,
      number       BIGINT NOT NULL DEFAULT nextval('order_number_seq'),
      status       TEXT NOT NULL DEFAULT 'created',
      address_text TEXT NOT NULL DEFAULT '',
      subtotal     INTEGER NOT NULL CHECK (subtotal >= 0),
      delivery     INTEGER NOT NULL CHECK (delivery >= 0),
      total        INTEGER NOT NULL CHECK (total >= 0),
      created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
    );

    CREATE INDEX IF NOT EXISTS orders_user_idx ON orders (user_id, created_at DESC);

    CREATE TABLE IF NOT EXISTS order_items (
      order_id     UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
      product_id   TEXT NOT NULL,
      product_name TEXT NOT NULL,
      price        INTEGER NOT NULL CHECK (price >= 0),
      qty          INTEGER NOT NULL CHECK (qty > 0),
      PRIMARY KEY (order_id, product_id)
    );
  `);

  // The app catalog is the source of truth for the demo: re-seed on every start
  // so price changes in catalog.js reach the database.
  for (const product of CATALOG) {
    await pool.query(
      `INSERT INTO products (id, name, price) VALUES ($1, $2, $3)
       ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price`,
      [product.id, product.name, product.price],
    );
  }
}

module.exports = { createPool, migrate };
