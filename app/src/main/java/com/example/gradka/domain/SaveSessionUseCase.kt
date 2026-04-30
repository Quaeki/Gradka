package com.example.gradka.domain

class SaveSessionUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String, name: String) {
        repository.saveSession(phone = phone, name = name)
    }
}
