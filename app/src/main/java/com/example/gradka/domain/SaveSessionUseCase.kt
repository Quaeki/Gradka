package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для сохранения сессии в зашифрованное хранилище после успешного входа. */
class SaveSessionUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(phone: String, name: String) {
        repository.saveLocalSession(phone = phone, name = name)
    }
}
