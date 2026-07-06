const path = require("path");

const dataDir = process.env.SUPPORT_CHAT_DATA_DIR || path.join(__dirname, "..", "data");

const config = {
  port: Number(process.env.PORT || 3001),
  host: process.env.HOST || "127.0.0.1",
  // Shared HS256 secret: the auth service signs access tokens with the same value.
  jwtSecret: process.env.SUPPORT_JWT_SECRET || "",
  // Bot token from @BotFather.
  telegramBotToken: process.env.TELEGRAM_BOT_TOKEN || "",
  // Telegram chat id of the operator (get yours from @userinfobot).
  // Messages from any other chat are ignored.
  telegramChatId: process.env.TELEGRAM_CHAT_ID || "",
  rateLimit: {
    windowMillis: Number(process.env.SUPPORT_RATE_LIMIT_WINDOW_MS || 60_000),
    maxRequests: Number(process.env.SUPPORT_RATE_LIMIT_MAX_REQUESTS || 120),
  },
  maxMessageLength: Number(process.env.SUPPORT_MAX_MESSAGE_LENGTH || 1000),
  maxMessagesPerUser: Number(process.env.SUPPORT_MAX_MESSAGES_PER_USER || 500),
  dataFile: path.join(dataDir, "support-telegram.json"),
};

module.exports = { config };
