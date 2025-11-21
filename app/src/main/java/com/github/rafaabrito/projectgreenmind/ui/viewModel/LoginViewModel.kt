package com.github.rafaabrito.projectgreenmind.ui.viewModel


import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.data.model.toDomainModel
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository
) : ViewModel(){

    // Estado da UI
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    // Inicia o fluxo, retornando o GetCredentialRequest.
    fun getGoogleSignInRequest() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val request = authService.getGoogleIdCredentialRequest()
            if (request != null) {
                _loginState.value = LoginState.AwaitingSocialAuth(request)
            } else {
                _loginState.value = LoginState.Error("Falha ao criar requisição de login do Google.")
            }
        }
    }

    // Recebe a Credencial do Credential Manager
    fun handleGoogleSignInCredential(credential: androidx.credentials.Credential) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    when (val authResponse = authService.signInWithGoogleIdToken(idToken)) {
                        is AuthService.AuthResponse.Success -> {
                            val userEntity = userRepository.associateFirebaseUser(
                                name = authResponse.name,
                                email = authResponse.email,
                                authId = authResponse.authId
                            )
                            val userModel = userEntity.toDomainModel()
                            _loginState.value = LoginState.Success(userModel)
                        }

                        is AuthService.AuthResponse.Error -> {
                            _loginState.value =
                                LoginState.Error("Falha na autenticação social: ${authResponse.message}")
                        }

                        else -> {
                            _loginState.value = LoginState.Error("Autenticação social falhou ou foi cancelada.")
                        }
                    }

                } else {
                    _loginState.value = LoginState.Error("Credencial inválida ou tipo desconhecido.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro local ao finalizar o login: ${e.message}")
            }
        }
    }
    fun signInLocal(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val userEntity = userRepository.login(email, password)

                if (userEntity != null) {
                    val userModel = userEntity.toDomainModel()
                    _loginState.value = LoginState.Success(userModel)
                } else {
                    _loginState.value = LoginState.Error("Credenciais de login inválidas. Verifique seu e-mail e senha.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro ao tentar login: ${e.message}")
            }
        }
    }

    // Estados para a UI
    sealed class LoginState {
        data object Initial : LoginState()
        data object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
        data class AwaitingSocialAuth(val request: GetCredentialRequest) : LoginState()    }

    fun resetState() {
        _loginState.value = LoginState.Initial
    }
}