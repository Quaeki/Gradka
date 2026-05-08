const HKDF_INFO = "gradka-support-chat-e2ee-v1";

export function ensureCryptoAvailable() {
  if (!window.crypto?.subtle || !window.isSecureContext) {
    throw new Error("Откройте панель через http://127.0.0.1 или HTTPS, иначе WebCrypto недоступен.");
  }
}

export async function createOperatorIdentity() {
  ensureCryptoAvailable();
  const keyPair = await crypto.subtle.generateKey(
    { name: "ECDH", namedCurve: "P-256" },
    true,
    ["deriveBits"],
  );
  const publicKeyBytes = await crypto.subtle.exportKey("spki", keyPair.publicKey);
  const privateKeyBytes = await crypto.subtle.exportKey("pkcs8", keyPair.privateKey);

  return {
    privateKey: keyPair.privateKey,
    privateKeyBytes,
    publicKey: bytesToBase64(publicKeyBytes),
  };
}

export function importOperatorPrivateKey(privateKeyBytes) {
  ensureCryptoAvailable();
  return crypto.subtle.importKey(
    "pkcs8",
    privateKeyBytes,
    { name: "ECDH", namedCurve: "P-256" },
    false,
    ["deriveBits"],
  );
}

export async function encryptMessage({ privateKey, plainText, peerPublicKey, conversationId }) {
  const iv = crypto.getRandomValues(new Uint8Array(12));
  const key = await deriveMessageKey({ privateKey, peerPublicKey, conversationId });
  const encrypted = await crypto.subtle.encrypt(
    {
      name: "AES-GCM",
      iv,
      additionalData: textBytes(conversationId),
      tagLength: 128,
    },
    key,
    textBytes(plainText),
  );

  return {
    value: bytesToBase64(encrypted),
    iv: bytesToBase64(iv),
  };
}

export async function decryptMessage({ privateKey, message, peerPublicKey }) {
  const key = await deriveMessageKey({
    privateKey,
    peerPublicKey,
    conversationId: message.conversationId,
  });
  const plainBytes = await crypto.subtle.decrypt(
    {
      name: "AES-GCM",
      iv: base64ToBytes(message.textIv),
      additionalData: textBytes(message.conversationId),
      tagLength: 128,
    },
    key,
    base64ToBytes(message.encryptedText),
  );
  return new TextDecoder().decode(plainBytes);
}

async function deriveMessageKey({ privateKey, peerPublicKey, conversationId }) {
  const peerKey = await crypto.subtle.importKey(
    "spki",
    base64ToBytes(peerPublicKey),
    { name: "ECDH", namedCurve: "P-256" },
    false,
    [],
  );
  const sharedSecret = await crypto.subtle.deriveBits(
    { name: "ECDH", public: peerKey },
    privateKey,
    256,
  );
  const hkdfKey = await crypto.subtle.importKey("raw", sharedSecret, "HKDF", false, ["deriveKey"]);
  return crypto.subtle.deriveKey(
    {
      name: "HKDF",
      hash: "SHA-256",
      salt: textBytes(conversationId),
      info: textBytes(HKDF_INFO),
    },
    hkdfKey,
    { name: "AES-GCM", length: 256 },
    false,
    ["encrypt", "decrypt"],
  );
}

export function textBytes(value) {
  return new TextEncoder().encode(value);
}

export function bytesToBase64(value) {
  const bytes = value instanceof Uint8Array ? value : new Uint8Array(value);
  let binary = "";
  for (const byte of bytes) {
    binary += String.fromCharCode(byte);
  }
  return btoa(binary);
}

export function base64ToBytes(value) {
  const binary = atob(value);
  const bytes = new Uint8Array(binary.length);
  for (let index = 0; index < binary.length; index += 1) {
    bytes[index] = binary.charCodeAt(index);
  }
  return bytes;
}
