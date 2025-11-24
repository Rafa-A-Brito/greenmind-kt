package com.github.rafaabrito.projectgreenmind

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.github.rafaabrito.projectgreenmind.domain.utils.permissions.rememberAllPermissionsState
import com.github.rafaabrito.projectgreenmind.domain.utils.network.rememberNetworkState
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
    private val TAG = "MainActivity"

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.apply {
            setKeepOnScreenCondition {
                !mainViewModel.isReady.value
            }
        }

        setContent {
            ProjectGreenMindTheme {
                val networkState = rememberNetworkState()
                var wasDisconnected by remember { mutableStateOf(false) }
                var permissionsRequested by remember { mutableStateOf(false) }
                val isReady by mainViewModel.isReady.collectAsStateWithLifecycle()

                val permissionsManager = rememberAllPermissionsState(
                    onAllPermissionsGranted = {
                        Log.d(TAG, "✅ Todas as permissões concedidas")
                    },
                    onPermissionsDenied = {
                        Log.w(TAG, "⚠️ Algumas permissões foram negadas")
                    }
                )

                LaunchedEffect(isReady) {
                    if (isReady && !permissionsRequested) {
                        if (!permissionsManager.permissionsState.allPermissionsGranted) {
                            permissionsManager.requestAllPermissions()
                            permissionsRequested = true
                            Log.d(TAG, "🔒 Solicitando permissões...")
                        } else {
                            Log.d(TAG, "✅ Permissões já concedidas")
                        }
                    }
                }

                LaunchedEffect(networkState.isConnected) {
                    if (!networkState.isConnected) {
                        wasDisconnected = true
                        Log.w(TAG, "📡 Conexão perdida!")
                    } else if (wasDisconnected) {
                        Log.d(TAG, "📡 Conexão restaurada!")
                    }
                }

                if (!networkState.isConnected) {
                    NoConnectionScreen(
                        onRetry = {
                            if (networkState.checkConnection()) {
                                wasDisconnected = false
                            }
                        }
                    )
                } else {
                    MainApp(
                        mainViewModel = mainViewModel,
                        onSignOut = { signOut() }
                    )
                }
            }
        }
    }

    @Composable
    private fun MainApp(
        mainViewModel: MainViewModel,
        onSignOut: () -> Unit
    ) {
        val rootNavController = rememberNavController()
        val context = LocalContext.current
        val isAuthenticated by mainViewModel.isAuthenticated.collectAsStateWithLifecycle()
        val isReady by mainViewModel.isReady.collectAsStateWithLifecycle()

        LaunchedEffect(isReady, isAuthenticated) {
            if (isReady && isAuthenticated) {
                rootNavController.navigate(MainAppGraph) {
                    popUpTo(Presentation) { inclusive = true }
                }
            }
        }

        NavHost(
            navController = rootNavController,
            startDestination = Presentation
        ) {
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
                        mainViewModel.refreshUserData()
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
                        mainViewModel.refreshUserData()
                        rootNavController.navigate(MainAppGraph) {
                            popUpTo(Register) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainAppGraph> {
                val mainAppNavController = rememberNavController()
                val userState by mainViewModel.userState.collectAsStateWithLifecycle()

                val navigateToSettingsAction = {
                    mainAppNavController.navigate(SettingsRoute)
                }

                val navigateToAboutAction = {
                    mainAppNavController.navigate(AboutRoute)
                }

                val signOutAction = {
                    onSignOut()
                    mainViewModel.clearUserData()
                    rootNavController.navigate(Login) {
                        popUpTo<MainAppGraph> { inclusive = true }
                    }
                }

                MainScreen(
                    mainAppNavController = mainAppNavController,
                    userName = userState.userName,
                    userPhotoUrl = userState.photoUrl,
                    isLoadingUserData = userState.isLoading,
                    onSignOut = signOutAction,
                    onNavigateToSettings = navigateToSettingsAction,
                    onNavigateToAbout = navigateToAboutAction
                ) { paddingValues ->
                    NavHost(
                        navController = mainAppNavController,
                        startDestination = NavigationBarItems.House.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(NavigationBarItems.House.route) {
                            HomeScreen(mainViewModel)
                        }
                        composable(NavigationBarItems.Local.route) {
                            EcoScreen()
                        }

                        composable(NavigationBarItems.Trophy.route) { // "eco_tasks"
                            EcoTasksScreen(
                                onNavigateToRanking = {
                                    mainAppNavController.navigate("ranking") {
                                        launchSingleTop = true
                                    }
                                },
                                onBackClick = {
                                    mainAppNavController.popBackStack()
                                }
                            )
                        }

                        composable(NavigationBarItems.Trophy.route) { // "eco_tasks"
                            EcoTasksScreen(
                                onNavigateToRanking = {
                                    mainAppNavController.navigate("ranking") {
                                        launchSingleTop = true
                                    }
                                },
                                onBackClick = {
                                    mainAppNavController.popBackStack()
                                }
                            )
                        }

                        // ✅ Rota Ranking (acessível de EcoTasks)
                        composable("ranking") {
                            RankingScreen(
                                onNavigateToTasks = {
                                    mainAppNavController.navigate("eco_tasks") {
                                        popUpTo("eco_tasks") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(NavigationBarItems.Community.route) {
                            CommunityScreen()
                        }
                        composable(NavigationBarItems.Person.route) {
                            ProfileScreen(
                                onSignOut = signOutAction,
                                onNavigateToSettings = navigateToSettingsAction
                            )
                        }
                        composable<SettingsRoute> {
                            SettingsScreen(
                                onBackClick = {
                                    mainAppNavController.popBackStack()
                                }
                            )
                        }
                        composable<AboutRoute> {
                            AboutProjectScreen(
                                onBackClick = {
                                    mainAppNavController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun signOut() {
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
    userName: String? = null,
    userPhotoUrl: String? = null,
    isLoadingUserData: Boolean = false,
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    var isDrawerOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .padding(12.dp)
            .background(Color.White),
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
            },
            userName = userName,
            userPhotoUrl = userPhotoUrl,
            isLoadingUserData = isLoadingUserData,
            onSignOut = onSignOut,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAbout = onNavigateToAbout
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
object RankingRoute

@Serializable
object EcoTasksRoute

@Serializable
object SettingsRoute

@Serializable
object Presentation

@Serializable
object AboutRoute

@Serializable
object MainAppGraph