package com.example.gradka.domain

class ClearSessionUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke() {
        repository.clearSession()
    }
}
