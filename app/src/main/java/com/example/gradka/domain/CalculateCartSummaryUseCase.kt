package com.example.gradka.domain

import javax.inject.Inject

data class CartSummary(
    val count: Int = 0,
    val subtotal: Int = 0,
    val delivery: Int = 0,
    val total: Int = 0,
)

class CalculateCartSummaryUseCase @Inject constructor() {
    operator fun invoke(cart: Map<String, Int>): CartSummary {
        val snapshot = cart.toMap()
        val subtotal = snapshot.entries.sumOf { (id, qty) ->
            val price = PRODUCTS.find { it.id == id }?.price ?: 0
            price * qty.coerceAtLeast(0)
        }
        val delivery = if (subtotal > FREE_DELIVERY_THRESHOLD) 0 else DELIVERY_PRICE

        return CartSummary(
            count = snapshot.values.sumOf { it.coerceAtLeast(0) },
            subtotal = subtotal,
            delivery = delivery,
            total = subtotal + delivery,
        )
    }

    private companion object {
        const val FREE_DELIVERY_THRESHOLD = 1500
        const val DELIVERY_PRICE = 149
    }
}
