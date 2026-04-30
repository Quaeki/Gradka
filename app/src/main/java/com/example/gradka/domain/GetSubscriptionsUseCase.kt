package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

class GetSubscriptionsUseCase(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getSubscriptions()
    }
}
