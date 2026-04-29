# Handoff: Грядка — Карты и Адрес (Yandex Maps)

## Overview
Три экрана, связанных с адресом доставки и отслеживанием курьера. Все используют **Yandex Maps JS API 2.1**.

Открой `Карты.html` в браузере — это полностью интерактивный прототип с реальными картами.

## API Key
```
4139e44d-4325-4707-a507-02a0ab44fea6
```
Подключение:
```html
<script src="https://api-maps.yandex.ru/2.1/?apikey=4139e44d-4325-4707-a507-02a0ab44fea6&lang=ru_RU"></script>
```
В Android: `com.yandex.android:maps.mobile` (MapKit 4.x).

---

## Экран 01 — Адрес доставки

### Назначение
Пользователь выбирает адрес доставки через карту или поиск.

### Layout
```
┌─────────────────────────────┐
│ ← Адрес доставки            │ ← sticky header
│ [🔍 Поиск адреса…        🗡]│ ← search bar
├─────────────────────────────┤
│                             │
│      YANDEX MAP 240px       │ ← интерактивная карта
│         📍 (пин)            │ ← перетаскиваемый маркер
│              [🎯 GPS btn]   │
│         [⟳ Перетащите пин] │
├─────────────────────────────┤
│ ул. Лесная, 14              │ ← detected address (геокодер)
│ Москва                      │
├─────────────────────────────┤
│ ДЕТАЛИ                      │
│ [Кв: 47] [Подъезд: 3]      │ ← 2-column grid
│ [Этаж: 6] [Домофон: 47В]   │
├─────────────────────────────┤
│ МОИ АДРЕСА                  │
│ 🏠 Дом · ул. Лесная, 14    │ ← primary (border: --ink)
│ 💼 Работа · Пресн. наб 12  │
│ [+ Добавить адрес]          │ ← dashed border button
├─────────────────────────────┤
│ [✓ Подтвердить адрес]      │ ← sticky bottom CTA
└─────────────────────────────┘
```

### Yandex Maps интеграция

**Инициализация карты:**
```kotlin
// Android MapKit
val mapView = MapView(context)
val map = mapView.map
map.move(CameraPosition(Point(55.771, 37.582), 15f, 0f, 0f))

// Перетаскиваемый маркер
val placemark = map.mapObjects.addPlacemark(Point(55.771, 37.582))
placemark.isDraggable = true
placemark.addDragListener(object : MapObjectDragListener {
    override fun onMapObjectDragEnd(mapObject: MapObject) {
        val coords = (mapObject as PlacemarkMapObject).geometry
        geocode(coords) // reverse geocoding
    }
})
```

**Геокодирование (reverse geocode при drag):**
```kotlin
// Используй Yandex Geocoder API
// GET https://geocode-maps.yandex.ru/1.x/?apikey=KEY&geocode=lon,lat&format=json&results=1
suspend fun reverseGeocode(lat: Double, lon: Double): String {
    val url = "https://geocode-maps.yandex.ru/1.x/?apikey=$API_KEY&geocode=$lon,$lat&format=json&results=1"
    // parse response → featureMember[0].GeoObject.name
}
```

**Поиск с автодополнением (Suggest):**
```kotlin
// Yandex Suggest API
// GET https://suggest-maps.yandex.ru/v1/suggest?apikey=KEY&text=QUERY&lang=ru&results=5
// Обновляй список с debounce 250ms при каждом изменении текста
```

**Клик по карте:**
```kotlin
map.addInputListener(object : InputListener {
    override fun onMapTap(map: Map, point: Point) {
        placemark.geometry = point
        geocode(point)
    }
    override fun onMapLongTap(map: Map, point: Point) {}
})
```

**Геолокация:**
```kotlin
// Запроси разрешение ACCESS_FINE_LOCATION
// Используй FusedLocationProviderClient или LocationManager
locationClient.lastLocation.addOnSuccessListener { location ->
    val point = Point(location.latitude, location.longitude)
    placemark.geometry = point
    map.move(CameraPosition(point, 16f, 0f, 0f), Animation(Animation.Type.SMOOTH, 0.5f), null)
    geocode(point)
}
```

### Состояние экрана
```kotlin
data class AddressScreenState(
    val query: String = "",
    val suggests: List<SuggestItem> = emptyList(),
    val detectedAddress: String = "",
    val detectedCity: String = "",
    val apt: String = "",
    val entrance: String = "",
    val floor: String = "",
    val intercom: String = "",
    val isLoading: Boolean = false,
    val isGpsLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val mapCenter: LatLng = LatLng(55.771, 37.582),
)
```

### UI-компоненты

**Search bar:** height 44dp, cornerRadius 12dp, bg `surface_2`, border 1.5dp прозрачный (active: `ink`). Иконки: поиск слева, loading spinner / clear button справа.

**Suggest dropdown:** cornerRadius 14dp, border 1dp `line`, shadow `0 6px 24px rgba(26,26,23,.08)`. Каждый item: padding 13dp 14dp, pin icon + primary text 14sp weight-500 + secondary text 12sp `ink_3`. Divider между items.

**GPS button:** 44×44dp, cornerRadius 22dp, bg `surface`, border `line`, shadow. Иконка: target/crosshair в цвете `accent`.

**Map hint chip:** bg `rgba(26,26,23,.75)` backdropFilter blur, border-radius 8dp, text 11sp white. Позиция: bottom-left карты.

**Address card:** padding 14dp, cornerRadius 14dp, border 1dp `line`, bg `surface`. Адрес: 16sp weight-500; город: 13sp `ink_3`.

**Details grid:** 2 колонки, gap 8dp. Каждая ячейка: padding 11dp 12dp, cornerRadius 12dp, bg `surface_2`. Label 10sp `ink_3`, value 14sp weight-500.

**Saved address row:** padding 12dp 14dp, cornerRadius 12dp. Primary: border `ink`. Icon box: 36×36dp cornerRadius 10dp, bg `accent_soft` (primary) или `surface_2`. Badge "ОСНОВНОЙ": bg `ink`, color `bg`, 9sp weight-700.

**CTA button:** height 54dp, cornerRadius 16dp, bg `ink`, color `bg`, 15sp weight-500. После нажатия: bg `accent_soft`, color `accent_deep`, check icon.

---

## Экран 02 — Отслеживание курьера

### Назначение
Реальное время движения курьера по маршруту с ETA.

### Layout
```
┌─────────────────────────────┐
│ ←              [ETA 31 мин] │ ← absolute overlay buttons
│                             │
│      YANDEX MAP 330px       │
│  🏭──────────────🎯         │ ← маршрут пунктиром (accent)
│        🟢 (курьер)          │ ← анимированный маркер
│                             │
│ ▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒ 40%     │ ← progress bar bottom карты
├─────────────────────────────┤
│ ════ (drag handle)          │
│ В ПУТИ                      │ ← 11sp accent_deep uppercase
│ Прибудет через 31 мин  ~19:55│ ← serif 22sp + mono 22sp
├─────────────────────────────┤
│ [АП] Александр П.    [📞]  │ ← courier card
│      Электровелосипед       │
├─────────────────────────────┤
│ ● Принят       19:02        │
│ | ● Собирается  19:18        │ ← steps timeline
│ | ● В пути ● СЕЙЧАС 19:24   │ ← active step
│   ○ У дверей   ~19:55       │
├─────────────────────────────┤
│ [2.4 км] [18 км/ч] [#24809]│ ← route info chips
│ [🧭 Построить маршрут]      │
└─────────────────────────────┘
```

### Yandex Maps интеграция

**Маршрут от склада до адреса:**
```kotlin
val warehousePoint = Point(55.758, 37.642)
val deliveryPoint  = Point(55.771, 37.582)

val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
val requestPoints = listOf(
    RequestPoint(warehousePoint, RequestPointType.WAYPOINT, null),
    RequestPoint(deliveryPoint,  RequestPointType.WAYPOINT, null),
)
drivingRouter.requestRoutes(requestPoints, DrivingOptions(), VehicleOptions(), routeListener)

// В routeListener → получаем DrivingRoute → рисуем polyline
val polyline = route.geometry
map.mapObjects.addPolyline(polyline).apply {
    setStrokeColor(Color.parseColor("#5c8c49"))
    strokeWidth = 4f
    setDashLength(12f)
    setGapLength(8f)
}
```

**Анимация курьера по маршруту:**
```kotlin
// Получи список точек маршрута
val routePoints: List<Point> = polyline.points

// Запусти анимацию
var currentIndex = (routePoints.size * 0.3).toInt() // начинаем с 30%
val handler = Handler(Looper.getMainLooper())
val runnable = object : Runnable {
    override fun run() {
        if (currentIndex < routePoints.size - 1) {
            currentIndex++
            courierPlacemark.geometry = routePoints[currentIndex]
            val progress = currentIndex.toFloat() / routePoints.size
            updateETA(progress) // обновить ETA
            handler.postDelayed(this, 400)
        }
    }
}
handler.post(runnable)
```

**Маркеры:**
```kotlin
// Курьер (зелёный кружок с иконкой)
val courierIcon = ImageProvider.fromResource(context, R.drawable.ic_courier_marker)
courierPlacemark = map.mapObjects.addPlacemark(warehousePoint, courierIcon)

// Склад (белый кружок с иконкой здания)
val warehouseIcon = ImageProvider.fromResource(context, R.drawable.ic_warehouse_marker)
map.mapObjects.addPlacemark(warehousePoint, warehouseIcon)

// Адрес назначения (чёрная капля)
val destIcon = ImageProvider.fromResource(context, R.drawable.ic_destination_pin)
map.mapObjects.addPlacemark(deliveryPoint, destIcon)
```

**ETA расчёт:**
```kotlin
// Из маршрута: route.metadata.weight.timeWithTraffic.value (секунды)
fun updateETA(progress: Float) {
    val remainingSeconds = totalRouteSeconds * (1f - progress)
    val minutes = (remainingSeconds / 60).roundToInt()
    etaText = "$minutes мин"
}
```

**Fit bounds (показать весь маршрут):**
```kotlin
val boundingBox = BoundingBox(
    Point(minOf(warehousePoint.latitude, deliveryPoint.latitude) - 0.005,
          minOf(warehousePoint.longitude, deliveryPoint.longitude) - 0.005),
    Point(maxOf(warehousePoint.latitude, deliveryPoint.latitude) + 0.005,
          maxOf(warehousePoint.longitude, deliveryPoint.longitude) + 0.005)
)
map.move(map.cameraPosition(boundingBox))
```

### Состояние экрана
```kotlin
data class TrackingScreenState(
    val orderStep: Int = 2,        // 0=принят 1=собран 2=в пути 3=у дверей
    val etaMinutes: Int = 31,
    val courierName: String = "Александр П.",
    val courierProgress: Float = 0.3f, // 0..1 вдоль маршрута
    val routeDistanceKm: Float = 2.4f,
    val routePoints: List<LatLng> = emptyList(),
)
```

### UI-компоненты

**Map:** height 330dp. Без контролов. Overflow скрыт.

**Back button (overlay):** absolute top-14dp left-14dp. 40×40dp cornerRadius 20dp, bg `rgba(255,255,255,0.9)` + backdropFilter blur.

**ETA chip (overlay):** absolute top-14dp right-14dp. bg `rgba(255,255,255,0.9)` backdropFilter blur, cornerRadius 12dp, padding 8dp 12dp. "ETA" label 10sp uppercase `ink_3`, time `mono 18sp weight-600 ink`.

**Progress bar:** absolute bottom карты, height 3dp, full width. bg `line`. Fill: bg `accent`, анимируй width от 0 до 100% по прогрессу курьера.

**Bottom sheet:** cornerRadius top 24dp. Drag handle: 36×4dp cornerRadius 2dp bg `line_2` центрирован, margin-bottom 18dp.

**Status label:** 11sp weight-700 uppercase letter-spacing .08em `accent_deep`.

**Headline:** `Fraunces 22sp weight-500 letter-spacing -.02em`. ETA time справа: `mono 22sp weight-600 accent_deep`.

**Courier card:** padding 12dp 14dp, cornerRadius 14dp, border `line`. Avatar: 44×44dp cornerRadius 22dp, bg `accent`, initials 14sp weight-700 white. Name 14sp weight-500, subtitle 12sp `ink_3` + delivery icon. Call button: 44×44dp cornerRadius 22dp, bg `accent`, box-shadow `0 4px 12px rgba(92,140,73,.35)`.

**Steps timeline:**
- Иконка шага: 24×24dp cornerRadius 12dp. Done: bg `accent`, border `accent`, check icon white. Active: bg `accent_soft`, border `accent`, animated inner dot (pulse animation). Pending: bg `surface`, border `line_2`.
- Connector line: 2dp wide, height 20dp. Done: bg `accent`. Pending: bg `line_2`.
- "СЕЙЧАС" badge: padding 3dp 8dp cornerRadius 6dp, bg `accent_soft`, color `accent_deep`, 11sp weight-600.
- Step label: 14sp. Active: weight-600 `ink`. Done: weight-500 `ink`. Pending: weight-500 `ink_3`.

**Route info row:** 3 ячейки flex. Каждая: value `mono 15sp weight-600 ink`, label `10sp uppercase letter-spacing .06em ink_3`. bg `surface_2`, cornerRadius 12dp, padding 12dp 14dp.

**Navigate button:** height 48dp, cornerRadius 14dp, border `line`, bg `surface`. Nav icon + 14sp weight-500.

---

## Экран 03 — Ввод адреса вручную

### Назначение
Форма для ручного ввода адреса, если поиск не помог или нужно уточнить детали.

### Layout (2 шага)

**Шаг 1 — Поиск улицы:**
```
┌─────────────────────────────┐
│ Новый адрес                 │ ← header
│ Введите улицу и дом         │
│ ━━━━━━━━━━ ░░░░░░░░░        │ ← progress: шаг 1 активен
│ Адрес      Детали           │
├─────────────────────────────┤
│ УЛИЦА И ДОМ                 │
│ [🔍 Например: ул. Лесная 14]│ ← search input, autofocus
│ ↓ suggest dropdown          │ ← Yandex Suggest
├─────────────────────────────┤
│ ──────── или ────────        │
│ [✏️ Ввести вручную →]       │ ← border 1dp line
├─────────────────────────────┤
│ НЕДАВНИЕ АДРЕСА             │
│ 📍 ул. Лесная, 14    →      │
│ 📍 Пресненская наб.  →      │
└─────────────────────────────┘
```

**Шаг 2 — Детали:**
```
┌─────────────────────────────┐
│ ← Детали адреса             │ ← back to step 1
│ ул. Лесная, 14              │ ← выбранный адрес
│ ░░░░░░░░░░ ━━━━━━━━━━       │ ← progress: шаг 2 активен
├─────────────────────────────┤
│ ТИП АДРЕСА                  │
│ [🏠 Дом] [💼 Работа] [📍]  │ ← segmented control
├─────────────────────────────┤
│ УЛИЦА И ДОМ *               │
│ [📍 ул. Лесная, 14        ] │
├─────────────────────────────┤
│ КВАРТИРА *    ПОДЪЕЗД       │
│ [    47  ]    [    3    ]   │ ← 2 col grid
├─────────────────────────────┤
│ ЭТАЖ          ДОМОФОН       │
│ [    6   ]    [   47В   ]   │
├─────────────────────────────┤
│ КОММЕНТАРИЙ КУРЬЕРУ         │
│ [Позвоните за 10 минут…   ] │ ← textarea, 3 rows
├─────────────────────────────┤
│ 🚴 Курьер может позвонить…  │ ← hint bubble accent_soft
├─────────────────────────────┤
│ [✓ Сохранить адрес]         │ ← sticky CTA
└─────────────────────────────┘
```

### Логика и валидация
```kotlin
data class ManualAddressForm(
    val street: String = "",
    val apt: String = "",       // обязательное
    val entrance: String = "",
    val floor: String = "",
    val intercom: String = "",
    val comment: String = "",
    val label: AddressLabel = AddressLabel.HOME,
)

enum class AddressLabel { HOME, WORK, OTHER }

fun validate(form: ManualAddressForm): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    if (form.street.isBlank()) errors["street"] = "Укажите улицу"
    if (form.apt.isBlank())    errors["apt"]    = "Обязательно"
    return errors
}
```

### UI-компоненты

**Step progress bar:** 2 бара flex, height 3dp, cornerRadius 2dp. Active: bg `ink`. Inactive: bg `line_2`. Label под каждым баром: 10sp, active: `ink` weight-600, inactive: `ink_3`.

**Search input (шаг 1):** height 50dp, cornerRadius 14dp, border 1.5dp (active: `ink`, default: `line`). Autofocus. Search icon left, clear button right.

**Suggest dropdown:** margin-top 8dp. cornerRadius 14dp, border 1dp `line`, shadow. Items: pin icon + main text 14sp weight-500 + secondary 12sp `ink_3`. Chevron right.

**"Ввести вручную" button:** padding 14dp 16dp, cornerRadius 14dp, border 1dp `line`. Edit icon box 38×38dp cornerRadius 10dp bg `surface_2`. Title 14sp weight-500, subtitle 12sp `ink_3`.

**Recent address row:** padding 12dp 0, border-bottom 1dp `line`. Pin icon + address text + chevron.

**Label picker (шаг 2):** 3 кнопки flex с gap 8dp. Height 48dp each, cornerRadius 12dp. Active: bg `ink`, color `bg`, border `ink`. Inactive: bg `surface`, color `ink`, border `line`. Emoji icon + label text 13sp weight-500. Transition 150ms.

**Form fields:** height 48dp, cornerRadius 12dp, border 1.5dp (focus: `ink`, error: `danger`, default: `line`). Font 15sp. Error label: color `danger`, суффикс "— текст ошибки".

**Textarea:** padding 12dp 14dp, cornerRadius 12dp, border 1.5dp. minHeight 80dp, resize none. Font 14sp, line-height 1.5.

**Hint bubble:** padding 12dp 14dp, cornerRadius 12dp, bg `accent_soft`. Emoji 16sp + text 12sp `accent_deep` line-height 1.5.

**Success screen:** 90×90dp circle bg `accent_soft` → 56×56dp circle bg `accent` → check icon 26dp white. Title `Fraunces 28sp weight-500`. Address text 14sp `ink_2`. Label badge: 13sp, bg `surface_2`, emoji + label.

### Suggest API (Yandex)
```kotlin
// Retrofit interface
interface YandexSuggestApi {
    @GET("v1/suggest")
    suspend fun suggest(
        @Query("apikey") apikey: String = API_KEY,
        @Query("text") text: String,
        @Query("lang") lang: String = "ru",
        @Query("results") results: Int = 6,
        @Query("bbox") bbox: String = "36.8,55.1~38.0,56.2"
    ): SuggestResponse
}

// Base URL: https://suggest-maps.yandex.ru/
// Вызывай с debounce 250ms при каждом onChange
```

---

## Дизайн-токены (одинаковые для всех экранов)

```kotlin
object GradkaColors {
    val Bg         = Color(0xFFFAFAF7)
    val Surface    = Color(0xFFFFFFFF)
    val Surface2   = Color(0xFFF2EFE8)
    val Surface3   = Color(0xFFE8E4DB)
    val Ink        = Color(0xFF1A1A17)
    val Ink2       = Color(0xFF4A4A44)
    val Ink3       = Color(0xFF8A8A82)
    val Line       = Color(0xFFE5E2D8)
    val Line2      = Color(0xFFD6D2C6)
    val Accent     = Color(0xFF5C8C49)  // oklch(0.63 0.12 125)
    val AccentSoft = Color(0xFFEEF4E8)
    val AccentDeep = Color(0xFF3A6030)
    val Danger     = Color(0xFFD4532A)

    // Dark theme
    val BgDark         = Color(0xFF0F0F0D)
    val SurfaceDark    = Color(0xFF181815)
    val Surface2Dark   = Color(0xFF222220)
    val InkDark        = Color(0xFFF2F1EC)
    val Ink2Dark       = Color(0xFFBDBCB4)
    val Ink3Dark       = Color(0xFF7A7970)
    val AccentDark     = Color(0xFF76B05C)
}

object GradkaShape {
    val sm = RoundedCornerShape(10.dp)
    val md = RoundedCornerShape(14.dp)
    val lg = RoundedCornerShape(20.dp)
    val xl = RoundedCornerShape(28.dp)
    val pill = RoundedCornerShape(50)
}
```

## Зависимости Android (build.gradle)

```kotlin
dependencies {
    // Yandex MapKit
    implementation("com.yandex.android:maps.mobile:4.6.1-lite")
    // или полная версия:
    // implementation("com.yandex.android:maps.mobile:4.6.1-full")

    // HTTP (для Suggest/Geocoder REST API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Location (геолокация)
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
```

## AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

## Инициализация MapKit

```kotlin
// Application.onCreate()
MapKitFactory.setApiKey("4139e44d-4325-4707-a507-02a0ab44fea6")
MapKitFactory.initialize(this)
```

## Файлы в этом бандле

| Файл | Описание |
|---|---|
| `Карты.html` | Интерактивный прототип всех трёх экранов |
| `Грядка.html` | Основной прототип приложения (12 экранов) |
| `README.md` | Документация основного приложения |
| `README_MAPS.md` | Этот файл — документация по экранам карт |
| `styles.css` | Дизайн-токены (CSS) |
| `data.jsx` | Данные каталога |
| `ui.jsx` | Базовые компоненты |
| `screens-shop.jsx` | Экраны магазина |
| `screens-flow.jsx` | Экраны корзины, профиля и т.д. |
