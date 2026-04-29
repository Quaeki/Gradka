package com.example.gradka.domain

class DeleteAddressUseCase(private val repository: GradkaRepository) {
    operator fun invoke(addressId: String) {
        repository.deleteAddress(addressId)
    }
}
