package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke() : Flow<List<Order>> {
        return repository.getOrder()
    }
}
