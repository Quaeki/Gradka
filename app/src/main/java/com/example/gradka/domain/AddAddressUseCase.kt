package com.example.gradka.domain

import com.example.gradka.data.Address

class AddAddressUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(address: Address){
        repository.addAddress(address)
    }
}