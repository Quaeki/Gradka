package com.example.gradka.domain

import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateSubscriptionUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(
        id: String,
        qty: Int? = null,
        frequencyDays: Int? = null,
        active: Boolean? = null,
    ) {
        val current = repository.getSubscriptions().first().firstOrNull { it.id == id } ?: return
        val normalizedFrequency = frequencyDays?.coerceAtLeast(1)

        repository.updateSubscription(
            current.copy(
                qty = qty?.coerceAtLeast(1) ?: current.qty,
                frequencyDays = normalizedFrequency ?: current.frequencyDays,
                active = active ?: current.active,
                nextDelivery = if (normalizedFrequency != null) {
                    nextDeliveryLabel(normalizedFrequency)
                } else {
                    current.nextDelivery
                },
            )
        )
    }
}
