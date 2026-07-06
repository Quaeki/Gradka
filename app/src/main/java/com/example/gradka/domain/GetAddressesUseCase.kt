package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use Case для получения реактивного потока с адресами доставки пользователя. */
class GetAddressesUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Address>>{
        return repository.getAddresses()
    }
}
