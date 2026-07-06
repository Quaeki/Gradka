package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use Case для получения реактивного потока с сохранёнными способами оплаты. */
class GetPaymentMethodsUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<PaymentMethod>> {
        return repository.getPaymentMethods()
    }
}
