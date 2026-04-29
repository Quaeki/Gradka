package com.example.gradka.domain

import com.example.gradka.data.Order
import kotlinx.coroutines.flow.Flow

class GetOrderUseCase(private val repository: GradkaRepository) {
    operator fun invoke() : Flow<List<Order>> {
        return repository.getOrder()
    }
}