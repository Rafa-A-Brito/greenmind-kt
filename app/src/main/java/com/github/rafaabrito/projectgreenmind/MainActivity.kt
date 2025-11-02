package com.github.rafaabrito.projectgreenmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.HomeScreen
import com.github.rafaabrito.projectgreenmind.ui.theme.ProjectGreenMindTheme
import com.github.rafaabrito.projectgreenmind.ui.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen
            .apply {
                setKeepOnScreenCondition {
                    !viewModel.isReady.value
                }
            }
        setContent {
                ProjectGreenMindTheme {
                    HomeScreen()
                }
            }
        }
    }