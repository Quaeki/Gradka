package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Address>>{
        return repository.getAddresses()
    }
}
