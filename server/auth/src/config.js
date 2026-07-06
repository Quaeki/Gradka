const path = require("path");

const dataDir = process.env.AUTH_DATA_DIR || path.join(__dirname, "..", "data");

const config = {
  port: Number(process.env.PORT || 3002),
  host: process.env.HOST || "127.0.0.1",
  // Shared HS256 secret: the support-chat service verifies tokens with the same value.
  jwtSecret: process.env.AUTH_JWT_SECRET || process.env.SUPPORT_JWT_SECRET || "",
  accessTokenTtlSeconds: Number(process.env.AUTH_ACCESS_TOKEN_TTL_SECONDS || 30 * 24 * 60 * 60),
  refreshTokenTtlSeconds: Number(process.env.AUTH_REFRESH_TOKEN_TTL_SECONDS || 90 * 24 * 60 * 60),
  otp: {
    codeLength: 6,
    ttlMillis: Number(process.env.AUTH_OTP_TTL_MS || 10 * 60 * 1000),
    resendCooldownSeconds: Number(process.env.AUTH_OTP_RESEND_COOLDOWN_SECONDS || 60),
    maxVerifyAttempts: Number(process.env.AUTH_OTP_MAX_VERIFY_ATTEMPTS || 5),
  },
  rateLimit: {
    windowMillis: Number(process.env.AUTH_RATE_LIMIT_WINDOW_MS || 60_000),
    maxRequests: Number(process.env.AUTH_RATE_LIMIT_MAX_REQUESTS || 30),
  },
  // Optional login bot (@BotFather) for delivering OTP codes via Telegram.
  // Must be a separate bot from the support-relay one: Telegram allows only
  // one getUpdates consumer per bot token.
  telegramBotToken: process.env.AUTH_TELEGRAM_BOT_TOKEN || "",
  // Optional sms.ru integration, used when the phone is not linked to Telegram.
  // With neither configured the service runs in dev mode and prints OTP codes
  // to the server log.
  smsRuApiId: process.env.SMS_RU_API_ID || "",
  dataFile: path.join(dataDir, "auth.json"),
};

module.exports = { config };
