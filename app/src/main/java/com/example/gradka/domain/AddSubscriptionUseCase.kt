package com.example.gradka.domain

import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(productId: String, qty: Int, frequencyDays: Int) {
        val current = repository.getSubscriptions().first().toList()
        if (current.any { it.productId == productId }) return

        val normalizedFrequency = frequencyDays.coerceAtLeast(1)
        repository.addSubscription(
            Subscription(
                id = nextSubscriptionId(current),
                productId = productId,
                qty = qty.coerceAtLeast(1),
                frequencyDays = normalizedFrequency,
                nextDelivery = nextDeliveryLabel(normalizedFrequency),
            )
        )
    }

    private fun nextSubscriptionId(subscriptions: List<Subscription>): String {
        val maxId = subscriptions.maxOfOrNull {
            it.id.removePrefix("s").toIntOrNull() ?: 0
        } ?: 0
        return "s${maxId + 1}"
    }
}

internal fun nextDeliveryLabel(frequencyDays: Int): String {
    val days = listOf("вс", "пн", "вт", "ср", "чт", "пт", "сб")
    val months = listOf("янв", "фев", "мар", "апр", "мая", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, frequencyDays)
    val dow = days[(calendar.get(Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)]
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = months[calendar.get(Calendar.MONTH).coerceIn(0, 11)]
    return "$dow, $day $month"
}
