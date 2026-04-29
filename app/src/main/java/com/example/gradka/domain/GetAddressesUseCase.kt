package com.example.gradka.domain

import com.example.gradka.data.Address
import kotlinx.coroutines.flow.Flow

class GetAddressesUseCase(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Address>>{
        return repository.getAddresses()
    }
}