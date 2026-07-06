// Delivers an OTP code with the following priority:
// 1. Telegram — when the user has linked their phone through the login bot;
// 2. sms.ru — when SMS_RU_API_ID is configured;
// 3. dev mode — the code is printed to the server log.
function createCodeSender({ userStore, telegramBot, smsRuApiId, log = console }) {
  return async function sendCode(phone, code) {
    if (telegramBot) {
      const chatId = userStore.getTelegramChatId(phone);
      if (chatId) {
        await telegramBot.sendMessage(chatId, `Код входа в Грядку: ${code}`);
        return;
      }
    }

    if (smsRuApiId) {
      await sendViaSmsRu(smsRuApiId, phone, code);
      return;
    }

    log.log(`[DEV SMS] OTP code for ${phone}: ${code}`);
  };
}

async function sendViaSmsRu(apiId, phone, code) {
  const url = new URL("https://sms.ru/sms/send");
  url.searchParams.set("api_id", apiId);
  url.searchParams.set("to", phone);
  url.searchParams.set("msg", `Код входа в Грядку: ${code}`);
  url.searchParams.set("json", "1");

  const response = await fetch(url);
  const body = await response.json();
  if (body.status !== "OK") {
    throw new Error(`SMS_SEND_FAILED: ${body.status_code || response.status}`);
  }
}

module.exports = { createCodeSender };
