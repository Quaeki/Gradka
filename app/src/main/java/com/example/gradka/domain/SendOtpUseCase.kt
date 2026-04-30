package com.example.gradka.domain

import javax.inject.Inject

class SendOtpUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String) {
        repository.sendOtp(phone)
    }
}
