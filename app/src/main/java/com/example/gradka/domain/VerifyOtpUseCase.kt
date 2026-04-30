package com.example.gradka.domain

class VerifyOtpUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String, code: String): Boolean {
        return repository.verifyOtp(phone, code)
    }
}
