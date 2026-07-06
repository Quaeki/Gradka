const config = {
  port: Number(process.env.PORT || 3003),
  host: process.env.HOST || "127.0.0.1",
  // Shared HS256 secret: the auth service signs access tokens with the same value.
  jwtSecret: process.env.SUPPORT_JWT_SECRET || "",
  // PostgreSQL connection string, e.g. postgres://gradka:secret@postgres:5432/gradka
  databaseUrl: process.env.DATABASE_URL || "",
  // Password for the admin panel and operator endpoints
  // (ORDERS_ADMIN_TOKEN is accepted as a legacy alias).
  adminPassword: process.env.ORDERS_ADMIN_PASSWORD || process.env.ORDERS_ADMIN_TOKEN || "",
  rateLimit: {
    windowMillis: Number(process.env.ORDERS_RATE_LIMIT_WINDOW_MS || 60_000),
    maxRequests: Number(process.env.ORDERS_RATE_LIMIT_MAX_REQUESTS || 120),
  },
  // Delivery pricing must match the in-app cart summary rules.
  freeDeliveryThreshold: Number(process.env.ORDERS_FREE_DELIVERY_THRESHOLD || 1500),
  deliveryPrice: Number(process.env.ORDERS_DELIVERY_PRICE || 149),
};

module.exports = { config };
