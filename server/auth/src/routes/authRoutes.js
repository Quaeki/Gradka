const crypto = require("crypto");
const express = require("express");
const { signHs256Jwt, verifyHs256Jwt } = require("../security/jwt");

const PHONE_PATTERN = /^\+7\d{10}$/;

function createAuthRoutes({ config, otpService, userStore }) {
  const router = express.Router();

  router.post("/send-code", async (req, res, next) => {
    try {
      const phone = requirePhone(req.body.phone);
      const retryAfterSeconds = await otpService.requestCode(phone);
      res.json({ retryAfterSeconds });
    } catch (error) {
      next(error);
    }
  });

  router.post("/verify-code", (req, res, next) => {
    try {
      const phone = requirePhone(req.body.phone);
      const code = typeof req.body.code === "string" ? req.body.code.trim() : "";
      if (!code || !otpService.verifyCode(phone, code)) {
        res.status(401).json({ error: "INVALID_CODE" });
        return;
      }

      const { user, isNew } = userStore.findOrCreate(phone);
      res.json({
        ...issueTokens(user),
        user: toUserDto(user, isNew),
      });
    } catch (error) {
      next(error);
    }
  });

  router.post("/refresh", (req, res) => {
    const refreshToken = typeof req.body.refreshToken === "string" ? req.body.refreshToken.trim() : "";
    const user = refreshToken ? userStore.consumeRefreshToken(refreshToken) : null;
    if (!user) {
      res.status(401).json({ error: "INVALID_REFRESH_TOKEN" });
      return;
    }

    res.json({
      ...issueTokens(user),
      user: toUserDto(user, false),
    });
  });

  router.post("/update-name", (req, res) => {
    const payload = bearerPayload(req);
    const name = typeof req.body.name === "string" ? req.body.name.trim() : "";
    if (!payload) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }
    if (!name || name.length > 100) {
      res.status(400).json({ error: "NAME_IS_REQUIRED" });
      return;
    }

    const user = userStore.updateName(payload.sub, name);
    if (!user) {
      res.status(401).json({ error: "UNAUTHORIZED" });
      return;
    }

    res.json(toUserDto(user, false));
  });

  function issueTokens(user) {
    const accessToken = signHs256Jwt(
      { sub: user.id, phone: user.phone },
      config.jwtSecret,
      config.accessTokenTtlSeconds,
    );
    const refreshToken = crypto.randomBytes(48).toString("base64url");
    userStore.saveRefreshToken(
      user.id,
      refreshToken,
      Date.now() + config.refreshTokenTtlSeconds * 1000,
    );
    return { accessToken, refreshToken };
  }

  function bearerPayload(req) {
    const authorization = req.get("authorization") || "";
    const token = authorization.startsWith("Bearer ") ? authorization.slice("Bearer ".length).trim() : "";
    return token ? verifyHs256Jwt(token, config.jwtSecret) : null;
  }

  return router;
}

function requirePhone(value) {
  const phone = typeof value === "string" ? value.trim() : "";
  if (!PHONE_PATTERN.test(phone)) {
    const error = new Error("PHONE_IS_INVALID");
    error.statusCode = 400;
    throw error;
  }
  return phone;
}

function toUserDto(user, isNew) {
  return {
    id: user.id,
    phone: user.phone,
    name: user.name,
    isNew,
  };
}

module.exports = { createAuthRoutes };
