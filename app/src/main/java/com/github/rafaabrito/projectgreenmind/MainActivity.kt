package com.github.rafaabrito.projectgreenmind

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.rafaabrito.projectgreenmind.ui.components.BottomBarComponent
import com.github.rafaabrito.projectgreenmind.ui.components.NavigationBarItems
import com.github.rafaabrito.projectgreenmind.ui.screens.*
import com.github.rafaabrito.projectgreenmind.ui.theme.ProjectGreenMindTheme
import com.github.rafaabrito.projectgreenmind.ui.viewModel.MainViewModel
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import com.github.rafaabrito.projectgreenmind.ui.components.DrawerContainer

@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val credentialManager by lazy { CredentialManager.create(this) }
    private val viewModel by viewModels<MainViewModel>()

    private val TAG = "AuthActivity"

    // Membros da classe
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState) // Posição mais segura

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
                                rootNavController.navigate(Login) {
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
                            onLoginSuccess = { userId ->
                                rootNavController.navigate(MainAppGraph) {
                                    popUpTo(Login) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                rootNavController.navigate(Register)
                            }
                        )
                    }

                    composable<Register> {
                        RegisterScreen(
                            onNavigateToLogin = {
                                rootNavController.popBackStack()
                            },
                            onRegisterSuccess = { userId ->
                                rootNavController.navigate(MainAppGraph) {
                                    popUpTo(Register) { inclusive = true }
                                }
                            })
                    }

                    composable<MainAppGraph> {
                        val mainAppNavController = rememberNavController()

                        val signOutAction = {
                            signOut()
                            rootNavController.navigate(Login) {
                                popUpTo<MainAppGraph> { inclusive = true }
                            }
                        }

                        MainScreen(
                            mainAppNavController = mainAppNavController
                        ) { paddingValues ->
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
                                    ProfileScreen(onSignOut = signOutAction,
                                        onNavigateToSettings = {
                                            // Exemplo: rootNavController.navigate(SettingsGraph)
                                            // Você precisará definir a rota SettingsGraph ou SettingsScreen
                                        }  )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Lógica de Sign Out
    fun signOut() {
        // Use a instância injetada do Firebase Auth
        firebaseAuth.signOut()

        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                Log.d(TAG, "Credenciais do usuário limpas.")
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Não foi possível limpar as credenciais: ${e.localizedMessage}")
            }
        }
    }
}
@Composable
fun MainScreen(
    mainAppNavController: NavHostController,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    // Estado para controlar visibilidade da BottomBar
    var isDrawerOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(12.dp),
        bottomBar = {
            AnimatedVisibility(
                visible = !isDrawerOpen,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }
                )
            ) {
                BottomBarComponent(navController = mainAppNavController)
            }
        }
    ) { paddingValues ->
        DrawerContainer(
            showTopBar = true,
            title = "App Greenmind",
            outerPadding = paddingValues,
            onDrawerStateChange = { isOpen ->
                isDrawerOpen = isOpen
            }
        ) { combinedPadding ->
            content(combinedPadding)
        }
    }
}
@Serializable
object Login
@Serializable
object Register
@Serializable
object Presentation
@Serializable
object MainAppGraph