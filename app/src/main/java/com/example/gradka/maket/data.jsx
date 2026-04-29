// data.jsx — catalog data for Грядка

const PRODUCTS = [
  { id: 'p1',  name: 'Молоко фермерское',     subtitle: 'Рузское · 3,6%',        price: 189, unit: '1 л',    cat: 'dairy',   hue: 52,  badge: null,  farm: 'Рузское поле' },
  { id: 'p2',  name: 'Яйца деревенские',      subtitle: 'Перепёлка · C1',         price: 249, unit: '10 шт', cat: 'dairy',   hue: 38,  badge: 'new', farm: 'Ферма Ильиных' },
  { id: 'p3',  name: 'Хлеб ржаной',           subtitle: 'На закваске',            price: 159, unit: '450 г', cat: 'bakery',  hue: 28,  badge: null,  farm: 'Пекарня Квашня' },
  { id: 'p4',  name: 'Творог зернёный',       subtitle: '5% · без добавок',       price: 219, unit: '200 г', cat: 'dairy',   hue: 48,  badge: '-15%',farm: 'Рузское поле' },
  { id: 'p5',  name: 'Помидоры розовые',      subtitle: 'Бакинские',              price: 389, unit: '1 кг',  cat: 'veg',     hue: 12,  badge: null,  farm: 'Теплицы Юга' },
  { id: 'p6',  name: 'Огурцы грунтовые',      subtitle: 'Фермерские',             price: 229, unit: '1 кг',  cat: 'veg',     hue: 132, badge: null,  farm: 'Теплицы Юга' },
  { id: 'p7',  name: 'Яблоки Антоновка',      subtitle: 'Сезон',                   price: 149, unit: '1 кг',  cat: 'fruit',   hue: 95,  badge: 'сезон',farm: 'Сад Мещёра' },
  { id: 'p8',  name: 'Груша конференц',       subtitle: 'Сладкая',                price: 299, unit: '1 кг',  cat: 'fruit',   hue: 78,  badge: null,  farm: 'Сад Мещёра' },
  { id: 'p9',  name: 'Масло сливочное',       subtitle: '82,5% · традиционное',   price: 329, unit: '180 г', cat: 'dairy',   hue: 52,  badge: null,  farm: 'Рузское поле' },
  { id: 'p10', name: 'Сыр Качотта',           subtitle: 'Выдержка 30 дней',       price: 649, unit: '200 г', cat: 'dairy',   hue: 48,  badge: null,  farm: 'Сырная артель' },
  { id: 'p11', name: 'Говядина лопатка',      subtitle: 'Мраморная',              price: 899, unit: '500 г', cat: 'meat',    hue: 8,   badge: null,  farm: 'Калужский мясник' },
  { id: 'p12', name: 'Курица цельная',        subtitle: 'Фермерская',             price: 459, unit: '1,2 кг',cat: 'meat',    hue: 32,  badge: '-10%',farm: 'Фермер Петров' },
  { id: 'p13', name: 'Мёд липовый',           subtitle: 'Башкирский',             price: 549, unit: '500 г', cat: 'pantry',  hue: 60,  badge: null,  farm: 'Пасека Ивановых' },
  { id: 'p14', name: 'Авокадо Хасс',          subtitle: 'Спелое',                  price: 199, unit: '1 шт',  cat: 'fruit',   hue: 110, badge: null,  farm: 'Импорт · Мексика' },
  { id: 'p15', name: 'Зелень микс',           subtitle: 'Укроп, петрушка, лук',   price: 129, unit: '100 г', cat: 'veg',     hue: 140, badge: null,  farm: 'Теплицы Юга' },
  { id: 'p16', name: 'Лосось филе',           subtitle: 'Охлаждённый',            price: 1290,unit: '500 г', cat: 'fish',    hue: 18,  badge: null,  farm: 'Мурманский порт' },
];

const CATEGORIES = [
  { id: 'all',    label: 'Всё',       hue: 125 },
  { id: 'veg',    label: 'Овощи',     hue: 135 },
  { id: 'fruit',  label: 'Фрукты',    hue: 100 },
  { id: 'dairy',  label: 'Молочное',  hue: 50  },
  { id: 'bakery', label: 'Выпечка',   hue: 28  },
  { id: 'meat',   label: 'Мясо',      hue: 10  },
  { id: 'fish',   label: 'Рыба',      hue: 200 },
  { id: 'pantry', label: 'Бакалея',   hue: 65  },
];

const RECIPES = [
  { id: 'r1', title: 'Паста с томатами и базиликом', time: '25 мин', items: 7, hue: 12 },
  { id: 'r2', title: 'Запечённый лосось с лимоном',  time: '35 мин', items: 5, hue: 18 },
  { id: 'r3', title: 'Салат с авокадо и яйцом',      time: '15 мин', items: 6, hue: 110 },
  { id: 'r4', title: 'Сырники классические',         time: '20 мин', items: 4, hue: 48 },
];

const ORDERS = [
  { id: 'o1', date: '12 апр', n: '№ 24809', status: 'В пути', total: 2840, items: 8 },
  { id: 'o2', date: '8 апр',  n: '№ 24712', status: 'Доставлен', total: 3540, items: 12 },
  { id: 'o3', date: '1 апр',  n: '№ 24610', status: 'Доставлен', total: 1890, items: 6 },
];

const ADDRESSES = [
  { id: 'a1', label: 'Дом', text: 'ул. Лесная, 14, кв. 47', note: 'Код домофона 47В', primary: true },
  { id: 'a2', label: 'Работа', text: 'Пресненская наб., 12', note: 'БЦ Империя, вход с реки' },
];

Object.assign(window, { PRODUCTS, CATEGORIES, RECIPES, ORDERS, ADDRESSES });
