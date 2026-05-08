package com.example.gradka.domain

import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String, code: String): AuthResult =
        repository.verifyCode(phone, code)
}
