package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

class GetPaymentMethodsUseCase(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<PaymentMethod>> {
        return repository.getPaymentMethods()
    }
}
