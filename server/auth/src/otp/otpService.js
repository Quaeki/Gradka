const crypto = require("crypto");

// OTP codes live in memory only: they are short-lived and must not survive
// in a data file. A service restart simply invalidates pending codes.
class OtpService {
  constructor({ codeLength, ttlMillis, resendCooldownSeconds, maxVerifyAttempts, sendSms }) {
    this.codeLength = codeLength;
    this.ttlMillis = ttlMillis;
    this.resendCooldownSeconds = resendCooldownSeconds;
    this.maxVerifyAttempts = maxVerifyAttempts;
    this.sendSms = sendSms;
    this.entries = new Map();

    setInterval(() => {
      const now = Date.now();
      for (const [phone, entry] of this.entries) {
        if (entry.expiresAtMillis <= now) this.entries.delete(phone);
      }
    }, ttlMillis).unref();
  }

  // Returns the number of seconds until the code can be requested again.
  async requestCode(phone) {
    const now = Date.now();
    const existing = this.entries.get(phone);
    if (existing && existing.resendAvailableAtMillis > now) {
      return Math.ceil((existing.resendAvailableAtMillis - now) / 1000);
    }

    const code = generateCode(this.codeLength);
    this.entries.set(phone, {
      codeHash: hashCode(code),
      expiresAtMillis: now + this.ttlMillis,
      resendAvailableAtMillis: now + this.resendCooldownSeconds * 1000,
      attempts: 0,
    });

    await this.sendSms(phone, code);
    return this.resendCooldownSeconds;
  }

  verifyCode(phone, code) {
    const entry = this.entries.get(phone);
    if (!entry || entry.expiresAtMillis <= Date.now()) return false;

    entry.attempts += 1;
    if (entry.attempts > this.maxVerifyAttempts) {
      this.entries.delete(phone);
      return false;
    }

    const matches = timingSafeEqualStrings(hashCode(String(code)), entry.codeHash);
    if (matches) {
      this.entries.delete(phone);
    }
    return matches;
  }
}

function generateCode(length) {
  return String(crypto.randomInt(0, 10 ** length)).padStart(length, "0");
}

function hashCode(code) {
  return crypto.createHash("sha256").update(code).digest("hex");
}

function timingSafeEqualStrings(left, right) {
  return crypto.timingSafeEqual(Buffer.from(left, "utf8"), Buffer.from(right, "utf8"));
}

module.exports = { OtpService };
