package com.example.gradka.domain

import javax.inject.Inject

class AddAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(address: Address){
        repository.addAddress(address)
    }
}
