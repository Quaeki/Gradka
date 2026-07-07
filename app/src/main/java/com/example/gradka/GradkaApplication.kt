package com.example.gradka

import android.app.Application
import com.example.gradka.domain.SyncCatalogUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GradkaApplication : Application() {

    @Inject
    lateinit var syncCatalogUseCase: SyncCatalogUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Каталог обновляется в фоне; при недоступном сервере остаётся встроенный список.
        applicationScope.launch {
            runCatching { syncCatalogUseCase() }
        }
    }
}
