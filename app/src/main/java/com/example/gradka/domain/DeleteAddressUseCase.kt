package com.example.gradka.domain

import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(addressId: String) {
        repository.deleteAddress(addressId)
    }
}
