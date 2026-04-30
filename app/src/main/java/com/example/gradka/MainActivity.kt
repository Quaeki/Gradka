package com.example.gradka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.gradka.data.GradkaRepositoryImpl
import com.example.gradka.ui.theme.GradkaTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPS_KEY)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        GradkaRepositoryImpl.getInstance(this).activity = this
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GradkaTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
