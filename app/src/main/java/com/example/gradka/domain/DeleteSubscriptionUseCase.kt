package com.example.gradka.domain

class DeleteSubscriptionUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteSubscription(id)
    }
}
