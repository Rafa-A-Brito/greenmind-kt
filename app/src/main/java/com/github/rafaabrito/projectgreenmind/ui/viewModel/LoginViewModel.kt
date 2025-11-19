package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.content.Intent
import android.content.IntentSender
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.data.model.toDomainModel
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
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

    // Inicia o fluxo e expõe o IntentSender
    fun getGoogleSignInIntentSender() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val intentSender = authService.startSocialSignIn(AuthService.SocialProvider.GOOGLE)
            if (intentSender != null) {
                _loginState.value = LoginState.AwaitingSocialAuth(intentSender)
            } else {
                _loginState.value = LoginState.Error("Falha ao iniciar Google Sign-In.")
            }
        }
    }

    // Recebe o resultado da UI e finaliza a autenticação
    fun handleGoogleSignInResult(intent: Intent?) {
        if (intent == null) {
            _loginState.value = LoginState.Error("Autenticação social cancelada.")
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            when (val authResult = authService.completeSocialSignIn(intent)) {
                is AuthService.AuthResponse.Success -> {
                    // Reaproveita sua lógica de onSocialAuthResponse
                    onSocialAuthResponse(authResult, null)
                }
                is AuthService.AuthResponse.Error -> {
                    _loginState.value = LoginState.Error(authResult.message)
                }
                else -> {}
            }
        }
    }
    fun signInLocal(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val entity = userRepository.login(email, password)
                if (entity != null) {
                    _loginState.value = LoginState.Success(entity.toDomainModel())
                } else {
                    _loginState.value = LoginState.Error("Credenciais inválidas.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro ao conectar. ${e.message}")
            }
        }
    }

    fun onSocialAuthResponse(result: AuthService.AuthResponse.Success?, error: String?) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            if (result != null) {
                when (result) {
                    else -> {
                        try {
                            val userEntity = userRepository.associateFirebaseUser(
                                name = result.name,
                                email = result.email,
                                authId = result.authId
                            )

                            val userModel = userEntity.toDomainModel()
                            _loginState.value = LoginState.Success(userModel)

                        } catch (e: Exception) {
                            _loginState.value =
                                LoginState.Error("Erro local ao finalizar o login: ${e.message}")
                        }
                    }
                }
            } else if (error != null) {
                _loginState.value = LoginState.Error("Falha na autenticação social: $error")
            } else {
                _loginState.value = LoginState.Error("Autenticação social cancelada ou falha desconhecida.")
            }
        }
    }

    // Estados para a UI
    sealed class LoginState {
        data object Initial : LoginState()
        data object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
        // 🟢 NOVO ESTADO
        data class AwaitingSocialAuth(val intentSender: IntentSender) : LoginState()
    }
}