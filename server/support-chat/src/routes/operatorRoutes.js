const crypto = require("crypto");
const express = require("express");
const { createAdminAuth } = require("../security/adminAuth");
const { httpError } = require("../utils/httpErrors");
const { optionalString, requireString } = require("../utils/validation");

function createOperatorRoutes({ adminToken, store }) {
  const router = express.Router();
  const requireAdmin = createAdminAuth(adminToken);

  router.use(requireAdmin);

  router.post("/key", (req, res) => {
    const operatorKey = store.saveOperatorKey({
      operatorId: optionalString(req.body.operatorId) || "operator-main",
      displayName: optionalString(req.body.displayName) || "Поддержка",
      publicKey: requireString(req.body.publicKey, "publicKey"),
      updatedAtMillis: Date.now(),
    });

    res.json(operatorKey);
  });

  router.get("/key", (_req, res, next) => {
    const operatorKey = store.getOperatorKey();
    if (!operatorKey) {
      next(httpError(404, "SUPPORT_KEY_NOT_REGISTERED"));
      return;
    }
    res.json(operatorKey);
  });

  router.get("/conversations", (_req, res) => {
    res.json(store.listConversations());
  });

  router.get("/messages", (req, res, next) => {
    const conversation = store.findConversation(req.query.conversationId);
    if (!conversation) {
      next(httpError(404, "CONVERSATION_NOT_FOUND"));
      return;
    }
    res.json(conversation.messages);
  });

  router.post("/messages", (req, res, next) => {
    const conversation = store.findConversation(req.body.conversationId);
    if (!conversation) {
      next(httpError(404, "CONVERSATION_NOT_FOUND"));
      return;
    }

    const message = store.addMessage(conversation, {
      id: optionalString(req.body.messageId) || crypto.randomUUID(),
      conversationId: conversation.id,
      encryptedText: requireString(req.body.encryptedText, "encryptedText"),
      textIv: requireString(req.body.textIv, "textIv"),
      sender: "support",
      senderPublicKey: requireString(req.body.senderPublicKey, "senderPublicKey"),
      createdAtMillis: Date.now(),
    });

    res.status(201).json(message);
  });

  return router;
}

module.exports = { createOperatorRoutes };
