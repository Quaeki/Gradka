// Delivers an OTP code with the following priority:
// 1. Telegram Gateway — official verification-code service (gateway.telegram.org),
//    delivers to any phone number that has a Telegram account, no bot needed;
// 2. Telegram bot — when the user has linked their phone through the login bot;
// 3. sms.ru — when SMS_RU_API_ID is configured;
// 4. dev mode — the code is printed to the server log.
// Each tier falls through to the next one on failure.
function createCodeSender({ userStore, telegramBot, gatewayToken, gatewayApiBase, smsRuApiId, log = console }) {
  return async function sendCode(phone, code) {
    if (gatewayToken) {
      try {
        await sendViaGateway({ gatewayToken, gatewayApiBase, phone, code });
        return;
      } catch (error) {
        log.error(`Telegram Gateway delivery failed for ${phone}: ${error.message}`);
      }
    }

    if (telegramBot) {
      const chatId = userStore.getTelegramChatId(phone);
      if (chatId) {
        try {
          await telegramBot.sendMessage(chatId, `Код входа в Грядку: ${code}`);
          return;
        } catch (error) {
          log.error(`Telegram bot delivery failed for ${phone}: ${error.message}`);
        }
      }
    }

    if (smsRuApiId) {
      await sendViaSmsRu(smsRuApiId, phone, code);
      return;
    }

    log.log(`[DEV SMS] OTP code for ${phone}: ${code}`);
  };
}

async function sendViaGateway({ gatewayToken, gatewayApiBase, phone, code }) {
  const response = await fetch(`${gatewayApiBase || "https://gatewayapi.telegram.org"}/sendVerificationMessage`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
      authorization: `Bearer ${gatewayToken}`,
    },
    body: JSON.stringify({
      phone_number: phone,
      code,
      ttl: 600,
    }),
  });
  const body = await response.json();
  if (!body.ok) {
    throw new Error(`GATEWAY_SEND_FAILED: ${body.error || response.status}`);
  }
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
