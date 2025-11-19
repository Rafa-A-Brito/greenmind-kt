package com.github.rafaabrito.projectgreenmind

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

// Imports para Credential Manager e Firebase
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import com.github.rafaabrito.projectgreenmind.ui.components.DrawerContainer
import com.google.firebase.Firebase

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
                                    popUpTo<Login> { inclusive = true }
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
                                rootNavController.navigate(Login) {
                                    popUpTo<Register> { inclusive = true }
                                }
                            },
                            onRegisterSuccess = {
                                rootNavController.navigate(Login) {
                                    popUpTo<Register> { inclusive = true }
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
                                    ProfileScreen(onSignOut = signOutAction)                                }
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
    content: @Composable (paddingValues: PaddingValues) -> Unit)
{
    Scaffold(
        bottomBar = {
            BottomBarComponent(navController = mainAppNavController)
        },
    ) { paddingValues -> // paddingValues é o da BottomBar
        DrawerContainer(
            showTopBar = true,
            title = "App Greenmind",
            outerPadding = paddingValues
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