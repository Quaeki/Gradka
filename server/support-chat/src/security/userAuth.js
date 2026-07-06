const crypto = require("crypto");

function createUserAuth(jwtSecret) {
  return function requireUser(req, res, next) {
    if (!jwtSecret) {
      res.status(500).json({ error: "SUPPORT_JWT_SECRET_IS_MISSING" });
      return;
    }

    const authorization = req.get("authorization") || "";
    const token = authorization.startsWith("Bearer ") ? authorization.slice("Bearer ".length).trim() : "";
    const payload = token ? verifyHs256Jwt(token, jwtSecret) : null;
    const userId = payload && (payload.sub || payload.userId || payload.id);
    if (!userId || typeof userId !== "string") {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }

    req.userId = userId;
    next();
  };
}

function verifyHs256Jwt(token, secret) {
  const parts = token.split(".");
  if (parts.length !== 3) return null;

  const [encodedHeader, encodedPayload, encodedSignature] = parts;

  let header;
  let payload;
  try {
    header = JSON.parse(Buffer.from(encodedHeader, "base64url").toString("utf8"));
    payload = JSON.parse(Buffer.from(encodedPayload, "base64url").toString("utf8"));
  } catch (_error) {
    return null;
  }

  // Pin the algorithm: never trust header.alg beyond an exact match.
  if (header.alg !== "HS256" || (header.typ && header.typ !== "JWT")) return null;

  const expectedSignature = crypto
    .createHmac("sha256", secret)
    .update(`${encodedHeader}.${encodedPayload}`)
    .digest();
  const actualSignature = Buffer.from(encodedSignature, "base64url");
  if (
    actualSignature.length !== expectedSignature.length ||
    !crypto.timingSafeEqual(actualSignature, expectedSignature)
  ) {
    return null;
  }

  const nowSeconds = Math.floor(Date.now() / 1000);
  if (typeof payload.exp === "number" && payload.exp <= nowSeconds) return null;
  if (typeof payload.nbf === "number" && payload.nbf > nowSeconds) return null;

  return payload;
}

module.exports = { createUserAuth };
