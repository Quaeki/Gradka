const { createApp } = require("./app");
const { config } = require("./config");
const { createPool, migrate } = require("./db/database");

async function main() {
  const pool = createPool(config.databaseUrl);
  await migrate(pool);

  const app = createApp(config, pool);
  app.listen(config.port, config.host, () => {
    console.log(`Gradka orders API listening on ${config.host}:${config.port}`);
  });
}

main().catch((error) => {
  console.error(`Failed to start orders service: ${error.message}`);
  process.exit(1);
});
