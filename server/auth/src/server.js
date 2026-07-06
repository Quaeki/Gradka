const { createApp } = require("./app");
const { config } = require("./config");

const app = createApp(config);

app.listen(config.port, config.host, () => {
  console.log(`Gradka auth API listening on ${config.host}:${config.port}`);
});
