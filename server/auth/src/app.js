const express = require("express");
const { createAuthRoutes } = require("./routes/authRoutes");
const { createRateLimit } = require("./security/rateLimit");
const { createCodeSender } = require("./otp/codeSender");
const { OtpService } = require("./otp/otpService");
const { UserStore } = require("./storage/userStore");
const { AuthTelegramBot, startContactPolling } = require("./telegram/telegramBot");

function createApp(config, overrides = {}) {
  if (!config.jwtSecret) {
    throw new Error("AUTH_JWT_SECRET (or SUPPORT_JWT_SECRET) is required");
  }

  const app = express();
  const userStore = new UserStore(config.dataFile);
  const telegramBot =
    overrides.telegramBot ||
    (config.telegramBotToken ? new AuthTelegramBot({ botToken: config.telegramBotToken }) : null);
  const otpService = new OtpService({
    ...config.otp,
    sendSms: createCodeSender({
      userStore,
      telegramBot,
      gatewayToken: config.telegramGatewayToken,
      gatewayApiBase: config.telegramGatewayApiBase,
      smsRuApiId: config.smsRuApiId,
    }),
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

  const stopPolling =
    telegramBot && !overrides.skipPolling
      ? startContactPolling({ bot: telegramBot, userStore })
      : () => {};
  app.locals.stopPolling = stopPolling;
  app.locals.userStore = userStore;

  return app;
}

module.exports = { createApp };
