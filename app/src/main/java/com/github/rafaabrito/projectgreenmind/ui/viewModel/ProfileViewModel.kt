package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.repository.ScoreRepository
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl.asStateFlow()

    private val _userLevel = MutableStateFlow(0)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    private val _userRank = MutableStateFlow("Iniciante")
    val userRank: StateFlow<String> = _userRank.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✅ Estados dos toggles
    private val _showTipsPopup = MutableStateFlow(true)
    val showTipsPopup: StateFlow<Boolean> = _showTipsPopup.asStateFlow()

    private val _enableWeeklyNotifications = MutableStateFlow(false)
    val enableWeeklyNotifications: StateFlow<Boolean> = _enableWeeklyNotifications.asStateFlow()

    private val _allowLocation = MutableStateFlow(false)
    val allowLocation: StateFlow<Boolean> = _allowLocation.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val authDetails = authService.getCurrentUserProfileDetails()

                if (authDetails != null) {
                    _userName.value = authDetails.name ?: "Usuário"
                    _userPhotoUrl.value = authDetails.profilePictureUrl

                    val user = userRepository.getUserByAuthId(authDetails.authId)

                    if (user != null) {
                        val scoreEntity = scoreRepository.getScoreByUserId(user.userId).first()

                        if (scoreEntity != null) {
                            _totalScore.value = scoreEntity.totalScore
                            _userLevel.value = scoreEntity.scoreLevel
                            _userRank.value = calculateRank(scoreEntity.scoreLevel)
                        } else {
                            _totalScore.value = 0
                            _userLevel.value = 0
                            _userRank.value = "Iniciante"
                        }
                    } else {
                        _totalScore.value = 0
                        _userLevel.value = 0
                        _userRank.value = "Iniciante"
                    }
                } else {
                    _userName.value = "Visitante"
                    _userPhotoUrl.value = null
                    _totalScore.value = 0
                    _userLevel.value = 0
                    _userRank.value = "Iniciante"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userName.value = "Erro ao carregar"
                _userPhotoUrl.value = null
                _totalScore.value = 0
                _userLevel.value = 0
                _userRank.value = "Iniciante"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateRank(level: Int): String {
        return when {
            level == 0 -> "Iniciante"
            level in 1..4 -> "Explorador Verde"
            level in 5..9 -> "Guardião da Natureza"
            level in 10..19 -> "Herói da Sustentabilidade"
            else -> "Lenda Ecológica"
        }
    }

    // ✅ Funções para atualizar toggles
    fun updateShowTipsPopup(enabled: Boolean) {
        _showTipsPopup.value = enabled
        // TODO: Salvar preferência no DataStore ou SharedPreferences
    }

    fun updateEnableNotifications(enabled: Boolean) {
        _enableWeeklyNotifications.value = enabled
        // TODO: Salvar preferência e configurar WorkManager para notificações
    }

    fun updateAllowLocation(enabled: Boolean) {
        _allowLocation.value = enabled
        // TODO: Salvar preferência no DataStore
    }

    fun refreshData() {
        loadUserData()
    }
}