package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для выхода из аккаунта — удаляет локальную сессию пользователя. */
class ClearSessionUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.clearSession()
    }
}
