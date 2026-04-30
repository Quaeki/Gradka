package com.example.gradka.data.BillingDAO

import com.example.gradka.data.PaymentMethod

fun BillingDbModel.toPaymentMethod(): PaymentMethod =
    PaymentMethod(
        id = id,
        last4 = last4,
        brand = brand,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        isDefault = isDefault,
        createdAtMillis = createdAtMillis,
    )

fun PaymentMethod.toBillingDbModel(): BillingDbModel =
    BillingDbModel(
        id = id,
        last4 = last4,
        brand = brand,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        isDefault = isDefault,
        createdAtMillis = createdAtMillis,
    )
