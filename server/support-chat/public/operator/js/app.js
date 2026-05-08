import { SupportChatApi } from "./api.js";
import {
  createOperatorIdentity,
  decryptMessage,
  encryptMessage,
  ensureCryptoAvailable,
  importOperatorPrivateKey,
} from "./e2ee.js";
import {
  loadOperatorIdentity,
  restoreSettings,
  saveOperatorIdentity,
  saveSettings,
} from "./key-storage.js";

const state = {
  privateKey: null,
  publicKey: null,
  conversations: [],
  selectedConversationId: null,
  loadingConversations: false,
  loadingMessages: false,
};

const els = {
  apiBase: document.getElementById("apiBase"),
  adminToken: document.getElementById("adminToken"),
  operatorId: document.getElementById("operatorId"),
  displayName: document.getElementById("displayName"),
  passphrase: document.getElementById("passphrase"),
  loadKeyBtn: document.getElementById("loadKeyBtn"),
  createKeyBtn: document.getElementById("createKeyBtn"),
  status: document.getElementById("status"),
  conversations: document.getElementById("conversations"),
  chatTitle: document.getElementById("chatTitle"),
  chatMeta: document.getElementById("chatMeta"),
  messages: document.getElementById("messages"),
  replyInput: document.getElementById("replyInput"),
  sendBtn: document.getElementById("sendBtn"),
};

const api = new SupportChatApi({
  getAdminToken: () => els.adminToken.value.trim(),
  getBaseUrl: () => els.apiBase.value.trim(),
});

init();

function init() {
  applySettings(restoreSettings());
  bindEvents();
  updateSendState();
  setInterval(loadConversations, 5000);
  setInterval(() => {
    if (state.selectedConversationId) loadMessages(state.selectedConversationId);
  }, 3000);
}

function bindEvents() {
  els.createKeyBtn.addEventListener("click", createAndRegisterKey);
  els.loadKeyBtn.addEventListener("click", loadPrivateKey);
  els.sendBtn.addEventListener("click", sendReply);
  els.replyInput.addEventListener("input", updateSendState);
  [els.apiBase, els.adminToken, els.operatorId, els.displayName].forEach((input) => {
    input.addEventListener("change", persistSettings);
  });
}

function applySettings(settings) {
  els.apiBase.value = settings.apiBase;
  els.adminToken.value = settings.adminToken;
  els.operatorId.value = settings.operatorId;
  els.displayName.value = settings.displayName;
}

function persistSettings() {
  saveSettings({
    apiBase: els.apiBase.value.trim(),
    adminToken: els.adminToken.value.trim(),
    operatorId: els.operatorId.value.trim(),
    displayName: els.displayName.value.trim(),
  });
}

async function createAndRegisterKey() {
  try {
    ensureCryptoAvailable();
    persistSettings();
    const passphrase = readPassphrase();
    setStatus("Создаю ключ...");

    const identity = await createOperatorIdentity();
    await saveOperatorIdentity({ ...identity, passphrase });
    await api.registerOperatorKey({
      operatorId: els.operatorId.value.trim() || "operator-main",
      displayName: els.displayName.value.trim() || "Поддержка",
      publicKey: identity.publicKey,
    });

    state.privateKey = identity.privateKey;
    state.publicKey = identity.publicKey;
    setStatus("Ключ зарегистрирован. Диалоги обновляются автоматически.");
    await loadConversations();
    updateSendState();
  } catch (error) {
    setStatus(error.message, true);
  }
}

async function loadPrivateKey() {
  try {
    ensureCryptoAvailable();
    persistSettings();
    const identity = await loadOperatorIdentity(readPassphrase());
    state.privateKey = await importOperatorPrivateKey(identity.privateKeyBytes);
    state.publicKey = identity.publicKey;
    setStatus("Ключ открыт. Диалоги обновляются автоматически.");
    await loadConversations();
    updateSendState();
  } catch (error) {
    setStatus(error.message, true);
  }
}

async function loadConversations() {
  if (state.loadingConversations || !readyForApi()) return;
  state.loadingConversations = true;
  try {
    const conversations = await api.getConversations();
    state.conversations = conversations;
    if (!state.selectedConversationId && conversations.length > 0) {
      state.selectedConversationId = conversations[0].conversationId;
    }
    if (
      state.selectedConversationId &&
      !conversations.some((item) => item.conversationId === state.selectedConversationId)
    ) {
      state.selectedConversationId = conversations[0]?.conversationId || null;
    }

    renderConversations();
    if (state.selectedConversationId) {
      await loadMessages(state.selectedConversationId);
    }
    setStatus(conversations.length ? "Диалоги обновляются автоматически." : "Диалогов пока нет.");
  } catch (error) {
    setStatus(error.message, true);
  } finally {
    state.loadingConversations = false;
    updateSendState();
  }
}

async function loadMessages(conversationId) {
  if (state.loadingMessages || !state.privateKey) return;
  state.loadingMessages = true;
  try {
    const conversation = findConversation(conversationId);
    if (!conversation) return;
    const messages = await api.getMessages(conversationId);
    const viewModels = [];
    for (const message of messages) {
      viewModels.push({
        ...message,
        plainText: await decryptMessageForView(message, conversation),
      });
    }
    renderMessages(viewModels, conversation);
  } catch (error) {
    setStatus(error.message, true);
  } finally {
    state.loadingMessages = false;
  }
}

async function sendReply() {
  try {
    const text = els.replyInput.value.trim();
    const conversation = findConversation(state.selectedConversationId);
    if (!text || !conversation || !state.privateKey || !state.publicKey) return;

    els.sendBtn.disabled = true;
    const encrypted = await encryptMessage({
      privateKey: state.privateKey,
      plainText: text,
      peerPublicKey: conversation.userPublicKey,
      conversationId: conversation.conversationId,
    });
    await api.sendMessage({
      messageId: crypto.randomUUID(),
      conversationId: conversation.conversationId,
      encryptedText: encrypted.value,
      textIv: encrypted.iv,
      senderPublicKey: state.publicKey,
      createdAtMillis: Date.now(),
    });

    els.replyInput.value = "";
    await loadMessages(conversation.conversationId);
    await loadConversations();
  } catch (error) {
    setStatus(error.message, true);
  } finally {
    updateSendState();
  }
}

async function decryptMessageForView(message, conversation) {
  try {
    const peerPublicKey = message.sender === "support"
      ? conversation.userPublicKey
      : (message.senderPublicKey || conversation.userPublicKey);
    return await decryptMessage({
      privateKey: state.privateKey,
      message,
      peerPublicKey,
    });
  } catch (_error) {
    return "Не удалось расшифровать сообщение. Проверьте, что открыт тот же ключ поддержки.";
  }
}

function renderConversations() {
  els.conversations.innerHTML = "";
  if (state.conversations.length === 0) {
    const empty = document.createElement("div");
    empty.className = "conversation";
    empty.innerHTML = "<strong>Пока пусто</strong><span>Пользователь должен написать первым</span>";
    els.conversations.appendChild(empty);
    return;
  }

  for (const conversation of state.conversations) {
    const item = document.createElement("div");
    item.className = "conversation" + (conversation.conversationId === state.selectedConversationId ? " active" : "");
    item.innerHTML = `
      <strong>${conversation.messagesCount} сообщ.</strong>
      <span>${formatTime(conversation.updatedAtMillis)} · ${conversation.conversationId}</span>
    `;
    item.addEventListener("click", async () => {
      state.selectedConversationId = conversation.conversationId;
      renderConversations();
      await loadMessages(conversation.conversationId);
      updateSendState();
    });
    els.conversations.appendChild(item);
  }
}

function renderMessages(messages, conversation) {
  els.chatTitle.textContent = "Диалог поддержки";
  els.chatMeta.textContent = conversation.conversationId;
  els.messages.innerHTML = "";

  if (messages.length === 0) {
    const empty = document.createElement("div");
    empty.className = "empty";
    empty.textContent = "Сообщений пока нет";
    els.messages.appendChild(empty);
    return;
  }

  for (const message of messages) {
    const bubble = document.createElement("div");
    bubble.className = `bubble ${message.sender === "support" ? "support" : "user"}`;
    if (message.plainText.startsWith("Не удалось расшифровать")) {
      bubble.className += " error";
    }
    bubble.textContent = message.plainText;
    els.messages.appendChild(bubble);
  }
  els.messages.scrollTop = els.messages.scrollHeight;
}

function readyForApi() {
  return Boolean(els.apiBase.value.trim() && els.adminToken.value.trim());
}

function updateSendState() {
  els.sendBtn.disabled = !(
    state.selectedConversationId &&
    state.privateKey &&
    state.publicKey &&
    els.replyInput.value.trim()
  );
}

function findConversation(conversationId) {
  return state.conversations.find((conversation) => conversation.conversationId === conversationId) || null;
}

function readPassphrase() {
  const value = els.passphrase.value;
  if (value.length < 10) {
    throw new Error("Пароль ключа должен быть не короче 10 символов.");
  }
  return value;
}

function setStatus(text, isError = false) {
  els.status.textContent = text;
  els.status.classList.toggle("error", isError);
}

function formatTime(value) {
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}
