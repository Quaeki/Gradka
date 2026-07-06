// Sends the OTP code via sms.ru when SMS_RU_API_ID is configured;
// otherwise runs in dev mode and prints the code to the server log.
function createSmsSender(smsRuApiId) {
  if (!smsRuApiId) {
    return async function devSend(phone, code) {
      console.log(`[DEV SMS] OTP code for ${phone}: ${code}`);
    };
  }

  return async function smsRuSend(phone, code) {
    const url = new URL("https://sms.ru/sms/send");
    url.searchParams.set("api_id", smsRuApiId);
    url.searchParams.set("to", phone);
    url.searchParams.set("msg", `Код входа в Грядку: ${code}`);
    url.searchParams.set("json", "1");

    const response = await fetch(url);
    const body = await response.json();
    if (body.status !== "OK") {
      throw new Error(`SMS_SEND_FAILED: ${body.status_code || response.status}`);
    }
  };
}

module.exports = { createSmsSender };
