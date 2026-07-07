const express = require("express");
const path = require("path");
const { createRateLimit } = require("./security/rateLimit");
const { createOrderRoutes } = require("./routes/orderRoutes");
const { SabyClient } = require("./saby/sabyClient");
const { startCatalogSync } = require("./saby/catalogSync");
const { startDisplayNameGenerator } = require("./naming/displayNames");

function createApp(config, pool, overrides = {}) {
  if (!config.jwtSecret) throw new Error("SUPPORT_JWT_SECRET is required");
  if (!config.databaseUrl) throw new Error("DATABASE_URL is required");

  const app = express();

  app.set("trust proxy", true);
  app.use(express.json({ limit: "64kb" }));
  app.use(createRateLimit(config.rateLimit));

  app.get(["/health", "/orders/health"], (_req, res) => {
    res.json({ ok: true, service: "gradka-orders" });
  });

  // The admin panel page itself is public static content; every API call
  // it makes is guarded by the X-Orders-Admin-Token header.
  const adminDir = path.join(__dirname, "..", "public", "admin");
  app.use("/orders/admin", express.static(adminDir));
  app.get("/orders/admin/", (_req, res) => {
    res.sendFile(path.join(adminDir, "index.html"));
  });

  app.use("/orders", createOrderRoutes({ config, pool }));

  app.use((err, _req, res, _next) => {
    console.error(err);
    const status = err.statusCode || 500;
    res.status(status).json({ error: err.statusCode ? err.message : "INTERNAL_ERROR" });
  });

  if (!overrides.skipNaming) {
    app.locals.displayNames = startDisplayNameGenerator({ pool, config: config.naming });
  }

  const sabyConfigured =
    config.saby.appClientId && config.saby.appSecret && config.saby.secretKey;
  if (sabyConfigured || overrides.saby) {
    const saby = overrides.saby || new SabyClient(config.saby);
    app.locals.catalogSync = startCatalogSync({
      saby,
      pool,
      intervalMillis: config.saby.syncIntervalMillis,
      pointId: config.saby.pointId,
      priceListId: config.saby.priceListId,
    });
  }

  return app;
}

module.exports = { createApp };
