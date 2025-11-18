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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
import com.github.rafaabrito.projectgreenmind.domain.utils.AuthService
@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private val TAG = "AuthActivity"

    // Membros da classe
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var authService: AuthService

    private lateinit var callbackManager: CallbackManager
    private lateinit var credentialManager: CredentialManager

    var onSocialLoginResult: ((AuthService.AuthResult?, Exception?) -> Unit)? = null
    var onSocialRegisterResult: ((AuthService.AuthResult?, Exception?) -> Unit)? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        val splashScreen = installSplashScreen()

        // Inicializa o Firebase Auth e Credential Manager
        credentialManager = CredentialManager.create(this)
        callbackManager = CallbackManager.Factory.create()

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
                            },
                            onGoogleSignIn = ::signInWithGoogle,
                            onFacebookSignIn = ::signInWithFacebook
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
                            },
                            onGoogleSignUp = ::signInWithGoogle,
                            onFacebookSignUp = ::signInWithFacebook
                        )
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
                        ) { paddingValues -> // <- Este é o 'content' slot
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSocialCredential(credential: Credential) {
        val callback = onSocialRegisterResult ?: onSocialLoginResult

        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Lógica de autenticação transferida para o AuthService
            lifecycleScope.launch {
                try {
                    val authResult = authService.authenticateWithGoogleToken(googleIdTokenCredential.idToken)
                    // ... (chama authService.authenticateWithGoogleToken)
                    if (authResult != null) {
                        Log.d(TAG, "Google Auth Success via AuthService")
                        callback?.invoke(authResult, null)

                    } else {
                        Log.w(TAG, "Google Auth Failed via AuthService (Token Válido, Firebase Falhou)")
                        callback?.invoke(null, Exception("Falha na autenticação Firebase."))
                        Toast.makeText(this@MainActivity, "Autenticação falhou.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "AuthService error: ${e.localizedMessage}")
                    callback?.invoke(null, e)
                    Toast.makeText(this@MainActivity, "Erro de servidor.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.w(TAG, "A credencial não é do tipo Google ID!")
            callback?.invoke(null, Exception("Credencial inválida."))
        }
    }

    // Método para iniciar o login, chamado pelo Composable
    fun signInWithFacebook() {
        val loginManager = LoginManager.getInstance()

        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:${result.accessToken}")
                    // 🔑 Novo método para lidar com o token do Facebook
                    handleFacebookAuthResult(result.accessToken)
                }
                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }
                override fun onError(error: FacebookException) {
                    Log.w(TAG, "facebook:onError", error)
                }
            }
        )

        loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    private fun handleFacebookAuthResult(token: AccessToken) {
        val callback = onSocialRegisterResult ?: onSocialLoginResult

        lifecycleScope.launch {
            try {
                val authResult = authService.authenticateWithFacebookToken(token.token)
                if (authResult != null) {
                    Log.d(TAG, "Facebook Auth Success via AuthService")
                    callback?.invoke(authResult, null)
                } else {
                    Log.w(TAG, "Facebook Auth Failed via AuthService")
                    callback?.invoke(null, Exception("Falha na autenticação Firebase (Facebook)."))
                }
            } catch (e: Exception) {
                Log.e(TAG, "AuthService error (Facebook): ${e.localizedMessage}")
                callback?.invoke(null, e)
                Toast.makeText(this@MainActivity, "Erro de servidor.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função pública para ser chamada pelos Composable/ViewModel
    fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            // Uso correto: getString() é um método da Activity/Context
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity
                )
                handleSocialCredential(result.credential)
            } catch (e: Exception) {
                // Adicionado Toast para debug. Remova quando estiver estável.
                Toast.makeText(this@MainActivity, "Erro Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Google Sign-In falhou: ${e.localizedMessage}")
            }
        }
    }

    // Lógica de Sign Out
    fun signOut() {
        auth.signOut()

        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                Log.d(TAG, "User credentials cleared.")
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
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
    ) { paddingValues ->
        content(paddingValues)
    }
}

// Objects Serializable...
@Serializable
object Login
@Serializable
object Register
@Serializable
object Presentation
@Serializable
object MainAppGraph