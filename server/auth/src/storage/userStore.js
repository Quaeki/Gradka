const crypto = require("crypto");
const fs = require("fs");
const path = require("path");

class UserStore {
  constructor(dataFile) {
    this.dataFile = dataFile;
    this.state = this.load();
  }

  findByPhone(phone) {
    return this.state.users.find((user) => user.phone === phone) || null;
  }

  findById(id) {
    return this.state.users.find((user) => user.id === id) || null;
  }

  // Returns { user, isNew }.
  findOrCreate(phone) {
    const existing = this.findByPhone(phone);
    if (existing) {
      return { user: existing, isNew: false };
    }

    const user = {
      id: crypto.randomUUID(),
      phone,
      name: null,
      createdAtMillis: Date.now(),
    };
    this.state.users.push(user);
    this.save();
    return { user, isNew: true };
  }

  updateName(id, name) {
    const user = this.findById(id);
    if (!user) return null;
    user.name = name;
    this.save();
    return user;
  }

  // Refresh tokens are stored hashed so a leaked data file does not leak usable tokens.
  saveRefreshToken(userId, refreshToken, expiresAtMillis) {
    this.state.refreshTokens = this.state.refreshTokens.filter(
      (entry) => entry.userId !== userId && entry.expiresAtMillis > Date.now(),
    );
    this.state.refreshTokens.push({
      userId,
      tokenHash: hashToken(refreshToken),
      expiresAtMillis,
    });
    this.save();
  }

  consumeRefreshToken(refreshToken) {
    const tokenHash = hashToken(refreshToken);
    const entry = this.state.refreshTokens.find(
      (item) => item.tokenHash === tokenHash && item.expiresAtMillis > Date.now(),
    );
    if (!entry) return null;

    this.state.refreshTokens = this.state.refreshTokens.filter((item) => item !== entry);
    this.save();
    return this.findById(entry.userId);
  }

  load() {
    try {
      const parsed = JSON.parse(fs.readFileSync(this.dataFile, "utf8"));
      return {
        users: Array.isArray(parsed.users) ? parsed.users : [],
        refreshTokens: Array.isArray(parsed.refreshTokens) ? parsed.refreshTokens : [],
      };
    } catch (_error) {
      return { users: [], refreshTokens: [] };
    }
  }

  save() {
    fs.mkdirSync(path.dirname(this.dataFile), { recursive: true });
    const tempFile = `${this.dataFile}.tmp`;
    fs.writeFileSync(tempFile, JSON.stringify(this.state, null, 2));
    fs.renameSync(tempFile, this.dataFile);
  }
}

function hashToken(token) {
  return crypto.createHash("sha256").update(token).digest("hex");
}

module.exports = { UserStore };
