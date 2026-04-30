package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getSubscriptions()
    }
}
