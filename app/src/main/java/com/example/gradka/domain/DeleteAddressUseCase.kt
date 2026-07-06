package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для удаления адреса доставки по идентификатору. */
class DeleteAddressUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(addressId: String) {
        repository.deleteAddress(addressId)
    }
}
