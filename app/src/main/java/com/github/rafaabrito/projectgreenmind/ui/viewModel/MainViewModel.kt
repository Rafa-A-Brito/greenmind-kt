package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.data.model.UserState
import com.github.rafaabrito.projectgreenmind.data.repository.ScoreRepository
import com.github.rafaabrito.projectgreenmind.data.repository.StreakCheckInResult
import com.github.rafaabrito.projectgreenmind.data.repository.StreakRepository
import com.github.rafaabrito.projectgreenmind.data.repository.TasksRepository
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ Data classes para estados
data class StreakState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isNewRecord: Boolean = false
)

data class TasksProgressState(
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val completedPercentage: Float = 0f
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val scoreRepository: ScoreRepository,
    private val streakRepository: StreakRepository,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _streakState = MutableStateFlow(StreakState())
    val streakState = _streakState.asStateFlow()

    private val _tasksProgress = MutableStateFlow(TasksProgressState())
    val tasksProgress = _tasksProgress.asStateFlow()

    private val _showStreakDialog = MutableStateFlow(false)
    val showStreakDialog = _showStreakDialog.asStateFlow()

    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog = _showLevelUpDialog.asStateFlow()

    private val _newLevel = MutableStateFlow(0)
    val newLevel = _newLevel.asStateFlow()

    init {
        viewModelScope.launch {
            checkAuthentication()
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
    fun performDailyCheckIn() {
        viewModelScope.launch {
            val userId = _userState.value.user?.userId

            if (userId != null && userId > 0) {
                try {
                    val result = streakRepository.checkInToday(userId)

                    when (result) {
                        is StreakCheckInResult.NewStreak -> {
                            _streakState.value = StreakState(
                                currentStreak = result.days,
                                longestStreak = result.days,
                                isNewRecord = false
                            )
                            _showStreakDialog.value = true
                        }
                        is StreakCheckInResult.StreakContinued -> {
                            _streakState.value = _streakState.value.copy(
                                currentStreak = result.currentStreak,
                                isNewRecord = result.isNewRecord
                            )
                            _showStreakDialog.value = true
                        }
                        is StreakCheckInResult.StreakBroken -> {
                            _streakState.value = _streakState.value.copy(
                                currentStreak = 1
                            )
                            _showStreakDialog.value = true
                        }
                        is StreakCheckInResult.AlreadyCheckedIn -> {
                            // Não mostra o dialog se já fez check-in hoje
                            loadStreak(userId)
                        }
                    }

                    // Recarrega os dados do usuário para atualizar a UI
                    loadUserData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            loadUserData()
        }
    }

     suspend fun loadUserData() {
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
                    loadStreak(user.userId)
                    loadTasksProgress(user.userId)
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

    private suspend fun loadStreak(userId: Int) {
        try {
            streakRepository.getStreakByUserId(userId).first()?.let { streak ->
                _streakState.value = StreakState(
                    currentStreak = streak.currentStreak,
                    longestStreak = streak.longestStreak,
                    isNewRecord = streak.currentStreak == streak.longestStreak && streak.currentStreak > 1
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkLevelUp(previousXP: Int, newXP: Int) {
        val previousLevel = (previousXP / 1000) + 1
        val newLevelCalc = (newXP / 1000) + 1

        if (newLevelCalc > previousLevel) {
            _newLevel.value = newLevelCalc
            _showLevelUpDialog.value = true
        }
    }

    private suspend fun loadTasksProgress(userId: Int) {
        try {
            tasksRepository.getAllTasks().first().let { allTasks ->
                val completedCount = allTasks.count { task ->
                    val progress = tasksRepository.getOrCreateProgress(userId, task.taskId)
                    progress.missionStatus.equals("Completa", ignoreCase = true)
                }

                val totalCount = allTasks.size
                val percentage = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

                _tasksProgress.value = TasksProgressState(
                    completedTasks = completedCount,
                    totalTasks = totalCount,
                    completedPercentage = percentage
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissStreakDialog() {
        _showStreakDialog.value = false
    }

    fun dismissLevelUpDialog() {
        _showLevelUpDialog.value = false
    }

    fun clearUserData() {
        _userState.value = UserState(isLoading = false)
        _isAuthenticated.value = false
        _streakState.value = StreakState()
        _tasksProgress.value = TasksProgressState()
    }
}