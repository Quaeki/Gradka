package com.example.gradka.domain

import javax.inject.Inject

class SendOtpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String): Int = repository.sendCode(phone)
}
