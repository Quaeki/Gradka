const path = require("path");

const dataDir = process.env.SUPPORT_CHAT_DATA_DIR || path.join(__dirname, "..", "data");

const config = {
  port: Number(process.env.PORT || 3001),
  host: process.env.HOST || "127.0.0.1",
  adminToken: process.env.SUPPORT_ADMIN_TOKEN || "",
  // Secret used by the auth backend to sign access tokens (HS256). Required.
  jwtSecret: process.env.SUPPORT_JWT_SECRET || "",
  // Comma-separated list of origins allowed to call the API from a browser.
  // Empty (default) disables CORS: the operator panel is served from the same origin.
  allowedOrigins: (process.env.SUPPORT_ALLOWED_ORIGINS || "")
    .split(",")
    .map((origin) => origin.trim())
    .filter(Boolean),
  rateLimit: {
    windowMillis: Number(process.env.SUPPORT_RATE_LIMIT_WINDOW_MS || 60_000),
    maxRequests: Number(process.env.SUPPORT_RATE_LIMIT_MAX_REQUESTS || 120),
  },
  maxMessagesPerConversation: Number(process.env.SUPPORT_MAX_MESSAGES_PER_CONVERSATION || 500),
  dataFile: path.join(dataDir, "support-chat.json"),
  operatorPublicDir: path.join(__dirname, "..", "public", "operator"),
};

module.exports = { config };
