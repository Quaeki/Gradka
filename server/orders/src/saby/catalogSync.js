const { isFolderRecord } = require("./sabyClient");

// Synchronizes the products table with the Saby (СБИС) price list.
// After a successful sync the Saby catalog fully replaces the seeded one:
// items missing from Saby are deactivated, not deleted, so old orders keep
// their references. A product's category is the name of its top-level Saby
// folder («Кондитерка», «Кофе», …); the app builds its category chips from
// these values dynamically.
function startCatalogSync({ saby, pool, intervalMillis, pointId, priceListId, log = console }) {
  let stopped = false;

  async function syncOnce() {
    const resolvedPointId = pointId || (await saby.getFirstPoint()).id;
    const resolvedPriceListId = priceListId || (await saby.getFirstPriceList(resolvedPointId)).id;
    const records = await saby.getAllNomenclature(resolvedPointId, resolvedPriceListId);

    // Folder tree: hierarchicalId -> { name, parent } to resolve each item's
    // top-level folder name.
    const folders = new Map();
    for (const record of records) {
      const folderId = record.hierarchicalId ?? record.id;
      if (isFolderRecord(record) && folderId != null) {
        folders.set(folderId, {
          name: String(record.name || "").trim(),
          parent: record.hierarchicalParent ?? record.parent ?? null,
        });
      }
    }

    function topLevelFolderName(startFolderId) {
      let currentId = startFolderId;
      let name = null;
      for (let depth = 0; depth < 20 && currentId != null; depth++) {
        const folder = folders.get(currentId);
        if (!folder) break;
        name = folder.name || name;
        currentId = folder.parent;
      }
      return name;
    }

    const products = records
      .filter((record) => !isFolderRecord(record) && record.cost != null && record.name)
      .map((record) => ({
        id: `saby-${record.externalId || record.id}`,
        name: String(record.name),
        price: Math.round(Number(record.cost)),
        subtitle: record.description ? String(record.description) : null,
        unit: record.unit ? String(record.unit) : null,
        category: topLevelFolderName(record.hierarchicalParent ?? record.parent) || "all",
        imageUrl: Array.isArray(record.images) && record.images[0]
          ? new URL(record.images[0], saby.apiBase).toString()
          : null,
      }))
      .filter((product) => Number.isFinite(product.price) && product.price >= 0);

    if (products.length === 0) {
      log.log("Saby sync: price list is empty, keeping current catalog");
      return 0;
    }

    const client = await pool.connect();
    try {
      await client.query("BEGIN");
      await client.query("UPDATE products SET is_active = FALSE");
      for (const product of products) {
        await client.query(
          `INSERT INTO products (id, name, price, subtitle, unit, category, image_url, is_active)
           VALUES ($1, $2, $3, $4, $5, $6, $7, TRUE)
           ON CONFLICT (id) DO UPDATE SET
             name = EXCLUDED.name, price = EXCLUDED.price, subtitle = EXCLUDED.subtitle,
             unit = EXCLUDED.unit, category = EXCLUDED.category,
             image_url = EXCLUDED.image_url, is_active = TRUE`,
          [product.id, product.name, product.price, product.subtitle, product.unit, product.category, product.imageUrl],
        );
      }
      await client.query("COMMIT");
    } catch (error) {
      await client.query("ROLLBACK").catch(() => {});
      throw error;
    } finally {
      client.release();
    }

    log.log(`Saby sync: ${products.length} products updated`);
    return products.length;
  }

  async function loop() {
    while (!stopped) {
      try {
        await syncOnce();
      } catch (error) {
        log.error(`Saby sync failed: ${error.message}`);
      }
      await sleep(intervalMillis);
    }
  }

  loop();
  return { stop: () => { stopped = true; }, syncOnce };
}

function sleep(millis) {
  return new Promise((resolve) => setTimeout(resolve, millis));
}

module.exports = { startCatalogSync };
