const path = require("path");

const dataDir = process.env.SUPPORT_CHAT_DATA_DIR || path.join(__dirname, "..", "data");

const config = {
  port: Number(process.env.PORT || 3001),
  host: process.env.HOST || "127.0.0.1",
  adminToken: process.env.SUPPORT_ADMIN_TOKEN || "",
  dataFile: path.join(dataDir, "support-chat.json"),
  operatorPublicDir: path.join(__dirname, "..", "public", "operator"),
};

module.exports = { config };
