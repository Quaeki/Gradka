package com.example.gradka.domain

import javax.inject.Inject

class UpdateNameUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(name: String): AuthUser =
        repository.updateName(name)
}
