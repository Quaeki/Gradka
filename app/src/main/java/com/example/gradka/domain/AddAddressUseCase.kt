package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для добавления нового адреса доставки. */
class AddAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(address: Address){
        repository.addAddress(address)
    }
}
