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

    fun onSocialAuthResult(result: AuthService.AuthResult?, error: String?) {
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

    // Estados para a UI
    sealed class RegisterState {
        data object Initial : RegisterState()
        data object Loading : RegisterState()
        data object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}