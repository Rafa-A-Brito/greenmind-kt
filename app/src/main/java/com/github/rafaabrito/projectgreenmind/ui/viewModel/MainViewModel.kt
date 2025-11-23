package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.data.model.UserState
import com.github.rafaabrito.projectgreenmind.data.repository.ScoreRepository
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // ✅ ADICIONAR
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    init {
        viewModelScope.launch {
            checkAuthentication()
            // delay(400)
            _isReady.value = true
        }
    }

    private suspend fun checkAuthentication() {
        val authDetails = authService.getCurrentUserProfileDetails()

        if (authDetails != null) {
            _isAuthenticated.value = true
            loadUserData()
        } else {
            _isAuthenticated.value = false
            _userState.value = UserState(isLoading = false)
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            loadUserData()
        }
    }

    private suspend fun loadUserData() {
        _userState.value = _userState.value.copy(isLoading = true)

        val authDetails = authService.getCurrentUserProfileDetails()

        if (authDetails != null) {
            val photoUrl = authDetails.profilePictureUrl
            val firstName = authDetails.name?.split(" ")?.firstOrNull() ?: "Usuário"

            val userFromFirebase = User(
                userId = 0,
                name = firstName,
                email = authDetails.email,
                firebaseUid = authDetails.authId
            )

            _isAuthenticated.value = true

            try {
                val user = userRepository.getUserByAuthId(authDetails.authId)

                if (user != null) {
                    val totalScore = scoreRepository.getTotalScoreByUserId(user.userId)
                    val scoreLevel = (totalScore / 300) + 1

                    val scoreEntity = scoreRepository.getScoreByUserId(user.userId).first()

                    _userState.value = UserState(
                        user = userFromFirebase.copy(userId = user.userId), // ✅ Preservar userId
                        photoUrl = photoUrl,
                        userXP = totalScore,
                        userLevel = scoreLevel,
                        isLoading = false
                    )
                } else {
                    _userState.value = UserState(
                        user = userFromFirebase,
                        photoUrl = photoUrl,
                        userXP = 0,
                        userLevel = 0,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userState.value = UserState(
                    user = userFromFirebase,
                    photoUrl = photoUrl,
                    userXP = 0,
                    userLevel = 0,
                    isLoading = false,
                    error = e.message
                )
            }
        } else {
            _isAuthenticated.value = false
            _userState.value = UserState(isLoading = false)
        }
    }

    fun clearUserData() {
        _userState.value = UserState(isLoading = false)
        _isAuthenticated.value = false
    }
}