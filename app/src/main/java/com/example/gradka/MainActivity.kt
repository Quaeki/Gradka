package com.example.gradka

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.gradka.ui.theme.GradkaTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

private const val SPLASH_MIN_VISIBLE_MS = 600L

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install before super.onCreate so the system splash shows immediately.
        val splashScreen = installSplashScreen()
        val splashShownAt = SystemClock.uptimeMillis()
        splashScreen.setKeepOnScreenCondition {
            // Hold the system splash for a short minimum so the brand flashes
            // long enough on fast devices and merges into the in-app SplashContent.
            SystemClock.uptimeMillis() - splashShownAt < SPLASH_MIN_VISIBLE_MS
        }

        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPS_KEY)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
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
