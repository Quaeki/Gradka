package com.example.gradka.domain

class PlaceOrderUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        cart: Map<String, Int>,
        addressId: String
    ){
        repository.placeOrder(cart, addressId)
    }
}