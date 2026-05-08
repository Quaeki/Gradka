package com.example.gradka.domain

import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String, name: String) {
        repository.saveLocalSession(phone = phone, name = name)
    }
}
