export class SupportChatApi {
  constructor({ getAdminToken, getBaseUrl }) {
    this.getAdminToken = getAdminToken;
    this.getBaseUrl = getBaseUrl;
  }

  registerOperatorKey(body) {
    return this.request("/operator/api/key", { method: "POST", body });
  }

  getConversations() {
    return this.request("/operator/api/conversations");
  }

  getMessages(conversationId) {
    return this.request(`/operator/api/messages?conversationId=${encodeURIComponent(conversationId)}`);
  }

  sendMessage(body) {
    return this.request("/operator/api/messages", { method: "POST", body });
  }

  async request(path, options = {}) {
    const baseUrl = this.getBaseUrl().replace(/\/$/, "");
    const response = await fetch(baseUrl + path, {
      method: options.method || "GET",
      headers: {
        "Content-Type": "application/json",
        "X-Support-Admin-Token": this.getAdminToken(),
      },
      body: options.body ? JSON.stringify(options.body) : undefined,
    });
    const text = await response.text();
    const body = text ? JSON.parse(text) : null;
    if (!response.ok) {
      throw new Error(body?.error || `HTTP ${response.status}`);
    }
    return body;
  }
}
