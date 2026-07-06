package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для оформления заказа. Делегирует вызов в [GradkaRepository.placeOrder]. */
class PlaceOrderUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        cart: Map<String, Int>,
        addressId: String
    ){
        repository.placeOrder(cart, addressId)
    }
}
