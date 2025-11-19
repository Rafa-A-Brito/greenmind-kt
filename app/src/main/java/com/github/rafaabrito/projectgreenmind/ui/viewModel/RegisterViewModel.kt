package com.github.rafaabrito.projectgreenmind.ui.viewModel

import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.domain.utils.AuthService
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

    fun signUpLocal(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                // Tenta criar o usuário no Room (usando BCrypt no Repositório)
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
                    val authResult = authService.signUpWithEmailPassword(email, password)
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

    fun onSocialAuthResult(result: AuthService.AuthResponse?, error: String?) {
        if (result is AuthService.AuthResponse.Success) {
            viewModelScope.launch {
                try {
                    // Smart Cast do Kotlin que permite o acesso aos campos
                    userRepository.associateFirebaseUser(
                        name = result.name,
                        email = result.email,
                        authId = result.authId
                    )
                    _registerState.value = RegisterState.Success
                } catch (e: Exception) {
                    _registerState.value = RegisterState.Error("Erro ao finalizar registro local: ${e.message}")
                }
            }
        } else if (result is AuthService.AuthResponse.Error) {
            _registerState.value = RegisterState.Error("Falha na autenticação social: ${result.message}")
        } else if (error != null) {
            _registerState.value = RegisterState.Error("Falha na autenticação social: $error")
        } else {
            // Cancelamento caso haja erro ou o mesmo seja null
            _registerState.value = RegisterState.Error("Autenticação social cancelada ou falha desconhecida.")
        }
    }

    sealed class RegisterState {
        data object Initial : RegisterState()
        data object Loading : RegisterState()
        data object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}