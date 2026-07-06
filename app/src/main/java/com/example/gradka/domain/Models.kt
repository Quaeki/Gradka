package com.example.gradka.domain

/**
 * Товар из каталога фермерских продуктов.
 *
 * @property id Уникальный идентификатор товара.
 * @property name Название товара (например, «Молоко фермерское»).
 * @property subtitle Краткое описание или характеристика (например, «Рузское · 3,6%»).
 * @property price Цена в рублях за единицу.
 * @property unit Единица измерения (например, «1 л», «1 кг», «10 шт»).
 * @property cat Идентификатор категории (например, «dairy», «veg», «fruit»).
 * @property hue Цветовой оттенок (0–360) для визуального выделения карточки товара.
 * @property badge Ярлык акции или новинки (например, «-15%», «new»); null — если ярлыка нет.
 * @property farm Название фермерского хозяйства-производителя.
 */
data class Product(
    val id: String,
    val name: String,
    val subtitle: String,
    val price: Int,
    val unit: String,
    val cat: String,
    val hue: Float,
    val badge: String?,
    val farm: String,
)

/**
 * Категория товаров в каталоге.
 *
 * @property id Уникальный идентификатор категории (например, «veg», «dairy»).
 * @property label Отображаемое название категории (например, «Овощи», «Молочное»).
 * @property hue Цветовой оттенок (0–360) для иконки категории.
 */
data class Category(val id: String, val label: String, val hue: Float)

/**
 * Рецепт блюда, приготовленного из продуктов каталога.
 *
 * @property id Уникальный идентификатор рецепта.
 * @property title Название блюда.
 * @property time Примерное время приготовления (например, «25 мин»).
 * @property items Количество ингредиентов в рецепте.
 * @property hue Цветовой оттенок карточки рецепта.
 */
data class Recipe(val id: String, val title: String, val time: String, val items: Int, val hue: Float)

/**
 * Заказ пользователя.
 *
 * @property id Уникальный идентификатор заказа.
 * @property date Дата оформления заказа в читаемом формате (например, «12 апр»).
 * @property number Номер заказа для отображения (например, «№ 24809»).
 * @property status Текущий статус заказа (например, «В пути», «Доставлен»).
 * @property total Итоговая сумма заказа в рублях.
 * @property items Количество позиций в заказе.
 */
data class Order(
    val id: String,
    val date: String,
    val number: String,
    val status: String,
    val total: Int,
    val items: Int,
)

/**
 * Адрес доставки пользователя.
 *
 * @property id Уникальный идентификатор адреса.
 * @property label Краткое название адреса (например, «Дом», «Работа»).
 * @property text Полный адрес в текстовом виде.
 * @property note Дополнительные инструкции для курьера (этаж, код домофона и т.д.).
 * @property primary Признак основного адреса доставки; по умолчанию false.
 */
data class Address(
    val id: String,
    val label: String,
    val text: String,
    val note: String,
    val primary: Boolean = false,
)

/**
 * Подписка на регулярную доставку товара.
 *
 * @property id Уникальный идентификатор подписки.
 * @property productId Идентификатор товара, который доставляется по подписке.
 * @property qty Количество единиц товара за одну доставку.
 * @property frequencyDays Периодичность доставки в днях (например, 3 — каждые 3 дня).
 * @property nextDelivery Дата следующей доставки в читаемом формате.
 * @property active Признак активности подписки; по умолчанию true.
 */
data class Subscription(
    val id: String,
    val productId: String,
    val qty: Int,
    val frequencyDays: Int,
    val nextDelivery: String,
    val active: Boolean = true,
)

/**
 * Сохранённый способ оплаты (банковская карта).
 *
 * @property id Уникальный идентификатор способа оплаты.
 * @property last4 Последние 4 цифры номера карты.
 * @property brand Платёжная система (например, «Visa», «MasterCard», «Мир»).
 * @property expiryMonth Месяц истечения срока действия карты (1–12).
 * @property expiryYear Год истечения срока действия карты.
 * @property isDefault Признак карты по умолчанию; по умолчанию false.
 * @property createdAtMillis Время добавления карты в миллисекундах (Unix timestamp).
 */
data class PaymentMethod(
    val id: String,
    val last4: String,
    val brand: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val isDefault: Boolean = false,
    val createdAtMillis: Long,
)
