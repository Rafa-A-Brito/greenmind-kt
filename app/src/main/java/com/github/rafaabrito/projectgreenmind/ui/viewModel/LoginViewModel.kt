package com.github.rafaabrito.projectgreenmind.ui.viewModel

import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.domain.model.User
import com.github.rafaabrito.projectgreenmind.domain.model.toDomainModel
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

    fun handleSocialAuthResult(result: AuthService.AuthResult?, error: String?) {
        if (result != null) {
            viewModelScope.launch {
                _loginState.value = LoginViewModel.LoginState.Loading

                try {
                    val userEntity = userRepository.associateFirebaseUser(
                        name = result.name,
                        email = result.email,
                        authId = result.authId
                    )

                    val userModel = User(
                        userId = userEntity.userId,
                        name = userEntity.name ?: "User",
                        email = userEntity.email,
                        firebaseUid = userEntity.firebaseUid,
                    )

                    _loginState.value = LoginViewModel.LoginState.Success(user = userModel)

                } catch (e: Exception) {
                    _loginState.value =
                        LoginViewModel.LoginState.Error("Erro ao finalizar login local: ${e.message}")
                }
            }
        }
    }
    private fun handleAuthSuccess(result: AuthService.AuthResult) {
        viewModelScope.launch {
            try {
                val entity = userRepository.associateFirebaseUser(
                    result.name, result.email, result.authId
                )
                _loginState.value = LoginState.Success(entity.toDomainModel())
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Falha ao salvar dados locais.")
            }
        }
    }

    fun onSocialAuthSuccess(authResult: AuthService.AuthResult) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading // Opcional, mas bom para sincronia

            val userEntity = userRepository.associateFirebaseUser(
                name = authResult.name,
                email = authResult.email,
                authId = authResult.authId
            )
            _loginState.value = LoginState.Success(user = User(
                userId = userEntity.userId,
                name = userEntity.name ?: authResult.name ?: "Usuário", // Mapeia corretamente
                email = userEntity.email,
                firebaseUid = userEntity.firebaseUid
            ))
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
                _loginState.value = LoginState.Error("Erro ao conectar.")
            }
        }
    }

    fun onSocialAuthResult(result: AuthService.AuthResult?, error: String?) {
        if (result != null) {
            handleAuthSuccess(result)
        } else if (error != null) {
            _loginState.value = LoginState.Error("Falha na autenticação social: $error")
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