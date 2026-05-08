const crypto = require("crypto");
const express = require("express");
const { requireUser } = require("../security/userAuth");
const { httpError } = require("../utils/httpErrors");
const { optionalNumber, optionalString, requireString } = require("../utils/validation");

function createSupportRoutes({ store }) {
  const router = express.Router();

  router.use(requireUser);

  router.post("/conversation", (req, res, next) => {
    const conversation = store.getOrCreateConversation({
      userId: req.userId,
      userPublicKey: requireString(req.body.userPublicKey, "userPublicKey"),
    });
    if (!conversation) {
      next(httpError(409, "SUPPORT_KEY_NOT_REGISTERED"));
      return;
    }

    res.json({
      conversationId: conversation.id,
      supportPublicKey: conversation.supportPublicKey,
    });
  });

  router.get("/messages", (req, res, next) => {
    const conversation = store.findOwnedConversation(req.query.conversationId, req.userId);
    if (!conversation) {
      next(httpError(404, "CONVERSATION_NOT_FOUND"));
      return;
    }
    res.json(conversation.messages);
  });

  router.post("/messages", (req, res, next) => {
    const conversation = store.findOwnedConversation(req.body.conversationId, req.userId);
    if (!conversation) {
      next(httpError(404, "CONVERSATION_NOT_FOUND"));
      return;
    }

    const operatorKey = store.getOperatorKey();
    if (operatorKey && conversation.supportPublicKey !== operatorKey.publicKey) {
      next(httpError(409, "SUPPORT_KEY_CHANGED"));
      return;
    }

    const message = store.addMessage(conversation, {
      id: optionalString(req.body.messageId) || crypto.randomUUID(),
      conversationId: conversation.id,
      encryptedText: requireString(req.body.encryptedText, "encryptedText"),
      textIv: requireString(req.body.textIv, "textIv"),
      sender: "user",
      senderPublicKey: requireString(req.body.senderPublicKey, "senderPublicKey"),
      createdAtMillis: optionalNumber(req.body.createdAtMillis) || Date.now(),
    });

    res.status(201).json(message);
  });

  return router;
}

module.exports = { createSupportRoutes };
