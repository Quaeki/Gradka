package com.example.gradka.domain

import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String, name: String) {
        repository.saveSession(phone = phone, name = name)
    }
}
