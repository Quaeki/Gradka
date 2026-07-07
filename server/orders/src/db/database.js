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

    ALTER TABLE products
      ADD COLUMN IF NOT EXISTS subtitle  TEXT,
      ADD COLUMN IF NOT EXISTS unit      TEXT,
      ADD COLUMN IF NOT EXISTS category  TEXT NOT NULL DEFAULT 'all',
      ADD COLUMN IF NOT EXISTS image_url TEXT,
      ADD COLUMN IF NOT EXISTS hue       REAL,
      ADD COLUMN IF NOT EXISTS badge     TEXT,
      ADD COLUMN IF NOT EXISTS farm      TEXT,
      ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;

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

  // The bundled catalog is the fallback until the Saby sync takes over:
  // seeding never reactivates products that a successful sync deactivated.
  for (const product of CATALOG) {
    await pool.query(
      `INSERT INTO products (id, name, price, subtitle, unit, category, hue, badge, farm)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
       ON CONFLICT (id) DO UPDATE SET
         name = EXCLUDED.name, price = EXCLUDED.price, subtitle = EXCLUDED.subtitle,
         unit = EXCLUDED.unit, category = EXCLUDED.category, hue = EXCLUDED.hue,
         badge = EXCLUDED.badge, farm = EXCLUDED.farm`,
      [
        product.id, product.name, product.price, product.subtitle, product.unit,
        product.category, product.hue, product.badge, product.farm,
      ],
    );
  }
}

module.exports = { createPool, migrate };
