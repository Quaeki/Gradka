package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use Case для получения реактивного потока с активными подписками пользователя. */
class GetSubscriptionsUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getSubscriptions()
    }
}
