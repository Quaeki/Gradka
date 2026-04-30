package com.example.gradka.data.SubDAO

import com.example.gradka.data.Subscription

fun SubDbModel.toSubscription(): Subscription =
    Subscription(
        id = id,
        productId = productId,
        qty = qty,
        frequencyDays = frequencyDays,
        nextDelivery = nextDelivery,
        active = active,
    )

fun Subscription.toDbModel(): SubDbModel =
    SubDbModel(
        id = id,
        productId = productId,
        qty = qty,
        frequencyDays = frequencyDays,
        nextDelivery = nextDelivery,
        active = active,
    )
