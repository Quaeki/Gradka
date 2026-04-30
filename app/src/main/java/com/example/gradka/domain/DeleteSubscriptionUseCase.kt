package com.example.gradka.domain

import javax.inject.Inject

class DeleteSubscriptionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteSubscription(id)
    }
}
