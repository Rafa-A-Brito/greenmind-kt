package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity
import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSettingsState(
    val userId: Int = 0,
    val name: String = "",
    val email: String = "",
    val firebaseUid: String = "",
    val hasPassword: Boolean = false,
    val isFirebaseAuth: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) : ViewModel() {

    private val _userSettingsState = MutableStateFlow(UserSettingsState())
    val userSettingsState: StateFlow<UserSettingsState> = _userSettingsState.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _userSettingsState.value = _userSettingsState.value.copy(isLoading = true)

            try {
                val authDetails = authService.getCurrentUserProfileDetails()

                if (authDetails != null) {
                    val user = userRepository.getUserByAuthId(authDetails.authId)

                    if (user != null) {
                        _userSettingsState.value = UserSettingsState(
                            userId = user.userId,
                            name = user.name ?: authDetails.name ?: "Usuário",
                            email = user.email,
                            firebaseUid = user.firebaseUid ?: "",
                            hasPassword = user.hashPassword != null,
                            isFirebaseAuth = true,
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _userSettingsState.value = UserSettingsState(
                            name = authDetails.name ?: "Usuário",
                            email = authDetails.email,
                            firebaseUid = authDetails.authId,
                            hasPassword = false,
                            isFirebaseAuth = true,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _userSettingsState.value = UserSettingsState(
                        name = "Visitante",
                        email = "",
                        hasPassword = false,
                        isFirebaseAuth = false,
                        isLoading = false,
                        error = "Usuário não autenticado"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userSettingsState.value = UserSettingsState(
                    name = "Erro",
                    email = "",
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateUserData(
        newName: String,
        newEmail: String,
        newPassword: String? = null,
        currentPassword: String? = null
    ) {
        viewModelScope.launch {
            _userSettingsState.value = _userSettingsState.value.copy(
                isSaving = true,
                saveSuccess = false,
                error = null
            )

            try {
                val currentState = _userSettingsState.value

                if (currentState.userId <= 0) {
                    _userSettingsState.value = currentState.copy(
                        isSaving = false,
                        error = "Não foi possível atualizar: Usuário não encontrado"
                    )
                    return@launch
                }

                val userEntityFlow = userRepository.getUserById(currentState.userId)
                val existingUser = userEntityFlow.first()

                if (existingUser == null) {
                    _userSettingsState.value = currentState.copy(
                        isSaving = false,
                        error = "Usuário não encontrado no banco de dados"
                    )
                    return@launch
                }

                var newHashedPassword: String? = existingUser.hashPassword // Mantém a senha atual por padrão

                // Se está tentando mudar senha
                if (!newPassword.isNullOrEmpty()) {
                    // Se já tem senha, precisa validar a senha atual
                    if (existingUser.hashPassword != null) {
                        if (currentPassword.isNullOrEmpty()) {
                            _userSettingsState.value = currentState.copy(
                                isSaving = false,
                                error = "Senha atual é obrigatória para alterar a senha"
                            )
                            return@launch
                        }

                        // Verificar senha atual
                        val isPasswordCorrect = passwordHasher.verifyPassword(
                            currentPassword,
                            existingUser.hashPassword
                        )

                        if (!isPasswordCorrect) {
                            _userSettingsState.value = currentState.copy(
                                isSaving = false,
                                error = "Senha atual incorreta"
                            )
                            return@launch
                        }
                    }

                    // Hash da nova senha
                    newHashedPassword = passwordHasher.hashPassword(newPassword)
                }

                val updatedUser = UserEntity(
                    userId = currentState.userId,
                    name = newName.trim(),
                    email = newEmail.trim(),
                    hashPassword = newHashedPassword,
                    firebaseUid = currentState.firebaseUid.ifEmpty { null }
                )

                userRepository.saveUser(updatedUser)

                _userSettingsState.value = currentState.copy(
                    name = newName.trim(),
                    email = newEmail.trim(),
                    hasPassword = newHashedPassword != null,
                    isSaving = false,
                    saveSuccess = true
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _userSettingsState.value = _userSettingsState.value.copy(
                    isSaving = false,
                    error = "Erro ao salvar: ${e.message}"
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _userSettingsState.value = _userSettingsState.value.copy(saveSuccess = false)
    }

    fun refreshData() {
        loadUserData()
    }
}