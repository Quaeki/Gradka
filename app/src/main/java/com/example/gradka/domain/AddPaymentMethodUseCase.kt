package com.example.gradka.domain

import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class AddPaymentMethodUseCase @Inject constructor(
    private val repository: GradkaRepository,
) {
    fun createId(): String = UUID.randomUUID().toString()

    suspend operator fun invoke(
        id: String,
        last4: String,
        brand: String,
        expiryMonth: Int,
        expiryYear: Int,
    ): String? {
        val safeLast4 = last4.filter { it.isDigit() }.takeLast(4)
        if (safeLast4.length != 4 || !isValidExpiry(expiryMonth, expiryYear)) return null

        val current = repository.getPaymentMethods().first().toList()
        repository.addPaymentMethod(
            PaymentMethod(
                id = id,
                last4 = safeLast4,
                brand = brand.trim().ifBlank { "Карта" },
                expiryMonth = expiryMonth,
                expiryYear = expiryYear,
                isDefault = current.isEmpty(),
                createdAtMillis = System.currentTimeMillis(),
            )
        )
        return id
    }

    private fun isValidExpiry(month: Int, year: Int): Boolean {
        if (month !in 1..12) return false
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        return year > currentYear || (year == currentYear && month >= currentMonth)
    }
}
