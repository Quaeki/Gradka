package com.example.gradka.domain

import javax.inject.Inject

class SuggestAddressesUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(query: String): List<AddressSuggestion> {
        return repository.suggestAddresses(query)
    }
}
