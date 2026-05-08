package com.example.gradka.domain

import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.clearSession()
    }
}
