package com.example.gradka.domain

class SuggestAddressesUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(query: String): List<AddressSuggestion> {
        return repository.suggestAddresses(query)
    }
}
