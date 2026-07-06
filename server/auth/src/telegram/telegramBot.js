// Login bot: users open it, press Start, and share their phone number once.
// After that OTP codes are delivered to their Telegram instead of SMS.
// Uses long polling, so no public webhook URL is required.
//
// Must be a SEPARATE bot from the support-relay one: Telegram allows only
// one getUpdates consumer per bot token.
class AuthTelegramBot {
  constructor({ botToken, apiBase }) {
    this.botToken = botToken;
    this.apiBase = apiBase || "https://api.telegram.org";
  }

  async sendMessage(chatId, text, extra = {}) {
    await this.call("sendMessage", { chat_id: chatId, text, ...extra });
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

function startContactPolling({ bot, userStore, log = console }) {
  let stopped = false;

  async function loop() {
    while (!stopped) {
      try {
        const updates = await bot.getUpdates(userStore.getTelegramUpdateOffset());
        for (const update of updates) {
          userStore.setTelegramUpdateOffset(update.update_id + 1);
          await handleUpdate(update);
        }
      } catch (error) {
        log.error(`Auth bot polling error: ${error.message}`);
        await sleep(5000);
      }
    }
  }

  async function handleUpdate(update) {
    const message = update.message;
    if (!message || !message.chat) return;

    const contact = message.contact;
    // Accept only the sender's own contact: request_contact buttons always send
    // the user's verified number, and this check rejects forwarded third-party contacts.
    if (contact && contact.user_id === message.from?.id) {
      const phone = normalizePhone(contact.phone_number);
      if (!phone) {
        await reply(message, "Не удалось распознать номер. Попробуйте ещё раз.");
        return;
      }
      userStore.linkTelegram(phone, message.chat.id);
      await reply(
        message,
        `✅ Номер ${phone} привязан. Теперь коды входа будут приходить сюда.`,
        { reply_markup: { remove_keyboard: true } },
      );
      return;
    }

    await reply(message, "Чтобы получать коды входа в Telegram, поделитесь своим номером телефона:", {
      reply_markup: {
        keyboard: [[{ text: "📱 Отправить мой номер", request_contact: true }]],
        resize_keyboard: true,
        one_time_keyboard: true,
      },
    });
  }

  async function reply(message, text, extra) {
    try {
      await bot.sendMessage(message.chat.id, text, extra);
    } catch (error) {
      log.error(`Auth bot reply error: ${error.message}`);
    }
  }

  loop();
  return () => {
    stopped = true;
  };
}

// Telegram sends contact numbers like "79991234567" or "+79991234567".
function normalizePhone(rawPhone) {
  const digits = String(rawPhone || "").replace(/\D/g, "");
  if (digits.length === 11 && (digits.startsWith("7") || digits.startsWith("8"))) {
    return `+7${digits.slice(1)}`;
  }
  if (digits.length >= 10 && digits.length <= 15) {
    return `+${digits}`;
  }
  return null;
}

function sleep(millis) {
  return new Promise((resolve) => setTimeout(resolve, millis));
}

module.exports = { AuthTelegramBot, startContactPolling, normalizePhone };
