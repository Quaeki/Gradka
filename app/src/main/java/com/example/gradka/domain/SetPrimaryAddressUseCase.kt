package com.example.gradka.domain

class SetPrimaryAddressUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        addressId: String
    ){
        repository.setPrimaryAddress(addressId)
    }
}