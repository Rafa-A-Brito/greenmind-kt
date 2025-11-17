package com.github.rafaabrito.projectgreenmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.rafaabrito.projectgreenmind.ui.screens.HomeScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.LoginScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.PresentationScreen
import com.github.rafaabrito.projectgreenmind.ui.theme.ProjectGreenMindTheme
import com.github.rafaabrito.projectgreenmind.ui.viewModel.MainViewModel
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }
        setContent {
            ProjectGreenMindTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                NavHost(
                    navController = navController,
                    startDestination = Presentation
                ) {
                    // Rota da Apresentação
                    composable<Presentation> {
                        PresentationScreen(
                            onNavigateToHome = {
                                // Navegação para Home
                                navController.navigate(Login) {
                                    // Remove Presentation da 'back stack' para Login ser a tela principal
                                    popUpTo<Presentation> { inclusive = true }
                                }
                            },
                            onExit = {
                                // Lógica para fechar o aplicativo
                                (context as ComponentActivity).finish()
                            }
                        )
                    }

                    // Rota da Home
                    composable<Login> {
                        LoginScreen()
                    }
                }
            }
        }
    }
}
@Serializable
object Login
@Serializable
object Presentation