package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для загрузки каталога товаров с сервера (прайс-лист Saby/СБИС). */
class SyncCatalogUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke() {
        repository.syncCatalog()
    }
}
