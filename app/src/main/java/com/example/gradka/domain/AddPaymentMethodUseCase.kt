package com.example.gradka.domain

import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.UUID

class AddPaymentMethodUseCase(
    private val repository: GradkaRepository,
    private val idProvider: () -> String = { UUID.randomUUID().toString() },
    private val timeProvider: () -> Long = { System.currentTimeMillis() },
) {
    fun createId(): String = idProvider()

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
                createdAtMillis = timeProvider(),
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
