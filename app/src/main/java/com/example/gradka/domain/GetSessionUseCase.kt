package com.example.gradka.domain

import javax.inject.Inject

class GetSessionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(): UserSession? {
        return repository.getSession()
    }
}
