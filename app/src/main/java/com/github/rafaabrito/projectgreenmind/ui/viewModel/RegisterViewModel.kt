package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.content.Intent
import android.content.IntentSender
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authService: AuthService
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    fun getGoogleSignInIntentSender() {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            val intentSender = authService.startSocialSignIn(AuthService.SocialProvider.GOOGLE)
            if (intentSender != null) {
                _registerState.value = RegisterState.AwaitingSocialAuth(intentSender)
            } else {
                _registerState.value = RegisterState.Error("Falha ao iniciar Google Sign-In.")
            }
        }
    }

    fun handleGoogleSignInResult(intent: Intent?) {
        if (intent == null) {
            _registerState.value = RegisterState.Error("Autenticação social cancelada.")
            return
        }

        _registerState.value = RegisterState.Loading // Re-inicia o estado de loading
        viewModelScope.launch {
            when (val authResult = authService.completeSocialSignIn(intent)) {
                is AuthService.AuthResponse.Success -> {
                    // Reutiliza sua lógica existente de `onSocialAuthResponse`
                    onSocialAuthResponse(authResult, null)
                }
                is AuthService.AuthResponse.Error -> {
                    _registerState.value = RegisterState.Error(authResult.message)
                }
                else -> {}
            }
        }
    }

    fun signUpLocal(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val isSuccess = userRepository.createNewUser(name, email, password)
                if (isSuccess) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("O email já está em uso.")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao registrar: ${e.message}")
            }
        }
    }

    fun signUpFirebase(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                // A chamada retorna AuthResponse
                when (
                    val authResult = authService.signUpWithEmailPassword(email, password, name)
                ) {
                    is AuthService.AuthResponse.Success -> {
                        userRepository.associateFirebaseUser(
                            name = name,
                            email = authResult.email,
                            authId = authResult.authId
                        )
                        _registerState.value = RegisterState.Success
                    }
                    is AuthService.AuthResponse.Error -> {
                        // O data class Error contém a mensagem
                        _registerState.value = RegisterState.Error(authResult.message)
                    }
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao registrar: ${e.message}")
            }
        }
    }

    fun onSocialAuthResponse(result: AuthService.AuthResponse.Success?, error: String?) {
        if (result != null) {
            viewModelScope.launch {
                try {
                    // O associateFirebaseUser cuida do registro/associação local
                    userRepository.associateFirebaseUser(
                        name = result.name,
                        email = result.email,
                        authId = result.authId
                    )
                    _registerState.value = RegisterState.Success
                } catch (e: Exception) {
                    _registerState.value = RegisterState.Error("Erro ao finalizar registro local.")
                }
            }
        } else if (error != null) {
            _registerState.value = RegisterState.Error("Falha na autenticação social: $error")
        }
    }

    sealed class RegisterState {
        data object Initial : RegisterState()
        data object Loading : RegisterState()
        data object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
        // 🟢 NOVO ESTADO: Para notificar a UI que um IntentSender deve ser lançado
        data class AwaitingSocialAuth(val intentSender: IntentSender) : RegisterState()
    }
}