package com.example.gradka.domain

class SendOtpUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String) {
        repository.sendOtp(phone)
    }
}
