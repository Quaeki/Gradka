// Minimal Telegram Bot API client: relays user messages to the operator chat
// and long-polls getUpdates for operator replies. Long polling works without
// a public HTTPS URL, unlike webhooks.
class TelegramClient {
  constructor({ botToken, chatId, apiBase }) {
    this.botToken = botToken;
    this.chatId = String(chatId);
    this.apiBase = apiBase || "https://api.telegram.org";
  }

  // Returns the Telegram message id of the relayed message.
  async sendToOperator(text) {
    const body = await this.call("sendMessage", {
      chat_id: this.chatId,
      text,
    });
    return body.result.message_id;
  }

  async getUpdates(offset) {
    const body = await this.call("getUpdates", {
      offset,
      timeout: 25,
      allowed_updates: ["message"],
    });
    return body.result;
  }

  async call(method, payload) {
    const response = await fetch(`${this.apiBase}/bot${this.botToken}/${method}`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify(payload),
    });
    const body = await response.json();
    if (!body.ok) {
      throw new Error(`TELEGRAM_${method.toUpperCase()}_FAILED: ${body.description || response.status}`);
    }
    return body;
  }
}

// Polls Telegram for operator replies and stores them as support messages.
// An operator must use Telegram's "Reply" on the relayed message so the
// service knows which app user the answer belongs to.
function startOperatorPolling({ telegram, store, log = console }) {
  let stopped = false;

  async function loop() {
    while (!stopped) {
      try {
        const updates = await telegram.getUpdates(store.getUpdateOffset());
        for (const update of updates) {
          store.setUpdateOffset(update.update_id + 1);
          handleUpdate(update);
        }
      } catch (error) {
        log.error(`Telegram polling error: ${error.message}`);
        await sleep(5000);
      }
    }
  }

  function handleUpdate(update) {
    const message = update.message;
    if (!message || String(message.chat?.id) !== telegram.chatId) return;
    if (!message.text) return;

    const repliedToId = message.reply_to_message?.message_id;
    const userId = repliedToId ? store.resolveTelegramMessage(repliedToId) : null;
    if (!userId) {
      telegram
        .sendToOperator("Чтобы ответить пользователю, используйте «Ответить» (Reply) на его сообщении.")
        .catch(() => {});
      return;
    }

    store.addMessage(userId, {
      text: message.text,
      sender: "support",
    });
  }

  loop();
  return () => {
    stopped = true;
  };
}

function sleep(millis) {
  return new Promise((resolve) => setTimeout(resolve, millis));
}

module.exports = { TelegramClient, startOperatorPolling };
