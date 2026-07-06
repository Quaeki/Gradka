// Server-side price list. Must stay in sync with the in-app catalog
// (app/src/main/java/com/example/gradka/domain/MockData.kt): order totals are
// always computed from these prices, never from values sent by the client.
const CATALOG = [
  { id: "p1", name: "Молоко фермерское", price: 189 },
  { id: "p2", name: "Яйца деревенские", price: 249 },
  { id: "p3", name: "Хлеб ржаной", price: 159 },
  { id: "p4", name: "Творог зернёный", price: 219 },
  { id: "p5", name: "Помидоры розовые", price: 389 },
  { id: "p6", name: "Огурцы грунтовые", price: 229 },
  { id: "p7", name: "Яблоки Антоновка", price: 149 },
  { id: "p8", name: "Груша конференц", price: 299 },
  { id: "p9", name: "Масло сливочное", price: 329 },
  { id: "p10", name: "Сыр Качотта", price: 649 },
  { id: "p11", name: "Говядина лопатка", price: 899 },
  { id: "p12", name: "Курица цельная", price: 459 },
  { id: "p13", name: "Мёд липовый", price: 549 },
  { id: "p14", name: "Авокадо Хасс", price: 199 },
  { id: "p15", name: "Зелень микс", price: 129 },
  { id: "p16", name: "Лосось филе", price: 1290 },
];

module.exports = { CATALOG };
