const fs = require("fs");
const path = require("path");

class ChatStore {
  constructor(dataFile) {
    this.dataFile = dataFile;
    this.state = this.load();
  }

  getOperatorKey() {
    return this.state.operatorKey;
  }

  saveOperatorKey(operatorKey) {
    const previousKey = this.state.operatorKey?.publicKey;
    this.state.operatorKey = operatorKey;

    if (previousKey && previousKey !== operatorKey.publicKey) {
      this.state.conversations = this.state.conversations.map((conversation) => ({
        ...conversation,
        supportPublicKey: operatorKey.publicKey,
        messages: [],
        updatedAtMillis: Date.now(),
      }));
    }

    this.save();
    return this.state.operatorKey;
  }

  listConversations() {
    return [...this.state.conversations]
      .sort((left, right) => right.updatedAtMillis - left.updatedAtMillis)
      .map((conversation) => ({
        conversationId: conversation.id,
        userId: conversation.userId,
        userPublicKey: conversation.userPublicKey,
        supportPublicKey: conversation.supportPublicKey,
        createdAtMillis: conversation.createdAtMillis,
        updatedAtMillis: conversation.updatedAtMillis,
        messagesCount: conversation.messages.length,
        lastMessage: conversation.messages.at(-1) || null,
      }));
  }

  findConversation(conversationId) {
    if (!conversationId) return null;
    return this.state.conversations.find((conversation) => conversation.id === conversationId) || null;
  }

  findOwnedConversation(conversationId, userId) {
    const conversation = this.findConversation(conversationId);
    if (!conversation || conversation.userId !== userId) return null;
    return conversation;
  }

  getOrCreateConversation({ userId, userPublicKey }) {
    const operatorKey = this.getOperatorKey();
    if (!operatorKey) return null;

    let conversation = this.state.conversations.find((item) => item.userId === userId);
    const now = Date.now();

    if (!conversation) {
      conversation = {
        id: cryptoRandomId(),
        userId,
        userPublicKey,
        supportPublicKey: operatorKey.publicKey,
        messages: [],
        createdAtMillis: now,
        updatedAtMillis: now,
      };
      this.state.conversations.push(conversation);
    }

    if (
      conversation.userPublicKey !== userPublicKey ||
      conversation.supportPublicKey !== operatorKey.publicKey
    ) {
      conversation.userPublicKey = userPublicKey;
      conversation.supportPublicKey = operatorKey.publicKey;
      conversation.messages = [];
      conversation.updatedAtMillis = now;
    }

    this.save();
    return conversation;
  }

  addMessage(conversation, message) {
    conversation.messages.push(message);
    conversation.updatedAtMillis = Date.now();
    this.save();
    return message;
  }

  load() {
    try {
      const content = fs.readFileSync(this.dataFile, "utf8");
      const parsed = JSON.parse(content);
      return {
        operatorKey: parsed.operatorKey || null,
        conversations: Array.isArray(parsed.conversations) ? parsed.conversations : [],
      };
    } catch (_error) {
      return { operatorKey: null, conversations: [] };
    }
  }

  save() {
    fs.mkdirSync(path.dirname(this.dataFile), { recursive: true });
    fs.writeFileSync(this.dataFile, JSON.stringify(this.state, null, 2));
  }
}

function cryptoRandomId() {
  return require("crypto").randomUUID();
}

module.exports = { ChatStore };
