const express = require("express");
const path = require("path");
const { createRateLimit } = require("./security/rateLimit");
const { createOrderRoutes } = require("./routes/orderRoutes");

function createApp(config, pool) {
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

  return app;
}

module.exports = { createApp };
