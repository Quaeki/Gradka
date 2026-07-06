package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для синхронизации истории заказов с сервером. */
class SyncOrdersUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke() {
        repository.syncOrders()
    }
}
