package com.example.bw_clock

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bw_clock.ui.theme.BW_clockTheme
import com.example.bw_clock.ui.theme.ClockBlack
import com.example.bw_clock.ui.theme.ClockWhite

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsRepository = SettingsRepository(applicationContext)

        // Fullscreen: hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val settings by settingsRepository.settingsFlow
                .collectAsState(initial = ClockSettings())
            var showSettings by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()
            val focusRequester = remember { FocusRequester() }

            val bgColor = if (settings.isDarkBackground) ClockBlack else ClockWhite

            BW_clockTheme(isDarkBackground = settings.isDarkBackground) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bgColor)
                ) {
                    // Clock (rotated)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = settings.rotation.toFloat()
                            }
                            .focusRequester(focusRequester)
                            .focusable()
                            .onKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                                when (event.key) {
                                    Key.Enter, Key.DirectionCenter, Key.Menu -> {
                                        if (!showSettings) {
                                            showSettings = true
                                            true
                                        } else false
                                    }
                                    Key.Back -> {
                                        if (showSettings) {
                                            showSettings = false
                                            true
                                        } else false
                                    }
                                    else -> false
                                }
                            }
                    ) {
                        ClockScreen(settings = settings)

                        if (showSettings) {
                            SettingsScreen(
                                settings = settings,
                                repository = settingsRepository,
                                coroutineScope = coroutineScope,
                                onDismiss = {
                                    showSettings = false
                                },
                                rotation = settings.rotation
                            )
                        }
                    }
                }
            }

            androidx.compose.runtime.LaunchedEffect(showSettings) {
                if (!showSettings) {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
