const crypto = require("crypto");

function createAdminAuth(adminToken) {
  return function requireAdmin(req, res, next) {
    if (!adminToken) {
      res.status(500).json({ error: "SUPPORT_ADMIN_TOKEN_IS_MISSING" });
      return;
    }

    if (!timingSafeEqualStrings(req.get("x-support-admin-token") || "", adminToken)) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }

    next();
  };
}

// Hash both sides so the comparison stays constant-time even for different lengths.
function timingSafeEqualStrings(left, right) {
  const leftHash = crypto.createHash("sha256").update(left).digest();
  const rightHash = crypto.createHash("sha256").update(right).digest();
  return crypto.timingSafeEqual(leftHash, rightHash);
}

module.exports = { createAdminAuth };
