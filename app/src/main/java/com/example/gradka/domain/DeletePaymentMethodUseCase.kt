package com.example.gradka.domain

class DeletePaymentMethodUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(id: String) {
        repository.deletePaymentMethod(id)
    }
}
