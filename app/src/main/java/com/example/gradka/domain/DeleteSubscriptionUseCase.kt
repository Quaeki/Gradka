package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для удаления подписки по идентификатору. */
class DeleteSubscriptionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteSubscription(id)
    }
}
