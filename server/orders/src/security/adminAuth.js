const crypto = require("crypto");

const SESSION_COOKIE = "orders_admin_session";
const SESSION_TTL_MILLIS = 24 * 60 * 60 * 1000;
const LOGIN_WINDOW_MILLIS = 60_000;
const MAX_LOGIN_ATTEMPTS_PER_WINDOW = 5;

// Password login with server-side sessions, 3x-ui style: the panel posts the
// password once, gets an HttpOnly cookie, and every admin API call is checked
// against the in-memory session store. Sessions drop on service restart.
function createAdminAuth(password) {
  const sessions = new Map();
  const loginAttempts = new Map();

  setInterval(() => {
    const now = Date.now();
    for (const [sid, expiresAt] of sessions) {
      if (expiresAt <= now) sessions.delete(sid);
    }
    for (const [ip, entry] of loginAttempts) {
      if (entry.resetAtMillis <= now) loginAttempts.delete(ip);
    }
  }, 60_000).unref();

  function login(req, res) {
    if (!password) {
      res.status(500).json({ error: "ORDERS_ADMIN_PASSWORD_IS_MISSING" });
      return;
    }
    if (!allowLoginAttempt(req)) {
      res.status(429).json({ error: "TOO_MANY_ATTEMPTS" });
      return;
    }

    const provided = typeof req.body.password === "string" ? req.body.password : "";
    if (!timingSafeEqualStrings(provided, password)) {
      res.status(401).json({ error: "WRONG_PASSWORD" });
      return;
    }

    const sessionId = crypto.randomBytes(32).toString("hex");
    sessions.set(sessionId, Date.now() + SESSION_TTL_MILLIS);
    res.setHeader(
      "Set-Cookie",
      `${SESSION_COOKIE}=${sessionId}; HttpOnly; Path=/orders; SameSite=Lax; Max-Age=${SESSION_TTL_MILLIS / 1000}`,
    );
    res.json({ ok: true });
  }

  function logout(req, res) {
    const sessionId = readSessionCookie(req);
    if (sessionId) sessions.delete(sessionId);
    res.setHeader("Set-Cookie", `${SESSION_COOKIE}=; HttpOnly; Path=/orders; SameSite=Lax; Max-Age=0`);
    res.json({ ok: true });
  }

  function requireAdmin(req, res, next) {
    if (!password) {
      res.status(500).json({ error: "ORDERS_ADMIN_PASSWORD_IS_MISSING" });
      return;
    }

    const sessionId = readSessionCookie(req);
    const sessionValid = sessionId && (sessions.get(sessionId) || 0) > Date.now();
    // The password header keeps curl/scripts usable without a cookie jar.
    const headerValid = timingSafeEqualStrings(req.get("x-orders-admin-password") || "", password);
    if (!sessionValid && !headerValid) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }
    next();
  }

  function allowLoginAttempt(req) {
    const ip = req.ip || req.socket.remoteAddress || "unknown";
    const now = Date.now();
    let entry = loginAttempts.get(ip);
    if (!entry || entry.resetAtMillis <= now) {
      entry = { count: 0, resetAtMillis: now + LOGIN_WINDOW_MILLIS };
      loginAttempts.set(ip, entry);
    }
    entry.count += 1;
    return entry.count <= MAX_LOGIN_ATTEMPTS_PER_WINDOW;
  }

  return { login, logout, requireAdmin };
}

function readSessionCookie(req) {
  const header = req.get("cookie") || "";
  for (const part of header.split(";")) {
    const [name, ...rest] = part.trim().split("=");
    if (name === SESSION_COOKIE) return rest.join("=");
  }
  return null;
}

function timingSafeEqualStrings(left, right) {
  const leftHash = crypto.createHash("sha256").update(left).digest();
  const rightHash = crypto.createHash("sha256").update(right).digest();
  return crypto.timingSafeEqual(leftHash, rightHash);
}

module.exports = { createAdminAuth };
