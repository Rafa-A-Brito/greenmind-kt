package com.github.rafaabrito.projectgreenmind.ui.viewModel

import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.data.model.toDomainModel
import com.github.rafaabrito.projectgreenmind.domain.utils.AuthService
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

    // Função auxiliar para tratar o sucesso de autenticação
    private fun handleAuthSuccess(result: AuthService.AuthResponse.Success) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // Aqui o Smart Cast não é necessário, pois o tipo já é AuthResponse.Success
                val userEntity = userRepository.associateFirebaseUser(
                    name = result.name,
                    email = result.email,
                    authId = result.authId
                )

                _loginState.value = LoginState.Success(userEntity.toDomainModel())
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro ao sincronizar dados locais: ${e.message}")
            }
        }
    }

    fun signInFirebase(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val authResult = authService.signInWithEmailPassword(email, password)

                // Smart Cast/Pattern Matching
                when (authResult) {
                    is AuthService.AuthResponse.Success -> {
                        handleAuthSuccess(authResult)
                    }
                    is AuthService.AuthResponse.Error -> {
                        _loginState.value = LoginState.Error(authResult.message)
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro ao conectar ao Firebase: ${e.message}")
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

    fun onSocialAuthResult(result: AuthService.AuthResponse?, error: String?) {
        if (result is AuthService.AuthResponse.Success) {
            handleAuthSuccess(result)
        } else if (result is AuthService.AuthResponse.Error) {
            _loginState.value = LoginState.Error(result.message)
        } else if (error != null) {
            _loginState.value = LoginState.Error("Falha na autenticação social: $error")
        } else {
            _loginState.value =
                LoginState.Error("Autenticação social cancelada ou falha desconhecida.")
        }
    }
    fun signInSocial(provider: AuthService.SocialProvider) {
        _loginState.value = LoginState.Loading
        authService.startSocialSignIn(provider)
    }

    // Estados para a UI
    sealed class LoginState {
        data object Initial : LoginState()
        data object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}