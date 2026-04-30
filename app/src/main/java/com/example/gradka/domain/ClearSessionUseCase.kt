package com.example.gradka.domain

import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke() {
        repository.clearSession()
    }
}
