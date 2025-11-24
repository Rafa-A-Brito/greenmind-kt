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

    // Inicia o fluxo, retornando o GetCredentialRequest.
    fun getGoogleSignInRequest() {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            val request = authService.getGoogleIdCredentialRequest()
            if (request != null) {
                _registerState.value = RegisterState.AwaitingSocialAuth(request)
            } else {
                _registerState.value = RegisterState.Error("Falha ao criar requisição de registro do Google.")
            }
        }
    }

    // Recebe a Credencial do Credential Manager
    fun handleGoogleSignInCredential(credential: androidx.credentials.Credential) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    val authResponse = authService.signInWithGoogleIdToken(idToken)

                    when (authResponse) {
                        is AuthService.AuthResponse.Success -> {
                            // O associateFirebaseUser cuida do registro/associação local
                            val userEntity = userRepository.associateFirebaseUser(
                                name = authResponse.name,
                                email = authResponse.email,
                                authId = authResponse.authId
                            )
                            val userModel = userEntity.toDomainModel()
                            _registerState.value = RegisterState.Success(userModel)
                        }

                        is AuthService.AuthResponse.Error -> {
                            _registerState.value =
                                RegisterState.Error("Falha na autenticação social: ${authResponse.message}")
                        }

                        else -> {
                            _registerState.value = RegisterState.Error("Autenticação social falhou ou foi cancelada.")
                        }
                    }

                } else {
                    _registerState.value = RegisterState.Error("Credencial inválida ou tipo desconhecido.")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro local ao finalizar o registro: ${e.message}")
            }
        }
    }
    fun signUpLocal(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val userEntity = userRepository.createNewUser(name, email, password)

                if (userEntity != null) {
                    val userModel = userEntity.toDomainModel()
                    _registerState.value = RegisterState.Success(userModel)
                } else {
                    _registerState.value = RegisterState.Error("O email já está em uso.")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao registrar: ${e.message}")
            }
        }
    }

    sealed class RegisterState {
        data object Initial : RegisterState()
        data object Loading : RegisterState()
        data class Success(val user: User) : RegisterState()
        data class Error(val message: String) : RegisterState()
        // 🟢 NOVO ESTADO
        data class AwaitingSocialAuth(val request: GetCredentialRequest) : RegisterState()
    }

    fun resetState() {
        // Retorna ao estado inicial
        _registerState.value = RegisterState.Initial
    }
}