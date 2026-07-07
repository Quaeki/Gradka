// Minimal Saby (СБИС) Retail API client.
// Docs: https://saby.ru/help/integration/api/app_sale/sale_delyvery/catalog
class SabyClient {
  constructor({ appClientId, appSecret, secretKey, apiBase, authUrl }) {
    this.appClientId = appClientId;
    this.appSecret = appSecret;
    this.secretKey = secretKey;
    this.apiBase = (apiBase || "https://api.sbis.ru").replace(/\/$/, "");
    this.authUrl = authUrl || "https://online.sbis.ru/oauth/service/";
    this.token = null;
  }

  async authorize() {
    const response = await fetch(this.authUrl, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({
        app_client_id: this.appClientId,
        app_secret: this.appSecret,
        secret_key: this.secretKey,
      }),
    });
    const body = await response.json().catch(() => null);
    if (!response.ok || !body?.token) {
      throw new Error(`SABY_AUTH_FAILED: ${body?.error || response.status}`);
    }
    this.token = body.token;
  }

  // Performs a GET request, re-authorizing once when the token expires.
  async get(path, retried = false) {
    if (!this.token) await this.authorize();

    const response = await fetch(`${this.apiBase}${path}`, {
      headers: { "X-SBISAccessToken": this.token },
    });
    if (response.status === 401 && !retried) {
      this.token = null;
      return this.get(path, true);
    }
    const body = await response.json().catch(() => null);
    if (!response.ok) {
      throw new Error(`SABY_REQUEST_FAILED ${path}: ${body?.error || response.status}`);
    }
    return body;
  }

  async getFirstPoint() {
    const body = await this.get("/retail/point/list?withPrices=true");
    const point = body?.salesPoints?.[0] || body?.points?.[0];
    if (!point) throw new Error("SABY_NO_SALES_POINTS");
    return point;
  }

  async getFirstPriceList(pointId) {
    const today = new Date().toISOString().slice(0, 10);
    const body = await this.get(
      `/retail/nomenclature/price-list?pointId=${pointId}&actualDate=${today}`,
    );
    // Saby returns priceLists as an array or as an object map (an empty result
    // comes back as {}), so normalize both shapes.
    const raw = body?.priceLists;
    const priceLists = (Array.isArray(raw) ? raw : Object.values(raw || {}).flat())
      .filter((item) => item && item.id != null);
    if (priceLists.length === 0) throw new Error("SABY_NO_PRICE_LISTS");
    return priceLists[0];
  }

  // Loads the full nomenclature, transparently walking through pagination
  // (the API caps pages at 25 records).
  async getAllNomenclature(pointId, priceListId) {
    const records = [];
    for (let page = 0; page < 200; page++) {
      const body = await this.get(
        `/retail/v2/nomenclature/list?pointId=${pointId}&priceListId=${priceListId}` +
          `&withBalance=true&page=${page}&pageSize=25`,
      );
      const rawPage = body?.nomenclatures ?? body?.records ?? [];
      const pageRecords = Array.isArray(rawPage) ? rawPage : Object.values(rawPage).flat();
      records.push(...pageRecords);
      const hasMore = body?.outcome?.hasMore ?? pageRecords.length === 25;
      if (!hasMore) break;
    }
    return records;
  }
}

module.exports = { SabyClient };