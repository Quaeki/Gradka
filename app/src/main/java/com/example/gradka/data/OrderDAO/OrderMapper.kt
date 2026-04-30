package com.example.gradka.data.OrderDAO

import com.example.gradka.domain.Order

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
