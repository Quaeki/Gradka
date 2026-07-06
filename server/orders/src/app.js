const express = require("express");
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

  app.use("/orders", createOrderRoutes({ config, pool }));

  app.use((err, _req, res, _next) => {
    console.error(err);
    const status = err.statusCode || 500;
    res.status(status).json({ error: err.statusCode ? err.message : "INTERNAL_ERROR" });
  });

  return app;
}

module.exports = { createApp };
