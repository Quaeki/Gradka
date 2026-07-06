package com.example.gradka.data.OrderDAO

import com.example.gradka.domain.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun OrderDbModel.toOrder(): Order =
    Order(
        id = id,
        date = date,
        number = number,
        status = status,
        total = total,
        items = items,
    )

fun Order.toDbModel(): OrderDbModel =
    OrderDbModel(
        id = id,
        date = date,
        number = number,
        status = status,
        total = total,
        items = items,
    )

fun RemoteOrderDto.toDbModel(): OrderDbModel =
    OrderDbModel(
        id = id,
        date = formatOrderDate(createdAtMillis),
        number = "№ $number",
        status = status.toDisplayStatus(),
        total = total,
        items = itemsCount,
    )

private fun formatOrderDate(createdAtMillis: Long): String =
    SimpleDateFormat("d MMM", Locale("ru")).format(Date(createdAtMillis))

private fun String.toDisplayStatus(): String = when (this) {
    "created" -> "Оформлен"
    "confirmed" -> "Подтверждён"
    "delivering" -> "В пути"
    "delivered" -> "Доставлен"
    "cancelled" -> "Отменён"
    else -> this
}
