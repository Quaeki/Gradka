package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для получения адресных подсказок при ручном вводе адреса доставки. */
class SuggestAddressesUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(query: String): List<AddressSuggestion> {
        return repository.suggestAddresses(query)
    }
}
