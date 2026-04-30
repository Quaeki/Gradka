package com.example.gradka.domain

class GetSessionUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(): UserSession? {
        return repository.getSession()
    }
}
