package com.example.gradka.domain

import javax.inject.Inject

class DeletePaymentMethodUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(id: String) {
        repository.deletePaymentMethod(id)
    }
}
