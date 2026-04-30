package com.example.gradka.domain


class AddAddressUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(address: Address){
        repository.addAddress(address)
    }
}