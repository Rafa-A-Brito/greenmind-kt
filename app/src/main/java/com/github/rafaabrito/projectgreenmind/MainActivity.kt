package com.github.rafaabrito.projectgreenmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.rafaabrito.projectgreenmind.ui.components.BottomBarComponent
import com.github.rafaabrito.projectgreenmind.ui.components.NavigationBarItems
import com.github.rafaabrito.projectgreenmind.ui.screens.CommunityScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.EcoScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.EcoTasksScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.HomeScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.LoginScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.PresentationScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.ProfileScreen
import com.github.rafaabrito.projectgreenmind.ui.screens.RegisterScreen
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
                val rootNavController = rememberNavController()
                val context = LocalContext.current
                NavHost(
                    navController = rootNavController,
                    startDestination = Presentation
                ) {
                    // Rota da Apresentação
                    composable<Presentation> {
                        PresentationScreen(
                            onNavigateToHome = {
                                // Navegação para Home
                                rootNavController.navigate(Login) {
                                    // Remove Presentation da 'back stack' para Login ser a tela principal
                                    popUpTo<Presentation> { inclusive = true }
                                }
                            },
                            onExit = {
                                (context as ComponentActivity).finish()
                            }
                        )
                    }

                    composable<Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                rootNavController.navigate(MainAppGraph) {
                                    popUpTo<Login> { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                rootNavController.navigate(Register)
                            }
                        )
                    }

                    composable<Register> {
                        RegisterScreen()
                    }

                    composable<MainAppGraph> {
                        // O NavController gerencia internamente
                        MainScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen() {

    val mainAppNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBarComponent(navController = mainAppNavController)
        },

        ) { paddingValues ->
        // O NavHost é o contentor que renderiza a tela atual
        NavHost(
            navController = mainAppNavController,
            startDestination = NavigationBarItems.House.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavigationBarItems.House.route) {
                HomeScreen()
            }
            composable(NavigationBarItems.Local.route) {
                EcoScreen()
            }
            composable(NavigationBarItems.Trophy.route) {
                EcoTasksScreen()
            }
            composable(NavigationBarItems.Community.route) {
                CommunityScreen()
            }
            composable(NavigationBarItems.Person.route) {
                ProfileScreen()
            }
        }
    }
}

@Serializable
object Login
@Serializable
object Register
@Serializable
object Presentation
// Rota que leva ao MainScreen (Scaffold + bottomBar)
@Serializable
object MainAppGraph