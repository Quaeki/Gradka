const crypto = require("crypto");

function requireUser(req, res, next) {
  const authorization = req.get("authorization") || "";
  const token = authorization.startsWith("Bearer ") ? authorization.slice("Bearer ".length).trim() : "";
  if (!token) {
    res.status(401).json({ error: "UNAUTHORIZED" });
    return;
  }

  req.userId = crypto.createHash("sha256").update(token).digest("hex");
  next();
}

module.exports = { requireUser };
