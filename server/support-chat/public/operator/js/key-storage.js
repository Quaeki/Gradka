import { base64ToBytes, bytesToBase64, textBytes } from "./e2ee.js";

const STORE_PREFIX = "gradka_support_operator_";

export function restoreSettings() {
  return {
    apiBase: localStorage.getItem(STORE_PREFIX + "api_base") || window.location.origin,
    adminToken: localStorage.getItem(STORE_PREFIX + "admin_token") || "",
    operatorId: localStorage.getItem(STORE_PREFIX + "operator_id") || "operator-main",
    displayName: localStorage.getItem(STORE_PREFIX + "display_name") || "Поддержка",
  };
}

export function saveSettings(settings) {
  localStorage.setItem(STORE_PREFIX + "api_base", settings.apiBase);
  localStorage.setItem(STORE_PREFIX + "admin_token", settings.adminToken);
  localStorage.setItem(STORE_PREFIX + "operator_id", settings.operatorId);
  localStorage.setItem(STORE_PREFIX + "display_name", settings.displayName);
}

export async function saveOperatorIdentity({ publicKey, privateKeyBytes, passphrase }) {
  localStorage.setItem(STORE_PREFIX + "public_key", publicKey);
  localStorage.setItem(
    STORE_PREFIX + "private_key",
    JSON.stringify(await encryptPrivateKey(privateKeyBytes, passphrase)),
  );
}

export async function loadOperatorIdentity(passphrase) {
  const publicKey = localStorage.getItem(STORE_PREFIX + "public_key");
  const privateKeyBlob = localStorage.getItem(STORE_PREFIX + "private_key");
  if (!publicKey || !privateKeyBlob) {
    throw new Error("Локальный ключ не найден. Создайте ключ поддержки.");
  }

  return {
    publicKey,
    privateKeyBytes: await decryptPrivateKey(JSON.parse(privateKeyBlob), passphrase),
  };
}

async function encryptPrivateKey(privateKeyBytes, passphrase) {
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const iv = crypto.getRandomValues(new Uint8Array(12));
  const key = await derivePassphraseKey(passphrase, salt);
  const encrypted = await crypto.subtle.encrypt(
    { name: "AES-GCM", iv, tagLength: 128 },
    key,
    privateKeyBytes,
  );

  return {
    salt: bytesToBase64(salt),
    iv: bytesToBase64(iv),
    value: bytesToBase64(encrypted),
  };
}

async function decryptPrivateKey(blob, passphrase) {
  const key = await derivePassphraseKey(passphrase, base64ToBytes(blob.salt));
  return crypto.subtle.decrypt(
    { name: "AES-GCM", iv: base64ToBytes(blob.iv), tagLength: 128 },
    key,
    base64ToBytes(blob.value),
  );
}

async function derivePassphraseKey(passphrase, salt) {
  const baseKey = await crypto.subtle.importKey(
    "raw",
    textBytes(passphrase),
    "PBKDF2",
    false,
    ["deriveKey"],
  );
  return crypto.subtle.deriveKey(
    { name: "PBKDF2", salt, iterations: 210000, hash: "SHA-256" },
    baseKey,
    { name: "AES-GCM", length: 256 },
    false,
    ["encrypt", "decrypt"],
  );
}
