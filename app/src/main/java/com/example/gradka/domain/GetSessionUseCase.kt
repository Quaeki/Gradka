package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для получения текущей локальной сессии пользователя (автологин при запуске). */
class GetSessionUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(): UserSession? {
        return repository.getSession()
    }
}
