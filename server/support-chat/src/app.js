const express = require("express");
const path = require("path");
const { createOperatorRoutes } = require("./routes/operatorRoutes");
const { createSupportRoutes } = require("./routes/supportRoutes");
const { createRateLimit } = require("./security/rateLimit");
const { ChatStore } = require("./storage/chatStore");

function createApp(config) {
  const app = express();
  const store = new ChatStore(config.dataFile, {
    maxMessagesPerConversation: config.maxMessagesPerConversation,
  });

  app.set("trust proxy", true);
  app.use(express.json({ limit: "256kb" }));
  app.use(createCors(config.allowedOrigins));
  app.use(createRateLimit(config.rateLimit));

  app.get(["/health", "/support/health"], (_req, res) => {
    res.json({ ok: true, service: "gradka-support-chat" });
  });

  app.use(
    "/operator/api",
    createOperatorRoutes({
      adminToken: config.adminToken,
      store,
    }),
  );
  app.use("/support/chat", createSupportRoutes({ jwtSecret: config.jwtSecret, store }));

  app.use("/operator", express.static(config.operatorPublicDir));
  app.get("/operator/", (_req, res) => {
    res.sendFile(path.join(config.operatorPublicDir, "index.html"));
  });

  app.use(errorHandler);

  return app;
}

function createCors(allowedOrigins) {
  return function cors(req, res, next) {
    const origin = req.get("origin");
    if (origin && allowedOrigins.includes(origin)) {
      res.setHeader("Access-Control-Allow-Origin", origin);
      res.setHeader("Vary", "Origin");
      res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Support-Admin-Token");
      res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }
    if (req.method === "OPTIONS") {
      res.sendStatus(204);
      return;
    }
    next();
  };
}

function errorHandler(err, _req, res, _next) {
  const status = err.statusCode || 400;
  res.status(status).json({ error: err.message || "BAD_REQUEST" });
}

module.exports = { createApp };
