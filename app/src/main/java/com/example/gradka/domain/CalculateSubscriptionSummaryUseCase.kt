package com.example.gradka.domain


data class SubscriptionSummary(
    val activeCount: Int = 0,
    val monthlyTotal: Int = 0,
    val monthlySavings: Int = 0,
)

class CalculateSubscriptionSummaryUseCase {
    operator fun invoke(subscriptions: List<Subscription>): SubscriptionSummary {
        val snapshot = subscriptions.toList()
        val monthlyTotal = snapshot
            .filter { it.active }
            .sumOf { subscription ->
                val price = PRODUCTS.find { it.id == subscription.productId }?.price ?: 0
                price * subscription.qty * 30 / subscription.frequencyDays.coerceAtLeast(1)
            }

        return SubscriptionSummary(
            activeCount = snapshot.count { it.active },
            monthlyTotal = monthlyTotal,
            monthlySavings = monthlyTotal * 5 / 100,
        )
    }
}
