# Gradka

Gradka is an Android application for grocery delivery and farm product ordering. The project includes a product catalog, cart, checkout flow, delivery addresses, subscriptions, order history, OTP authentication, local persistence, payment method metadata, and a support chat relayed to the operator's Telegram.

## Features

- Product catalog with categories, product cards, cart, favorites, recipes, and checkout.
- Delivery address management with map/geocoding support.
- OTP authentication through a backend API.
- Local persistence with Room for orders, subscriptions, notes, addresses, payment methods, and support messages.
- Secure local token storage with Android Keystore.
- In-app support chat relayed to the operator's Telegram through a Node.js service and the Telegram Bot API.
- Dependency injection with Hilt and asynchronous data flow with Coroutines/Flow.

## Technology Stack

- Kotlin
- Android SDK, Jetpack Compose, Material 3
- Gradle Kotlin DSL and Gradle Wrapper
- Android Gradle Plugin
- Dagger Hilt
- Room and KSP
- Retrofit and Gson converter
- Kotlin Coroutines and Flow
- Firebase Analytics
- Yandex MapKit and Google Play Services Location
- Dokka for HTML API documentation
- Node.js and Express for the backend services (auth, support-telegram)

## Project Structure

- `app/` - Android application module.
- `app/src/main/java/com/example/gradka/domain/` - domain models, repository contracts, and use cases.
- `app/src/main/java/com/example/gradka/data/` - repository implementations, Room DAO models, and network API DTOs.
- `app/src/main/java/com/example/gradka/security/` - Android Keystore based encrypted storage helpers.
- `app/src/main/java/com/example/gradka/ui/` - Compose screens, UI components, and theme.
- `server/auth/` - OTP authentication API.
- `server/support-telegram/` - support chat API relaying messages to Telegram.

## Local Configuration

Create or update `local.properties` in the project root:

```properties
sdk.dir=/path/to/Android/sdk
YANDEX_MAPS_KEY=your-yandex-mapkit-key
API_BASE_URL=http://your-api-host/
```

For debug builds, HTTP API URLs are allowed. For release builds, `API_BASE_URL` must use HTTPS.

Do not commit `local.properties`, `.env`, private keys, logs, `node_modules/`, or server data files.

## Build and Run

Build a debug APK:

```bash
./gradlew :app:assembleDebug
```

Install the debug APK on a connected emulator or device:

```bash
./gradlew :app:installDebug
```

Run unit tests and lint:

```bash
./gradlew test lint
```

Generate HTML project documentation from KDoc comments:

```bash
./gradlew :app:dokkaGeneratePublicationHtml
```

Generated documentation is written to:

```text
app/build/dokka/html
```

## Support Chat Server (Telegram relay)

The support-telegram service is located in `server/support-telegram`. Messages written in the app are delivered to the operator's personal Telegram chat through a bot; the operator answers with Telegram's "Reply" on the relayed message, and the service routes the answer back to the right app user via long polling (no public webhook URL required).

Setup:

1. Create a bot with [@BotFather](https://t.me/BotFather) and copy the bot token into `TELEGRAM_BOT_TOKEN`.
2. Get your personal chat id from [@userinfobot](https://t.me/userinfobot) and put it into `TELEGRAM_CHAT_ID`.
3. Open your bot in Telegram and press Start so it can message you.

Run locally:

```bash
cd server/support-telegram
npm install
SUPPORT_JWT_SECRET=replace-with-long-random-secret \
TELEGRAM_BOT_TOKEN=replace-with-botfather-token \
TELEGRAM_CHAT_ID=replace-with-your-chat-id \
PORT=3001 npm start
```

`SUPPORT_JWT_SECRET` must be the same HS256 secret the auth service uses to sign access tokens (both services read it from the shared `.env` in Docker deployment). The service verifies the JWT signature and expiry of every request and takes the user identity from the `sub` claim.

Optional environment variables:

- `SUPPORT_RATE_LIMIT_WINDOW_MS` / `SUPPORT_RATE_LIMIT_MAX_REQUESTS` - per-IP rate limit (default 120 requests per 60 seconds).
- `SUPPORT_MAX_MESSAGES_PER_USER` - message history cap per user (default 500, oldest messages are dropped).
- `SUPPORT_MAX_MESSAGE_LENGTH` - maximum message length (default 1000 characters).

## Auth Server

The OTP authentication service is located in `server/auth`. It implements the `auth/send-code`, `auth/verify-code`, `auth/update-name`, and `auth/refresh` endpoints used by the app, stores users in a JSON file, and issues HS256 JWT access tokens signed with the shared `SUPPORT_JWT_SECRET`.

OTP delivery order (each tier falls through to the next on failure):

1. **Telegram Gateway** — when `AUTH_TELEGRAM_GATEWAY_TOKEN` is set. The official verification-code service ([gateway.telegram.org](https://gateway.telegram.org)) delivers the code to any phone number that has a Telegram account — the user just enters their number in the app, no bot registration needed. Paid: about $0.01 per delivered code; register with your Telegram account and top up the balance to get a token.
2. **Telegram bot** — when `AUTH_TELEGRAM_BOT_TOKEN` is set and the user has linked their phone. Create a separate login bot with @BotFather (do not reuse the support-relay bot: Telegram allows only one getUpdates consumer per token). A user opens the bot, presses Start, and taps the "Отправить мой номер" button once; after that login codes arrive in Telegram. Free.
3. **sms.ru** — when `SMS_RU_API_ID` is set.
4. **Dev mode** — with nothing configured, codes are printed to the service log:

```bash
docker compose logs -f auth
# [DEV SMS] OTP code for +79991234567: 123456
```

Run locally:

```bash
cd server/auth
npm install
SUPPORT_JWT_SECRET=replace-with-long-random-secret PORT=3002 npm start
```

## Orders Server

The orders service is located in `server/orders` and uses PostgreSQL. Orders and their items are stored in `orders` / `order_items` tables; the price list lives in the `products` table (seeded from `src/db/catalog.js` on start) and totals are always computed server-side — prices sent by a client are ignored.

User endpoints (JWT): `GET /orders/` returns the caller's order history, `POST /orders/` places an order from cart items and an address. Operator endpoints (protected by `ORDERS_ADMIN_TOKEN`):

```bash
# list latest orders
curl http://127.0.0.1/orders/all -H "X-Orders-Admin-Token: $ORDERS_ADMIN_TOKEN"

# update status by order number (created / confirmed / delivering / delivered / cancelled)
curl -X PATCH http://127.0.0.1/orders/10001/status \
  -H "X-Orders-Admin-Token: $ORDERS_ADMIN_TOKEN" \
  -H 'content-type: application/json' -d '{"status":"delivering"}'
```

The app shows synced statuses on the orders screen (created → «Оформлен», delivering → «В пути», and so on).

## Docker Deployment

The Docker configuration runs five containers: PostgreSQL, the auth service, the orders service, the support-telegram service, and Caddy as the single public entry point. The Android application is built with Gradle as an APK.

Create a local `.env` file from the example and fill it in:

```bash
cp .env.example .env
openssl rand -hex 32   # generate the value for SUPPORT_JWT_SECRET
```

```env
SUPPORT_JWT_SECRET=<random shared JWT secret>
POSTGRES_PASSWORD=<random database password>
ORDERS_ADMIN_TOKEN=<random token for order management>
TELEGRAM_BOT_TOKEN=<bot token from @BotFather>
TELEGRAM_CHAT_ID=<your chat id from @userinfobot>
#DOMAIN=your-domain.ru
#SMS_RU_API_ID=
```

The auth and support-telegram containers bind to loopback only; all public traffic goes through Caddy (ports 80/443). Without `DOMAIN`, Caddy serves plain HTTP on port 80 — this works only with debug builds of the app. Set `DOMAIN` to a domain whose A record points to the server and Caddy will obtain and renew a Let's Encrypt certificate automatically; release builds require an HTTPS `API_BASE_URL`.

Build and run the backend container:

```bash
docker compose up -d --build
```

Check logs:

```bash
docker compose logs -f support-telegram
```

Health check:

```bash
curl http://127.0.0.1:3001/health
```

## Documentation

The project uses KDoc comments for Kotlin classes, interfaces, and important public methods. Dokka converts these comments into HTML documentation.

Recommended documentation command:

```bash
./gradlew :app:dokkaGeneratePublicationHtml
```
