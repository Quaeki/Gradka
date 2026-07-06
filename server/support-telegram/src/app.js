const express = require("express");
const { createSupportRoutes } = require("./routes/supportRoutes");
const { createRateLimit } = require("./security/rateLimit");
const { ChatStore } = require("./storage/chatStore");
const { TelegramClient, startOperatorPolling } = require("./telegram/telegramClient");

function createApp(config, overrides = {}) {
  if (!config.jwtSecret) throw new Error("SUPPORT_JWT_SECRET is required");
  if (!config.telegramBotToken) throw new Error("TELEGRAM_BOT_TOKEN is required");
  if (!config.telegramChatId) throw new Error("TELEGRAM_CHAT_ID is required");

  const app = express();
  const store = new ChatStore(config.dataFile, {
    maxMessagesPerUser: config.maxMessagesPerUser,
  });
  const telegram =
    overrides.telegram ||
    new TelegramClient({
      botToken: config.telegramBotToken,
      chatId: config.telegramChatId,
    });

  app.set("trust proxy", true);
  app.use(express.json({ limit: "64kb" }));
  app.use(createRateLimit(config.rateLimit));

  app.get(["/health", "/support/health"], (_req, res) => {
    res.json({ ok: true, service: "gradka-support-telegram" });
  });

  app.use(
    "/support/chat",
    createSupportRoutes({
      jwtSecret: config.jwtSecret,
      maxMessageLength: config.maxMessageLength,
      store,
      telegram,
    }),
  );

  app.use((err, _req, res, _next) => {
    const status = err.statusCode || 500;
    res.status(status).json({ error: err.message || "INTERNAL_ERROR" });
  });

  const stopPolling = overrides.skipPolling ? () => {} : startOperatorPolling({ telegram, store });
  app.locals.stopPolling = stopPolling;
  app.locals.store = store;

  return app;
}

module.exports = { createApp };
