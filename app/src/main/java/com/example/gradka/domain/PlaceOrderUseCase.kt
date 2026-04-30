package com.example.gradka.domain

import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        cart: Map<String, Int>,
        addressId: String
    ){
        repository.placeOrder(cart, addressId)
    }
}
