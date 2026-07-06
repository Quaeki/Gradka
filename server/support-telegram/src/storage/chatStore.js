const crypto = require("crypto");
const fs = require("fs");
const path = require("path");

class ChatStore {
  constructor(dataFile, { maxMessagesPerUser }) {
    this.dataFile = dataFile;
    this.maxMessagesPerUser = maxMessagesPerUser;
    this.state = this.load();
  }

  getMessages(userId) {
    return this.state.users[userId]?.messages || [];
  }

  addMessage(userId, { id, text, sender, phone }) {
    const user = this.state.users[userId] || { phone: phone || null, messages: [] };
    this.state.users[userId] = user;
    if (phone) user.phone = phone;

    const existing = user.messages.find((message) => message.id === id);
    if (existing) return existing;

    const message = {
      id: id || crypto.randomUUID(),
      text,
      sender,
      createdAtMillis: Date.now(),
    };
    user.messages.push(message);
    if (user.messages.length > this.maxMessagesPerUser) {
      user.messages = user.messages.slice(-this.maxMessagesPerUser);
    }
    this.save();
    return message;
  }

  clearMessages(userId) {
    const user = this.state.users[userId];
    if (!user) return;
    user.messages = [];
    this.save();
  }

  // Maps a Telegram message id (the relayed notification) back to the app user,
  // so an operator reply in Telegram can be routed to the right chat.
  rememberTelegramMessage(telegramMessageId, userId) {
    this.state.telegramMessages[String(telegramMessageId)] = userId;
    const ids = Object.keys(this.state.telegramMessages);
    if (ids.length > MAX_TELEGRAM_MESSAGE_MAPPINGS) {
      for (const staleId of ids.slice(0, ids.length - MAX_TELEGRAM_MESSAGE_MAPPINGS)) {
        delete this.state.telegramMessages[staleId];
      }
    }
    this.save();
  }

  resolveTelegramMessage(telegramMessageId) {
    return this.state.telegramMessages[String(telegramMessageId)] || null;
  }

  getUpdateOffset() {
    return this.state.updateOffset;
  }

  setUpdateOffset(offset) {
    this.state.updateOffset = offset;
    this.save();
  }

  load() {
    try {
      const parsed = JSON.parse(fs.readFileSync(this.dataFile, "utf8"));
      return {
        users: parsed.users && typeof parsed.users === "object" ? parsed.users : {},
        telegramMessages:
          parsed.telegramMessages && typeof parsed.telegramMessages === "object"
            ? parsed.telegramMessages
            : {},
        updateOffset: Number.isFinite(parsed.updateOffset) ? parsed.updateOffset : 0,
      };
    } catch (_error) {
      return { users: {}, telegramMessages: {}, updateOffset: 0 };
    }
  }

  save() {
    fs.mkdirSync(path.dirname(this.dataFile), { recursive: true });
    // Write to a temp file and rename so a crash mid-write never corrupts the data file.
    const tempFile = `${this.dataFile}.tmp`;
    fs.writeFileSync(tempFile, JSON.stringify(this.state, null, 2));
    fs.renameSync(tempFile, this.dataFile);
  }
}

const MAX_TELEGRAM_MESSAGE_MAPPINGS = 2000;

module.exports = { ChatStore };
