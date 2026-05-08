function createAdminAuth(adminToken) {
  return function requireAdmin(req, res, next) {
    if (!adminToken) {
      res.status(500).json({ error: "SUPPORT_ADMIN_TOKEN_IS_MISSING" });
      return;
    }

    if (req.get("x-support-admin-token") !== adminToken) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }

    next();
  };
}

module.exports = { createAdminAuth };
