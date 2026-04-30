package com.example.gradka.domain

import javax.inject.Inject

class SetPrimaryAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        addressId: String
    ){
        repository.setPrimaryAddress(addressId)
    }
}
