package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для отправки OTP-кода по SMS. Возвращает секунды до повторной отправки. */
class SendOtpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String): Int = repository.sendCode(phone)
}
