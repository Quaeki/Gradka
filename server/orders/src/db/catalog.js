// Fallback price list used until the Saby (СБИС) catalog sync takes over.
// Mirrors the in-app mock catalog (app/src/main/java/com/example/gradka/domain/MockData.kt):
// order totals are always computed from these prices, never from client values.
const CATALOG = [
  { id: "p1",  name: "Молоко фермерское", price: 189,  subtitle: "Рузское · 3,6%",       unit: "1 л",    category: "dairy",  hue: 52,  badge: null,    farm: "Рузское поле" },
  { id: "p2",  name: "Яйца деревенские",  price: 249,  subtitle: "Перепёлка · C1",        unit: "10 шт",  category: "dairy",  hue: 38,  badge: "new",   farm: "Ферма Ильиных" },
  { id: "p3",  name: "Хлеб ржаной",       price: 159,  subtitle: "На закваске",           unit: "450 г",  category: "bakery", hue: 28,  badge: null,    farm: "Пекарня Квашня" },
  { id: "p4",  name: "Творог зернёный",   price: 219,  subtitle: "5% · без добавок",      unit: "200 г",  category: "dairy",  hue: 48,  badge: "-15%",  farm: "Рузское поле" },
  { id: "p5",  name: "Помидоры розовые",  price: 389,  subtitle: "Бакинские",             unit: "1 кг",   category: "veg",    hue: 12,  badge: null,    farm: "Теплицы Юга" },
  { id: "p6",  name: "Огурцы грунтовые",  price: 229,  subtitle: "Фермерские",            unit: "1 кг",   category: "veg",    hue: 132, badge: null,    farm: "Теплицы Юга" },
  { id: "p7",  name: "Яблоки Антоновка",  price: 149,  subtitle: "Сезон",                 unit: "1 кг",   category: "fruit",  hue: 95,  badge: "сезон", farm: "Сад Мещёра" },
  { id: "p8",  name: "Груша конференц",   price: 299,  subtitle: "Сладкая",               unit: "1 кг",   category: "fruit",  hue: 78,  badge: null,    farm: "Сад Мещёра" },
  { id: "p9",  name: "Масло сливочное",   price: 329,  subtitle: "82,5% · традиционное",  unit: "180 г",  category: "dairy",  hue: 52,  badge: null,    farm: "Рузское поле" },
  { id: "p10", name: "Сыр Качотта",       price: 649,  subtitle: "Выдержка 30 дней",      unit: "200 г",  category: "dairy",  hue: 48,  badge: null,    farm: "Сырная артель" },
  { id: "p11", name: "Говядина лопатка",  price: 899,  subtitle: "Мраморная",             unit: "500 г",  category: "meat",   hue: 8,   badge: null,    farm: "Калужский мясник" },
  { id: "p12", name: "Курица цельная",    price: 459,  subtitle: "Фермерская",            unit: "1,2 кг", category: "meat",   hue: 32,  badge: "-10%",  farm: "Фермер Петров" },
  { id: "p13", name: "Мёд липовый",       price: 549,  subtitle: "Башкирский",            unit: "500 г",  category: "pantry", hue: 60,  badge: null,    farm: "Пасека Ивановых" },
  { id: "p14", name: "Авокадо Хасс",      price: 199,  subtitle: "Спелое",                unit: "1 шт",   category: "fruit",  hue: 110, badge: null,    farm: "Импорт · Мексика" },
  { id: "p15", name: "Зелень микс",       price: 129,  subtitle: "Укроп, петрушка, лук",  unit: "100 г",  category: "veg",    hue: 140, badge: null,    farm: "Теплицы Юга" },
  { id: "p16", name: "Лосось филе",       price: 1290, subtitle: "Охлаждённый",           unit: "500 г",  category: "fish",   hue: 18,  badge: null,    farm: "Мурманский порт" },
];

module.exports = { CATALOG };