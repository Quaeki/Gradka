package com.example.gradka.domain

import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(phone: String, code: String): Boolean {
        return repository.verifyOtp(phone, code)
    }
}
