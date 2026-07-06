package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для назначения адреса основным (снимает флаг primary со всех остальных). */
class SetPrimaryAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        addressId: String
    ){
        repository.setPrimaryAddress(addressId)
    }
}
