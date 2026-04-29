# Handoff: Грядка — Авторизация

## Overview
7 экранов авторизации: сплэш → welcome → ввод телефона → OTP → (регистрация: имя) → успех. Плюс восстановление доступа. Открой `Авторизация.html` в браузере — полностью интерактивный прототип с работающим нампадом и OTP.

---

## Флоу

```
                     ┌──────────┐
         ┌──────────▶│  Вход    │──▶ Телефон ──▶ OTP ──▶ Успех (вход)
         │           └──────────┘
Сплэш ──▶ Welcome
         │           ┌──────────────┐
         └──────────▶│ Регистрация  │──▶ Телефон ──▶ OTP ──▶ Имя ──▶ Успех (новый)
                     └──────────────┘

Welcome ──▶ [Забыли доступ?] ──▶ Recovery: Телефон ──▶ OTP ──▶ Готово
```

---

## Экраны

### 01 — Splash
- Полноэкранный фон `--ink` (#1a1a17)
- Логотип по центру: leaf SVG + "грядка" Fraunces 36sp weight-500, цвет #fafaf7
- Анимация появления: `scale(0.6) opacity(0)` → `scale(1) opacity(1)`, `cubic-bezier(.34,1.56,.64,1)` 600ms
- 3 мигающих точки под логотипом: 6×6dp, border-radius 3dp, opacity .3, blink animation 1.2s с задержками 0/0.2/0.4s
- Декоративные кольца — 2 концентрических круга bottom-right, opacity .06/.08
- **Автопереход** на Welcome через 2000ms

```kotlin
// Android: используй Handler + постаршонный переход
Handler(Looper.getMainLooper()).postDelayed({
    findNavController().navigate(R.id.action_splash_to_welcome)
}, 2000)
```

---

### 02 — Welcome
**Layout:** верхняя тёмная зона flex:1 + нижний sheet.

**Верхняя зона (bg #1a1a17):**
- 3 концентрических круга по центру (320dp/220dp/130dp), последний с bg accent opacity .18
- Логотип + заголовок `Fraunces 32sp weight-500` цвет #fafaf7, line-height 1.05, letter-spacing -.025em
- Подзаголовок: 14sp rgba(250,250,247,.6) line-height 1.55

**Нижний sheet (bg --bg):**
- Border-top-radius: 28dp
- Margin-top: -24dp (перекрывает тёмную зону)
- Drag handle: 36×4dp, cornerRadius 2dp, bg --line-2, centered, margin-bottom 24dp
- Кнопка «Войти»: h 54dp, cornerRadius 16dp, bg --ink, color --bg, 15sp weight-500
- Кнопка «Создать аккаунт»: h 54dp, cornerRadius 16dp, border 1.5dp --line-2, bg transparent, 15sp weight-500
- Ссылки на соглашение: 12sp --ink-3, подчёркнутые слова цвет --ink

---

### 03 — Ввод номера телефона
**Header:** back button 38×38dp border-radius 19dp, border 1dp --line

**Контент (padding 22dp 24dp):**
- Бейдж: 11sp weight-700 uppercase letter-spacing .1em `--accent-deep`
- Заголовок: Fraunces 30sp weight-500 letter-spacing -.025em line-height 1.1
- Подзаголовок: 14sp --ink-2 line-height 1.5

**Phone input field:** h 60dp, cornerRadius 16dp, border 1.5dp:
- Default: `--line`
- Focused/filled: `--ink` + box-shadow `0 0 0 3px rgba(26,26,23,.06)`
- Error: `--danger`

Левая секция (flag + code): padding 0 14dp, border-right 1dp --line, флаг 🇷🇺 22sp + "+7" mono 15sp weight-600. Правая: форматированный номер mono 22sp weight-600, мигающий курсор 2×34dp при незаполненном поле.

**Форматирование номера:**
```kotlin
fun formatPhone(digits: String): String {
    var s = ""
    if (digits.isNotEmpty()) s += "(${digits.take(3)}"
    if (digits.length >= 3) s += ") "
    if (digits.length > 3) s += digits.substring(3, minOf(6, digits.length))
    if (digits.length >= 6) s += "-"
    if (digits.length > 6) s += digits.substring(6, minOf(8, digits.length))
    if (digits.length >= 8) s += "-"
    if (digits.length > 8) s += digits.substring(8, minOf(10, digits.length))
    return s
}
```

**Ошибка:** иконка warning + текст 13sp --danger, margin-top 8dp.

**Нампад:** см. раздел «Компонент NumPad» ниже.

---

### 04 — OTP (4 цифры)
**Контент:**
- Заголовок Fraunces 30sp
- Подзаголовок с отформатированным номером **bold mono** + кнопка-карандаш для редактирования

**OTP ячейки:** 4 штуки, gap 10dp, каждая:
- Size: 64×72dp, cornerRadius 16dp, border 1.5dp, bg --surface
- **Состояния:**
  - Default: border --line
  - Active (текущая): border --ink + shadow `0 0 0 3px rgba(26,26,23,.08)`
  - Filled: border --ink
  - Error: border --danger, bg `oklch(0.97 0.02 30)`
  - Success: border --accent, bg --accent-soft, иконка check вместо цифры, анимация growIn
- Цифра внутри: mono 28sp weight-600
- Мигающий курсор в активной пустой ячейке: 2×34dp borderRadius 1dp, blink 1s step-end

**Логика верификации:**
```kotlin
// Имитация проверки — замени на реальный API
fun verifyCode(code: String): VerifyResult {
    return when (code) {
        "0000" -> VerifyResult.Error("Неверный код")
        else   -> VerifyResult.Success // любой другой код — успех (demo)
    }
}
// На продакшне: POST /auth/verify { phone, code }
```

**Таймер повторной отправки:** 59 секунд, mono weight-600. По истечении — кнопка «Отправить снова» с иконкой refresh.

**Состояние checking:** spinner 20×20dp (border 2dp, border-top --accent, rotation animation) по центру вместо сообщения об ошибке.

---

### 05 — Ввод имени (регистрация)
**Аватар-заглушка:** 80×80dp border-radius 40dp, border 2dp --line.
- Пустой: bg --surface-2, иконка user --ink-3
- С именем: bg --accent-soft, первая буква имени Fraunces 28sp weight-700 --accent-deep. Анимация перехода background 200ms.

**Поле имени:** h 56dp, cornerRadius 16dp, font 18sp. Placeholder "Например, Анна".

**CTA кнопка:** меняет цвет при наличии текста:
```kotlin
buttonBg    = if (name.isNotBlank()) inkColor    else surface2Color
buttonColor = if (name.isNotBlank()) bgColor     else ink3Color
```

---

### 06 — Успешный вход / регистрация
**Анимация иконки:** 100×100dp circle --accent-soft → 62×62dp circle --accent → check 30dp white. Animation: `growIn .5s cubic-bezier(.34,1.56,.64,1)`.

**Тексты (разные для входа и регистрации):**
```kotlin
title = if (isNew) "Привет, $name!" else "С возвращением!"
body  = if (isNew) "Аккаунт создан. Готовы делать первый заказ?"
               else "Вы успешно вошли в аккаунт."
```

**Для новых пользователей** — 3 карточки фич (10dp gap):
- Padding 10dp 14dp, cornerRadius 12dp, bg --surface, border 1dp --line
- Emoji 22sp + title 14sp weight-500 + subtitle 12sp --ink-3

---

### 07 — Восстановление доступа
Двухшаговый флоу (Step 0: телефон, Step 1: OTP) — те же компоненты что в основном флоу, бейдж цвет --danger вместо --accent-deep.

Финальный экран: анимированный check (80dp/50dp кружки), заголовок Fraunces 26sp, кнопка «Войти».

---

## Компонент NumPad

```
┌───────┬───────┬───────┐
│   1   │  2    │  3    │
│       │  ABC  │  DEF  │
├───────┼───────┼───────┤
│   4   │  5    │  6    │
│  GHI  │  JKL  │  MNO  │
├───────┼───────┼───────┤
│   7   │  8    │  9    │
│ PQRS  │  TUV  │  WXYZ │
├───────┼───────┼───────┤
│ Далее │  0    │  ⌫   │
└───────┴───────┴───────┘
```

**Каждая клавиша:**
- h 60dp, cornerRadius 14dp, bg --surface, shadow `0 1px 2px rgba(26,26,23,.06)`
- Цифра: 22sp weight-500 --ink
- Подпись (ABC и т.д.): 9sp weight-600 uppercase letter-spacing .12em --ink-3
- Active press: scale(.94) + bg --surface-2, duration 70ms

**Кнопка «Далее»:** bg --surface-2, 13sp weight-600 --ink-2, disabled opacity .35

**Кнопка ⌫:** bg --surface-2, SVG иконка backspace

**Padding контейнера:** 8dp top/bottom, 20dp sides; display grid 3 columns gap 10dp.

```kotlin
@Composable
fun NumPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    doneEnabled: Boolean,
) {
    val keys = listOf(
        "1" to "", "2" to "ABC", "3" to "DEF",
        "4" to "GHI", "5" to "JKL", "6" to "MNO",
        "7" to "PQRS", "8" to "TUV", "9" to "WXYZ",
        null to null, "0" to "", null to null,
    )
    LazyVerticalGrid(columns = Fixed(3), ...) {
        // render keys
        // for null positions: Done button (index 9) and Delete (index 11)
    }
}
```

---

## Анимации

```kotlin
// growIn — появление success иконки
val scale by animateFloatAsState(
    targetValue = if (visible) 1f else 0.6f,
    animationSpec = spring(dampingRatio = .4f, stiffness = 300f)
)

// blink — курсор в OTP
val alpha by rememberInfiniteTransition().animateFloat(
    initialValue = 1f, targetValue = 0f,
    animationSpec = infiniteRepeatable(
        animation = keyframes { durationMillis = 1000; 1f at 500 },
        repeatMode = RepeatMode.Restart
    )
)

// sfade — fade при смене экрана
AnimatedContent(
    targetState = screen,
    transitionSpec = { fadeIn(tween(280)) + slideInVertically { it / 12 } togetherWith fadeOut(tween(200)) }
)
```

---

## State Machine

```kotlin
enum class AuthScreen { SPLASH, WELCOME, PHONE, OTP, NAME, SUCCESS, RECOVERY }

data class AuthState(
    val screen: AuthScreen = AuthScreen.SPLASH,
    val mode: AuthMode = AuthMode.LOGIN,  // LOGIN | REGISTER
    val phone: String = "",
    val otp: String = "",
    val name: String = "",
    val otpError: Boolean = false,
    val otpChecking: Boolean = false,
    val otpCountdown: Int = 59,
    val phoneError: String = "",
    val loading: Boolean = false,
)

sealed class AuthEvent {
    object GoToWelcome : AuthEvent()
    data class SelectMode(val mode: AuthMode) : AuthEvent()
    data class PhoneDigit(val d: String) : AuthEvent()
    object PhoneDelete : AuthEvent()
    object PhoneSubmit : AuthEvent()
    data class OtpDigit(val d: String) : AuthEvent()
    object OtpDelete : AuthEvent()
    object OtpResend : AuthEvent()
    data class NameInput(val name: String) : AuthEvent()
    object NameSubmit : AuthEvent()
    object Done : AuthEvent()
    object GoToRecovery : AuthEvent()
    object Back : AuthEvent()
}
```

---

## API эндпоинты

```
POST /auth/send-otp     { phone: "+79161234567" }
                        → { success: true, expiresIn: 59 }

POST /auth/verify-otp   { phone: "+79161234567", code: "1234" }
                        → { token: "...", isNew: bool, user: { id, name } }

POST /auth/register     { phone, name, token }
                        → { user: { id, name, phone } }

POST /auth/recovery     { phone }  // отправить код восстановления
```

---

## Дизайн-токены (те же, что в основном приложении)

```kotlin
// см. README.md → GradkaColors
// Основные для авторизации:
// --ink        #1a1a17  — фон сплэша, welcome hero
// --accent     #5c8c49  — бейджи, success check, OTP active
// --accent-deep #3a6030 — заголовочные бейджи
// --accent-soft #eef4e8 — success bg, аватар bg
// --danger     #d4532a  — recovery бейдж, OTP error border
```

---

## Файлы в бандле

| Файл | Описание |
|---|---|
| `Авторизация.html` | Интерактивный прототип всех 7 экранов |
| `README_AUTH.md` | Этот файл |
| `Грядка.html` | Основной прототип (12 экранов) |
| `Карты.html` | Карты и адрес |
| `README.md` | Документация основного приложения |
| `README_MAPS.md` | Документация экранов карт |
