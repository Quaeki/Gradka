// Fixed-window in-memory rate limiter keyed by client IP.
// Good enough for a single-instance service; swap for a shared store when scaling out.
function createRateLimit({ windowMillis, maxRequests }) {
  const hits = new Map();

  setInterval(() => {
    const now = Date.now();
    for (const [key, entry] of hits) {
      if (entry.resetAtMillis <= now) hits.delete(key);
    }
  }, windowMillis).unref();

  return function rateLimit(req, res, next) {
    const key = req.ip || req.socket.remoteAddress || "unknown";
    const now = Date.now();
    let entry = hits.get(key);

    if (!entry || entry.resetAtMillis <= now) {
      entry = { count: 0, resetAtMillis: now + windowMillis };
      hits.set(key, entry);
    }

    entry.count += 1;
    if (entry.count > maxRequests) {
      res.setHeader("Retry-After", Math.ceil((entry.resetAtMillis - now) / 1000));
      res.status(429).json({ error: "TOO_MANY_REQUESTS" });
      return;
    }

    next();
  };
}

module.exports = { createRateLimit };
