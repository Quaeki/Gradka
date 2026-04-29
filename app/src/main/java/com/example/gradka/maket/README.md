# Handoff: Грядка — Android Grocery App (Variant C)

## Overview
"Грядка" is a farm-to-table grocery delivery app for Android. The preferred design direction is **Variant C — Editorial / Magazine style**: generous whitespace, large serif typography, minimal chrome, strong editorial hero on the home screen. This is a full-featured grocery delivery experience with 12+ screens.

## About the Design Files
The files in this bundle are **high-fidelity design prototypes created in HTML/React** — they show the intended look, feel, and interactions. Your task is to **recreate these designs in your existing target environment** (React Native, Jetpack Compose, Flutter, etc.) using its established patterns and libraries. Do not ship the HTML files directly.

Open `Грядка.html` in a browser to interact with the full prototype. The preferred home screen variant is **Variant C** (switch in the Tweaks panel, bottom-right corner, or select the 3rd phone frame labeled "Главная · Вариант C").

## Fidelity
**High-fidelity.** Recreate pixel-perfectly: exact colors, font sizes, weights, spacing, border radii, and interactions. The prototype uses real hover/active states, working cart, navigation transitions, and a functional tweaks panel.

---

## Design Tokens

### Colors
```
--bg:           #fafaf7          /* page background */
--surface:      #ffffff          /* cards, panels */
--surface-2:    #f2efe8          /* input fields, tag backgrounds */
--surface-3:    #e8e4db          /* pressed states, dividers */
--ink:          #1a1a17          /* primary text, buttons */
--ink-2:        #4a4a44          /* secondary text */
--ink-3:        #8a8a82          /* placeholder, captions */
--line:         #e5e2d8          /* borders */
--line-2:       #d6d2c6          /* stronger borders */
--accent:       oklch(0.63 0.12 125)   /* ≈ #5a8a4a — organic green */
--accent-ink:   #ffffff
--accent-soft:  oklch(0.94 0.04 125)   /* light green tint */
--accent-deep:  oklch(0.42 0.08 125)   /* dark green */
--danger:       oklch(0.62 0.16 30)    /* ≈ #d4542a — red/orange */

/* Dark theme overrides */
--bg:           #0f0f0d
--surface:      #181815
--surface-2:    #222220
--surface-3:    #2a2a27
--ink:          #f2f1ec
--ink-2:        #bdbcb4
--ink-3:        #7a7970
--line:         #2a2a27
--line-2:       #383834
--accent:       oklch(0.72 0.14 125)
```

### Typography
```
Display / Headings:  Fraunces (serif), weights 400–500, letter-spacing: -0.025em
Body / UI:           Inter, weights 400–600
Monospace / Prices:  JetBrains Mono, weights 500–600, font-feature-settings: 'tnum' 1
```

### Spacing & Radius
```
Screen horizontal padding: 20px
Card padding:              12–16px
Gap between cards (grid):  10px
--r-sm:   10px
--r-md:   14px
--r-lg:   20px
--r-xl:   28px
Phone inner radius: 36px
Phone outer radius: 44px
```

### Shadows
```
--shadow-sm: 0 1px 2px rgba(26,26,23,0.04), 0 2px 8px rgba(26,26,23,0.03)
--shadow-md: 0 2px 8px rgba(26,26,23,0.06), 0 12px 28px rgba(26,26,23,0.07)
```

---

## Screens / Views

### 1. Onboarding (3 steps)
**Purpose:** Introduce the app's value props before the user reaches home.

**Layout:** Full-screen vertical flex. Logo + skip top bar → centered illustrated circle → text block → progress dots → CTA button.
- Logo: `Fraunces 22px weight-500` + small leaf SVG icon
- Skip button: `Inter 13px color: --ink-3`
- Illustration: 240×240px rounded-36 tile with striped placeholder background (toned by hue per step), centered SVG circles (concentric rings + filled center)
- Step badge: `Inter 11px weight-700 uppercase letter-spacing:0.12em color:--accent-deep`
- Title: `Fraunces 34px weight-500 letter-spacing:-0.025em line-height:1.05`
- Body: `Inter 15px color:--ink-2 line-height:1.55`
- Progress dots: flex row of 3 bars (h:3px, active bar flex:2, inactive flex:1, active color:--ink, inactive:--line-2), border-radius:2px
- Primary CTA: full-width h:54px border-radius:16px bg:--ink color:--bg `Inter 15px weight-500`
- Back button (step 2+): h:54px padding:0 20px border-radius:16px border:1px solid --line-2

**Step hues:** Step 1: 125 (green), Step 2: 95 (yellow-green), Step 3: 48 (warm amber)

---

### 2. Home — Variant C (PREFERRED)
**Purpose:** Editorial magazine-style home. Strong brand statement, one featured product hero, product grid below.

**Header:**
- Logo left (`Fraunces 22px` + leaf icon), 2 icon buttons right (search, profile): 38×38px border-radius:19px border:1px solid --line
- Padding: 14px top, 20px horizontal

**Editorial hero text block (padding 28px top, 20px sides):**
- Eyebrow: `Inter 11px weight-600 uppercase letter-spacing:0.1em color:--ink-3` with 20px wide horizontal rule line before text: `"Сезон №14 · Апрель"`
- Main headline: `Fraunces 38px weight-400 letter-spacing:-0.03em line-height:1.0`, contains `<i>` italic span in --accent-deep color
- Body copy: `Inter 14px color:--ink-2 line-height:1.5 max-width:300px margin-top:14px`
- CTA button: `display:inline-flex align-items:center gap:8px padding:12px 20px border-radius:999px bg:--ink color:--bg Inter 14px weight-500`. Contains pin icon + address text.

**Featured product card (margin: 28px 20px 0):**
- Border-radius: 20px, background: `oklch(0.94 0.04 {product.hue})`
- Padding: 20px
- Top row: label `Inter 10px weight-700 uppercase letter-spacing:0.08em`, title `Fraunces 22px weight-500 letter-spacing:-0.02em line-height:1.1`, price `mono 24px weight-600` — all using dark tones of product hue
- Product image tile: 140px height, border-radius:14px, striped placeholder

**Section title:** `Fraunces 22px weight-500 letter-spacing:-0.02em` left, action link `Inter 13px` right with chevron. Padding: 0 20px, margin: 28px 0 14px.

**Product grid:** `display:grid grid-template-columns:1fr 1fr gap:10px padding:0 16px`

---

### 3. Product Card (grid cell)
- Background: --surface, border-radius:18px, padding:12px, border:1px solid --line
- Product tile: 120×120px (see Assets section)
- Badge (sale/new): position absolute top-left, `Inter 10px weight-700 uppercase letter-spacing:0.02em`. Sale → bg:--danger, New → bg:--ink, Season → bg:--ink. color:#fff. padding:3px 7px border-radius:6px
- Favorite button: 30×30px border-radius:15px, absolute top-right, bg: rgba(255,255,255,0.9) backdropFilter:blur(8px). Heart icon filled when favorited (--danger).
- Product name: `Inter 13px weight-500 line-height:1.25 color:--ink`
- Subtitle: `Inter 11px color:--ink-3 margin-top:2px`
- Price: `JetBrains Mono 16px weight-600 color:--ink`
- Unit: `Inter 10px color:--ink-3`
- Stepper (compact, bottom-right): see Stepper component below

---

### 4. Catalog Screen
- Sticky header: `Fraunces 24px weight-500`, padding:14px 20px 10px, bg:--surface, border-bottom:1px solid --line
- Sticky category chips row below header: horizontal scroll, no scrollbar, padding:14px 16px 4px
- Category chips: see Chip component
- Product grid: 2-column, same as home

---

### 5. Search Screen
- Back button (24×24px icon) + search input field (flex:1 h:44px border-radius:12px bg:--surface-2)
- Input contains: search icon left, text input, voice icon / clear button right
- Empty state: "Недавно искали" pill chips + "Популярные запросы" list rows (h~52px each, border-bottom)
- Results: vertical list of ProductRow components

**ProductRow:** h:auto padding:10px border-radius:16px border:1px solid --line. 72×72px tile left, name/subtitle/price center, Stepper right.

---

### 6. Product Detail Screen
- **Top bar:** Transparent absolute positioned. Back button (40×40px border-radius:20px bg:rgba(255,255,255,0.85) backdropFilter:blur(8px)). Favorite + More buttons same style. top:12px left/right:16px
- **Hero image area:** h:340px bg:`oklch(0.94 0.04 {hue})`. 220×220px centered tile. Pagination dots (3): active dot w:24px inactive w:6px h:6px border-radius:3px bg:--ink/--ink@20%
- **Content below image:** padding:22px 20px
  - Farm label: leaf icon + `Inter 12px weight-500 color:--accent-deep`
  - Product name: `Fraunces 28px weight-500 letter-spacing:-0.02em line-height:1.15`
  - Subtitle: `Inter 14px color:--ink-2`
  - Price: `JetBrains Mono 30px weight-600` + unit `Inter 13px color:--ink-3`
  - Meta grid: 3 columns, `Inter 10px uppercase / 13px weight-500`, bg:--surface-2 border-radius:14px, padding:12px 10px per cell, text-align:center
  - Description label: `Inter 11px uppercase letter-spacing:0.08em weight-600 color:--ink-3 margin-top:24px`
  - Description body: `Inter 14px color:--ink-2 line-height:1.55`
  - Subscribe row: border:1px dashed --line-2 border-radius:14px padding:14px, repeat icon + title/subtitle + toggle pill

- **Bottom action bar (sticky):** padding:14px 16px, border-top:1px solid --line, bg:--bg
  - Empty state: full-width h:54px button bg:--ink, plus icon + "В корзину · {price} ₽"
  - With quantity: inline stepper full-width + separate bag icon button (54×54px bg:--accent)

---

### 7. Cart Screen
- Header: `Fraunces 24px` + item count
- Free delivery progress banner (if subtotal < 1500 ₽): bg:--accent-soft color:--accent-deep border-radius:12px. Progress bar h:4px.
- Cart items: vertical list, each: 64×64px tile + name/unit/price + Stepper compact. padding:10px border-radius:14px border:1px solid --line
- "С этим часто берут" horizontal scroll (100px wide mini-cards)
- Summary: subtotal + delivery lines + divider + total
- Sticky CTA: h:54px full-width, space-between: "Оформить заказ" ← → "{total} ₽ →"

---

### 8. Checkout Screen
- Back + title header
- Address row: 38×38px accent-soft icon box + address text + chevron. border:1px solid --line border-radius:14px
- Time slot picker: radio-style buttons, h:~52px each, border:1.5px solid --line (selected: --ink)
- Payment picker: same radio style, with title + subtitle per option
- Summary totals at bottom
- Sticky CTA: "Оплатить {total} ₽"

---

### 9. Tracking Screen
- SVG map (h:340px): grid pattern, roads, park, dashed route line (stroke: --accent strokeDasharray:7 5), courier circle (--accent with 30% opacity halo), destination pin
- Sheet slides up from bottom (border-top-radius:24px): drag handle pill, ETA time (mono 26px --accent-deep), courier card, 4-step timeline (icon: 22×22 border-radius:11, checked: bg --accent with check icon, unchecked: --line-2)
- Back button absolute top-left on map

---

### 10. Address Screen
- Back + title
- Mini SVG map (h:190px border-radius:16px) with SVG pin marker (32×42px)
- Street address card
- 2×2 detail grid (Квартира / Подъезд / Этаж / Домофон) bg:--surface-2 border-radius:12px
- Saved addresses list, primary has border:--ink + "ОСНОВНОЙ" badge
- "Добавить адрес" dashed border button

---

### 11. Favorites Screen
- Header + item count
- 2-col product grid (same cards as catalog)
- "Мои списки" section: list of saved shopping lists with colored 38×38px tile thumbnail

---

### 12. Profile Screen
- User card: 54×54px avatar circle (--accent-soft bg, initials), name + phone, edit button
- Loyalty card (--ink bg): level, points (Fraunces 24px), cashback badge, progress bar to next level
- 2 menu groups (rounded-16 cards with dividers): orders, addresses, subscriptions / promo codes, notifications, support
- Each menu row: 22×22px icon + title 14px weight-500 + subtitle 11px --ink-3 + chevron
- Red "Выйти" text link at bottom

---

### 13. Orders Screen
- Back + title
- Order cards: date/number top-left, status badge + price top-right, item count. If "В пути": delivery banner inside card (--surface-2 bg, delivery icon + ETA text)

---

### 14. Recipes Screen
- Header + subtitle "Ингредиенты в корзину в 1 клик"
- Vertical list of recipe cards: 140px full-width image tile, title 16px weight-500, time + ingredient count, full-width CTA "Добавить все в корзину"

---

### 15. Success Screen
- Centered: 100×100px circle (--accent-soft) with inner 60×60px circle (--accent) containing check icon (28px)
- Serif title 30px, body copy, two CTAs: "Следить за заказом" (primary) + "На главную" (outlined)

---

## Components

### Stepper
- **Empty (qty=0):** 36×36px button, border-radius:12px, bg:--ink, color:--bg, plus icon
- **With qty:** Inline pill bg:--ink color:--bg border-radius:12px h:36px. Minus button (28×28px) | qty (mono 13px weight-600 min-width:26px text-center) | Plus button. Compact variant: h:32px, buttons 24×24px.

### Chip (category filter)
- `padding:8px 14px border-radius:999px border:1px solid`
- Active: `bg:--ink color:--bg border-color:--ink`
- Inactive: `bg:--surface color:--ink border-color:--line`
- `Inter 13px weight-500 white-space:nowrap`

### Bottom Navigation
- 5 tabs: Главная, Каталог, Избранное, Корзина, Профиль
- Height: ~72px (8px top padding + icon 24px + gap 2px + label 10px + bottom padding)
- Active: --ink icon + label weight:600
- Inactive: --ink-3 icon + label weight:500
- Cart badge: 16×16px min, border-radius:8px, bg:--accent color:--accent-ink, `mono 10px weight-700`, absolute top-right of icon
- bg:--surface border-top:1px solid --line

### Product Placeholder Tile
Since the prototype uses placeholder tiles (no real photography), real implementation should use actual product photography. Placeholder spec for dev reference:
- Horizontal stripe pattern (line every 8px)
- Background: `oklch(0.94 0.03 {product.hue})`
- Stripe color: `oklch(0.88 0.04 {product.hue})`
- Monospace category label bottom-left, 9px
- Each product has a `hue` value (0–360 on oklch chroma wheel) — use it to tint backgrounds

### Section Title
- `Fraunces 22px weight-500 letter-spacing:-0.02em` + optional action link `Inter 13px color:--ink-2` with 14px chevron
- Padding: 0 20px, margin: 28px top 14px bottom

---

## Interactions & Behavior

### Navigation
- Bottom tab bar navigates between: Home → Catalog → Favorites → Cart → Profile
- All screens with a back arrow navigate to the previous screen in history
- Product card tap → Product Detail screen
- Cart icon in Product Detail (when qty > 0) → Cart screen

### Cart
- Cart is global state shared across all screens
- Stepper add/subtract updates cart immediately everywhere
- Cart badge on bottom nav shows total item count
- Items with qty=0 are removed from cart

### Checkout Flow
- Cart → Checkout → (Success screen after "Оплатить")
- On success: cart is cleared, route → Success screen
- Success screen → Tracking (primary CTA) or Home (secondary)

### Transitions
- Screen changes: fade-in + translateY(6px→0) 240ms ease
- Button press: scale(0.97) 80ms ease
- Chip/option selection: border-color and background transition 150ms ease

### Scroll
- All inner phone screens scroll vertically
- No visible scrollbar (`scrollbar-width: none`)
- Sticky headers use `position: sticky; top: 0; z-index: 5`
- Bottom action bars use `position: sticky; bottom: 0`

---

## State Management
```
cart: { [productId: string]: number }   // quantity per product
favs: Set<string>                        // product IDs
catFilter: string                        // active category ('all' | category.id)
route: string                            // current screen name
history: string[]                        // navigation history stack
productId: string | null                 // currently open product
```

---

## Data Model

### Product
```ts
{
  id: string
  name: string
  subtitle: string
  price: number          // RUB
  unit: string           // '1 кг', '500 г', etc.
  cat: string            // category id
  hue: number            // oklch hue for placeholder color
  badge: string | null   // 'new' | '-15%' | 'сезон' | null
  farm: string           // farm/supplier name
}
```

### Category
```ts
{ id: string, label: string, hue: number }
```
Categories: all, veg, fruit, dairy, bakery, meat, fish, pantry

---

## Assets
- **Icons:** All icons are custom inline SVG, stroke-based, 24×24px viewport, strokeWidth:1.6, strokeLinecap:round, strokeLinejoin:round. See `ui.jsx` → `const Icon` object for all paths. Replicate with your preferred icon library (match the stroke style — do not use filled icons).
- **Logo:** "грядка" wordmark in Fraunces 22px + custom leaf SVG (circle with leaf path inside). See `ui.jsx` → `Logo` component.
- **Product images:** Prototype uses stripe-pattern placeholders. Replace with real product photography: square aspect ratio, light/neutral background, shot from above or slight angle.
- **Map:** Prototype uses SVG map illustration. Replace with real map SDK (Google Maps, Mapbox, Yandex Maps) showing courier location and delivery route.
- **Fonts:** Inter, Fraunces, JetBrains Mono — all available on Google Fonts.

---

## Files in this Bundle
| File | Contents |
|---|---|
| `Грядка.html` | Main entry — open in browser to view full prototype |
| `styles.css` | Design tokens (CSS custom properties), global styles |
| `data.jsx` | Product catalog, categories, recipes, orders data |
| `ui.jsx` | Shared components: Icon, ProductPlaceholder, Chip, Stepper, Phone frame, BottomNav, Logo |
| `screens-shop.jsx` | Home A/B/C, Catalog, Search, Product detail screens |
| `screens-flow.jsx` | Onboarding, Cart, Checkout, Tracking, Address, Favorites, Profile, Orders, Recipes, Success |

---

## Implementation Notes for Claude Code

1. **Preferred variant is C** — the editorial/magazine home screen. Variants A and B exist in the prototype for comparison but the product decision is Variant C.

2. **Fraunces is essential** to the brand feel — it's the main differentiator vs. generic grocery apps. Do not substitute with another font.

3. **Accent color** (`oklch(0.63 0.12 125)`) is a calm organic green — do not increase saturation. The brand deliberately avoids bright/neon greens.

4. **Product hues** in the data model drive background tints throughout the app. When displaying real photos, you can drop the hue-tinted backgrounds and use neutral white/surface instead.

5. **Dark mode** is fully specified in CSS tokens — implement as a system-preference toggle following the OS dark mode setting.

6. **Free delivery threshold** is 1 500 ₽. Show the progress bar in cart whenever subtotal is below this.

7. **Sticky elements:** Both header and bottom action bar should be sticky on scroll. Use appropriate platform primitives (StickyHeader, sticky positioned View, etc.).
