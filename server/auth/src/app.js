const express = require("express");
const { createAuthRoutes } = require("./routes/authRoutes");
const { createRateLimit } = require("./security/rateLimit");
const { OtpService } = require("./otp/otpService");
const { createSmsSender } = require("./otp/smsSender");
const { UserStore } = require("./storage/userStore");

function createApp(config) {
  if (!config.jwtSecret) {
    throw new Error("AUTH_JWT_SECRET (or SUPPORT_JWT_SECRET) is required");
  }

  const app = express();
  const userStore = new UserStore(config.dataFile);
  const otpService = new OtpService({
    ...config.otp,
    sendSms: createSmsSender(config.smsRuApiId),
  });

  app.set("trust proxy", true);
  app.use(express.json({ limit: "16kb" }));
  app.use(createRateLimit(config.rateLimit));

  app.get(["/health", "/auth/health"], (_req, res) => {
    res.json({ ok: true, service: "gradka-auth" });
  });

  app.use("/auth", createAuthRoutes({ config, otpService, userStore }));

  app.use((err, _req, res, _next) => {
    const status = err.statusCode || 500;
    res.status(status).json({ error: err.message || "INTERNAL_ERROR" });
  });

  return app;
}

module.exports = { createApp };
