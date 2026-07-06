const express = require("express");
const { createUserAuth } = require("../security/userAuth");
const { httpError } = require("../utils/httpErrors");

function createSupportRoutes({ jwtSecret, maxMessageLength, store, telegram }) {
  const router = express.Router();

  router.use(createUserAuth(jwtSecret));

  router.get("/messages", (req, res) => {
    res.json(store.getMessages(req.userId));
  });

  router.post("/messages", async (req, res, next) => {
    try {
      const text = typeof req.body.text === "string" ? req.body.text.trim() : "";
      if (!text) {
        next(httpError(400, "TEXT_IS_REQUIRED"));
        return;
      }
      if (text.length > maxMessageLength) {
        next(httpError(400, "TEXT_IS_TOO_LONG"));
        return;
      }

      const messageId = typeof req.body.messageId === "string" ? req.body.messageId.trim() : "";
      const phone = req.userPhone || null;

      const telegramMessageId = await telegram.sendToOperator(
        `💬 ${phone || "Пользователь " + req.userId.slice(0, 8)}\n\n${text}`,
      );
      store.rememberTelegramMessage(telegramMessageId, req.userId);

      const message = store.addMessage(req.userId, {
        id: messageId || undefined,
        text,
        sender: "user",
        phone,
      });
      res.status(201).json(message);
    } catch (error) {
      next(httpError(502, "TELEGRAM_DELIVERY_FAILED"));
    }
  });

  router.post("/clear", (req, res) => {
    store.clearMessages(req.userId);
    res.json({ ok: true });
  });

  return router;
}

module.exports = { createSupportRoutes };
