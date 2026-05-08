package com.example.gradka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.gradka.domain.CalculateCartSummaryUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartStore @Inject constructor(
    private val calculateCartSummaryUseCase: CalculateCartSummaryUseCase,
) {
    private val cartItems = mutableStateMapOf<String, Int>()

    val cart: Map<String, Int>
        get() = cartItems.toMap()

    fun addToCart(productId: String) {
        cartItems[productId] = (cartItems[productId] ?: 0) + 1
    }

    fun subFromCart(productId: String) {
        val current = cartItems[productId] ?: 0
        if (current <= 1) cartItems.remove(productId) else cartItems[productId] = current - 1
    }

    fun clearCart() {
        cartItems.clear()
    }

    private val summary get() = calculateCartSummaryUseCase(cartItems.toMap())
    val cartCount: Int get() = summary.count
    val cartSubtotal: Int get() = summary.subtotal
    val cartDelivery: Int get() = summary.delivery
    val cartTotal: Int get() = summary.total
}

@Singleton
class FavoriteStore @Inject constructor() {
    var favs by mutableStateOf(setOf<String>())
        private set

    fun toggleFav(productId: String) {
        favs = if (favs.contains(productId)) favs - productId else favs + productId
    }
}

@Singleton
class CatalogFilterStore @Inject constructor() {
    var catFilter by mutableStateOf("all")
}

@Singleton
class ProfileStore @Inject constructor() {
    var profileEmail by mutableStateOf("")
        private set
    var profileBirthday by mutableStateOf("")
        private set
    var profileGender by mutableStateOf("")
        private set

    fun updateProfileExtras(email: String, birthday: String, gender: String) {
        profileEmail = email.trim()
        profileBirthday = birthday.trim()
        profileGender = gender
    }
}
