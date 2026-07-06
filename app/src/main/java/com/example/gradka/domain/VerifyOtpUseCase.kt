package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для проверки OTP-кода. При успехе возвращает [AuthResult] с токенами. */
class VerifyOtpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String, code: String): AuthResult =
        repository.verifyCode(phone, code)
}
