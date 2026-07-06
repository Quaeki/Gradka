package com.example.gradka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.gradka.domain.CalculateCartSummaryUseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Синглтон-хранилище состояния корзины покупок.
 *
 * Хранит текущий набор товаров с их количеством в виде Compose-state,
 * что обеспечивает реактивное обновление UI без Flow.
 * Вычисление итогов делегируется [CalculateCartSummaryUseCase].
 */
@Singleton
class CartStore @Inject constructor(
    private val calculateCartSummaryUseCase: CalculateCartSummaryUseCase,
) {
    /** Внутренняя карта: productId → количество единиц в корзине. */
    private val cartItems = mutableStateMapOf<String, Int>()

    /** Публичная неизменяемая копия содержимого корзины. */
    val cart: Map<String, Int>
        get() = cartItems.toMap()

    /**
     * Добавляет одну единицу товара в корзину.
     *
     * @param productId Идентификатор товара.
     */
    fun addToCart(productId: String) {
        cartItems[productId] = (cartItems[productId] ?: 0) + 1
    }

    /**
     * Уменьшает количество товара в корзине на 1.
     * Если количество становится 0, товар удаляется из корзины.
     *
     * @param productId Идентификатор товара.
     */
    fun subFromCart(productId: String) {
        val current = cartItems[productId] ?: 0
        if (current <= 1) cartItems.remove(productId) else cartItems[productId] = current - 1
    }

    /** Полностью очищает корзину (используется после успешного оформления заказа). */
    fun clearCart() {
        cartItems.clear()
    }

    private val summary get() = calculateCartSummaryUseCase(cartItems.toMap())

    /** Общее количество единиц товара в корзине. */
    val cartCount: Int get() = summary.count

    /** Стоимость товаров без учёта доставки (в рублях). */
    val cartSubtotal: Int get() = summary.subtotal

    /** Стоимость доставки (в рублях). */
    val cartDelivery: Int get() = summary.delivery

    /** Итоговая сумма к оплате (товары + доставка, в рублях). */
    val cartTotal: Int get() = summary.total
}

/**
 * Синглтон-хранилище избранных товаров пользователя.
 *
 * Хранит набор идентификаторов товаров, добавленных в избранное.
 * Использует Compose State для реактивного обновления UI.
 */
@Singleton
class FavoriteStore @Inject constructor() {
    /** Набор идентификаторов товаров в избранном. */
    var favs by mutableStateOf(setOf<String>())
        private set

    /**
     * Переключает состояние товара в избранном:
     * добавляет если отсутствует, удаляет если присутствует.
     *
     * @param productId Идентификатор товара.
     */
    fun toggleFav(productId: String) {
        favs = if (favs.contains(productId)) favs - productId else favs + productId
    }
}

/**
 * Синглтон-хранилище выбранного фильтра каталога.
 *
 * Хранит идентификатор активной категории каталога.
 * По умолчанию выбрана категория «all» (все товары).
 */
@Singleton
class CatalogFilterStore @Inject constructor() {
    /** Идентификатор текущей активной категории (например, «veg», «dairy», «all»). */
    var catFilter by mutableStateOf("all")
}

/**
 * Синглтон-хранилище дополнительных данных профиля пользователя.
 *
 * Хранит поля профиля, которые пользователь может заполнить по желанию:
 * email, дату рождения и пол.
 */
@Singleton
class ProfileStore @Inject constructor() {
    /** Email-адрес пользователя (может быть пустым). */
    var profileEmail by mutableStateOf("")
        private set

    /** Дата рождения пользователя в строковом формате (может быть пустой). */
    var profileBirthday by mutableStateOf("")
        private set

    /** Пол пользователя (может быть пустым). */
    var profileGender by mutableStateOf("")
        private set

    /**
     * Обновляет дополнительные данные профиля.
     *
     * @param email Новый email-адрес.
     * @param birthday Новая дата рождения.
     * @param gender Новый пол.
     */
    fun updateProfileExtras(email: String, birthday: String, gender: String) {
        profileEmail = email.trim()
        profileBirthday = birthday.trim()
        profileGender = gender
    }
}
