const { badRequest } = require("./httpErrors");

function requireString(value, name) {
  if (typeof value !== "string" || !value.trim()) {
    throw badRequest(`${name.toUpperCase()}_IS_REQUIRED`);
  }
  return value.trim();
}

function optionalString(value) {
  return typeof value === "string" && value.trim() ? value.trim() : null;
}

function optionalNumber(value) {
  return Number.isFinite(value) ? Number(value) : null;
}

module.exports = { optionalNumber, optionalString, requireString };
