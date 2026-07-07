const Anthropic = require("@anthropic-ai/sdk");

// Turns raw price-list names («кулич 0,3 изюм») into storefront names
// («Кулич с изюмом, 300 г») with Claude. Results are cached in the products
// table (display_name + display_name_for), so every raw name is paid for once
// and regenerated only when the source name changes in Saby.

const SYSTEM_PROMPT = `Ты — редактор каталога интернет-магазина продуктов. Тебе дают сырые названия товаров из складского прайс-листа. Для каждого составь аккуратное витринное название на русском языке.

Правила:
- Исправь регистр: название с заглавной буквы, без CAPS LOCK.
- Расшифровывай сокращения только когда они однозначны («изюм» у кулича → «с изюмом», «шок.» → «шоколадный», «в/у» → «в вакуумной упаковке»).
- Вес и объём приводи к читаемому виду в конце названия через запятую: у выпечки «0,3» — это 300 г, «0,5» — 500 г; «1л» → «1 л».
- Убирай складской мусор: артикулы, коды, лишние пробелы и точки.
- НИЧЕГО не выдумывай: если свойства нет в исходном названии, не добавляй его. Непонятное название просто поправь по регистру и пунктуации.
- Бренды и торговые марки сохраняй как есть.

Верни для каждого исходного названия пару: original — исходная строка БЕЗ изменений, display — витринное название.`;

const OUTPUT_SCHEMA = {
  type: "object",
  properties: {
    items: {
      type: "array",
      items: {
        type: "object",
        properties: {
          original: { type: "string" },
          display: { type: "string" },
        },
        required: ["original", "display"],
        additionalProperties: false,
      },
    },
  },
  required: ["items"],
  additionalProperties: false,
};

const BATCH_SIZE = 40;

function startDisplayNameGenerator({ pool, config, log = console }) {
  if (!config.apiKey) {
    log.log("Display names: ANTHROPIC_API_KEY is not set, keeping raw product names");
    return { stop: () => {}, runOnce: async () => 0 };
  }

  const client = new Anthropic({
    apiKey: config.apiKey,
    ...(config.baseUrl ? { baseURL: config.baseUrl } : {}),
  });
  let stopped = false;

  async function runOnce() {
    const { rows } = await pool.query(
      `SELECT id, name FROM products
       WHERE is_active AND (display_name IS NULL OR display_name_for IS DISTINCT FROM name)
       ORDER BY name
       LIMIT $1`,
      [config.maxProductsPerRun],
    );
    if (rows.length === 0) return 0;

    const uniqueNames = [...new Set(rows.map((row) => row.name))];
    const displayByName = new Map();
    for (let i = 0; i < uniqueNames.length; i += BATCH_SIZE) {
      const batch = uniqueNames.slice(i, i + BATCH_SIZE);
      for (const [original, display] of await generateBatch(client, config.model, batch)) {
        displayByName.set(original, display);
      }
    }

    let updated = 0;
    for (const row of rows) {
      const display = displayByName.get(row.name);
      if (!display) continue;
      await pool.query(
        "UPDATE products SET display_name = $1, display_name_for = $2 WHERE id = $3",
        [display, row.name, row.id],
      );
      updated += 1;
    }
    log.log(`Display names: ${updated} products named`);
    return updated;
  }

  async function loop() {
    while (!stopped) {
      try {
        await runOnce();
      } catch (error) {
        log.error(`Display name generation failed: ${error.message}`);
      }
      await sleep(config.intervalMillis);
    }
  }

  loop();
  return { stop: () => { stopped = true; }, runOnce };
}

async function generateBatch(client, model, names) {
  const response = await client.messages.create({
    model,
    max_tokens: 16000,
    system: SYSTEM_PROMPT,
    messages: [{ role: "user", content: JSON.stringify({ names }) }],
    output_config: { format: { type: "json_schema", schema: OUTPUT_SCHEMA } },
  });

  const result = new Map();
  if (response.stop_reason === "refusal") return result;

  const textBlock = response.content.find((block) => block.type === "text");
  if (!textBlock) return result;

  const parsed = JSON.parse(textBlock.text);
  for (const item of parsed.items || []) {
    const display = String(item.display || "").trim();
    if (item.original && display) {
      result.set(item.original, display);
    }
  }
  return result;
}

function sleep(millis) {
  return new Promise((resolve) => setTimeout(resolve, millis));
}

module.exports = { startDisplayNameGenerator };
