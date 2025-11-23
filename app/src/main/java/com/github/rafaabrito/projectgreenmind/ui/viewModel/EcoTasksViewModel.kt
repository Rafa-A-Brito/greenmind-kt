package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.data.repository.ScoreRepository
import com.github.rafaabrito.projectgreenmind.data.repository.TasksRepository
import com.github.rafaabrito.projectgreenmind.data.repository.UserRepository
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksProgressEntity
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EcoTaskUi(
    val taskId: Int,
    val title: String,
    val xp: String,
    val scoreValue: Int,
    val duration: String,
    val iconResId: Int,
    val progress: Float,
    val isCompleted: Boolean,
    val category: String, // ✅ Adicionado
    val level: String // ✅ Adicionado
)

data class UserLevelState(
    val currentLevel: Int = 1,
    val currentPoints: Int = 0,
    val pointsToNextLevel: Int = 300,
    val levelName: String = "Iniciante"
)

@HiltViewModel
class EcoTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val scoreRepository: ScoreRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "EcoTasksViewModel"

    private val _currentUserId = MutableStateFlow<Int?>(null)
    private val _isPopulating = MutableStateFlow(false)

    // ✅ Novos estados adicionados
    private val _showCongratulationsDialog = MutableStateFlow(false)
    private val _lastCompletedTask = MutableStateFlow<EcoTaskUi?>(null)
    private val _userLevelState = MutableStateFlow(UserLevelState())

    val showCongratulationsDialog: StateFlow<Boolean> = _showCongratulationsDialog.asStateFlow()
    val lastCompletedTask: StateFlow<EcoTaskUi?> = _lastCompletedTask.asStateFlow()
    val userLevelState: StateFlow<UserLevelState> = _userLevelState.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            try {
                val authDetails = authService.getCurrentUserProfileDetails()
                if (authDetails != null) {
                    val user = userRepository.getUserByAuthId(authDetails.authId)
                    _currentUserId.value = user?.userId
                    Log.d(TAG, "✅ UserID carregado: ${user?.userId}")

                    // ✅ Carregar nível do usuário
                    user?.userId?.let { userId ->
                        loadUserLevel(userId)
                    }
                } else {
                    Log.w(TAG, "⚠️ Nenhum usuário autenticado")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao carregar userId: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Função para carregar nível do usuário
    private suspend fun loadUserLevel(userId: Int) {
        try {
            val totalScore = scoreRepository.getTotalScoreByUserId(userId)
            val level = (totalScore / 300) + 1
            val currentLevelPoints = totalScore % 300

            _userLevelState.value = UserLevelState(
                currentLevel = level,
                currentPoints = currentLevelPoints,
                pointsToNextLevel = 300,
                levelName = getLevelName(level)
            )

            Log.d(TAG, "📊 Nível atualizado: $level | Pontos: $currentLevelPoints/300 (Total: $totalScore)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao carregar nível: ${e.message}")
            e.printStackTrace()
        }
    }

    // Função para determinar nome do nível
    private fun getLevelName(level: Int): String {
        return when (level) {
            1 -> "Iniciante"
            2 -> "Aprendiz"
            3 -> "Consciente"
            4 -> "Ativista"
            5 -> "Guardião"
            else -> "Mestre Eco"
        }
    }

    // ✅ Função atualizada para pegar ícone por categoria
    private fun getIconResId(category: String): Int {
        return when (category.lowercase()) {
            "resíduos", "residuos" -> R.drawable.ic_sustainability
            "água", "agua" -> R.drawable.water_logo
            "energia" -> R.drawable.lamp_logo
            "mobilidade" -> R.drawable.bike
            "conscientização", "conscientizacao" -> R.drawable.mind_health
            "consumo" -> R.drawable.chariety
            else -> R.drawable.challenges
        }
    }

    val ecoTasks: StateFlow<List<EcoTaskUi>> = combine(
        tasksRepository.getAllTasks(),
        _refreshTrigger
    ) { tasks, _ ->
        val userId = _currentUserId.value
        if (userId == null) {
            Log.w(TAG, "⚠️ UserID ainda é null, retornando lista vazia")
            emptyList()
        } else {
            Log.d(TAG, "🔄 Mapeando ${tasks.size} tarefas para userId: $userId (trigger: ${_refreshTrigger.value})")
            tasks.map { task ->
                val progress = tasksRepository.getOrCreateProgress(userId, task.taskId)
                mapToEcoTaskUi(task, progress)
            }
        }
    }
        .catch { e ->
            Log.e(TAG, "❌ Erro no flow de tarefas: ${e.message}")
            e.printStackTrace()
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun mapToEcoTaskUi(task: TasksEntity, progress: TasksProgressEntity): EcoTaskUi {
        val taskScore = task.rewardPoints

        val taskDurationText = when {
            task.durationInDays > 0 -> "${task.durationInDays} dias"
            else -> "Diário"
        }

        val progressPercentage = if (task.finalPercentual > 0) {
            (progress.currentProgress.toFloat() / task.finalPercentual).coerceIn(0f, 1f)
        } else {
            0f
        }

        val isCompleted = progress.missionStatus.equals("Completa", ignoreCase = true)
                || progressPercentage >= 1.0f

        return EcoTaskUi(
            taskId = task.taskId,
            title = task.missionTitle,
            xp = "$taskScore XP",
            scoreValue = taskScore,
            duration = taskDurationText,
            iconResId = getIconResId(task.missionType),
            progress = progressPercentage,
            isCompleted = isCompleted,
            category = task.missionType,
            level = task.missionStatus
        )
    }

    // ✅ Função atualizada com diálogo de parabéns
    fun completeTask(task: EcoTaskUi) {
        if (task.isCompleted) {
            Log.w(TAG, "⚠️ Tarefa ${task.taskId} já está completa")
            return
        }

        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                if (userId == null) {
                    Log.e(TAG, "❌ UserID é null, não pode completar tarefa")
                    return@launch
                }

                Log.d(TAG, "✅ Completando tarefa ${task.taskId} para userId: $userId")

                // 1. Adicionar pontuação
                scoreRepository.addScoreForCompletedTask(
                    userId = userId,
                    taskId = task.taskId,
                    scoreEarned = task.scoreValue
                )

                // 2. Buscar a tarefa original
                val taskEntity = tasksRepository.getTaskById(task.taskId)
                if (taskEntity == null) {
                    Log.e(TAG, "❌ TaskEntity não encontrada: ${task.taskId}")
                    return@launch
                }

                // 3. Buscar progresso atual
                val progress = tasksRepository.getOrCreateProgress(userId, task.taskId)

                // 4. Atualizar progresso para 100%
                tasksRepository.updateMissionProgress(
                    progressEntity = progress,
                    newProgressValue = taskEntity.finalPercentual.toInt(),
                    totalPercentual = taskEntity.finalPercentual
                )
                Log.d(TAG, "✅ Dados salvos no banco")

                delay(300)

                loadUserLevel(userId)
                Log.d(TAG, "📊 Nível após conclusão: ${_userLevelState.value.currentLevel}, XP: ${_userLevelState.value.currentPoints}/300")

                repeat(3) {
                    _refreshTrigger.value = System.currentTimeMillis()
                    delay(100)
                }
                Log.d(TAG, "🔄 UI forçada a refresh (trigger: ${_refreshTrigger.value})")

                delay(200)

                _lastCompletedTask.value = task
                _showCongratulationsDialog.value = true

                Log.d(TAG, "🎉 Tarefa ${task.taskId} completada com sucesso!")
                Log.d(TAG, "📈 Estado final: Nível ${_userLevelState.value.currentLevel} | ${_userLevelState.value.currentPoints}/${_userLevelState.value.pointsToNextLevel} XP")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao completar tarefa: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // ✅ Função para fechar diálogo
    fun dismissCongratulationsDialog() {
        _showCongratulationsDialog.value = false
        _lastCompletedTask.value = null
    }

    fun forceRefresh() {
        viewModelScope.launch {
            _refreshTrigger.value = System.currentTimeMillis()
            _currentUserId.value?.let { loadUserLevel(it) }
            Log.d(TAG, "🔄 Refresh manual acionado")
        }
    }

    fun populateTasksDatabase() {
        if (_isPopulating.value) {
            Log.d(TAG, "⏳ Já está populando banco...")
            return
        }

        viewModelScope.launch {
            try {
                _isPopulating.value = true
                Log.d(TAG, "🔄 Iniciando população do banco...")

                val existingTasks = tasksRepository.getAllTasks().first()

                if (existingTasks.isNotEmpty()) {
                    Log.d(TAG, "✅ Banco já possui ${existingTasks.size} tarefas")
                    _isPopulating.value = false
                    return@launch
                }

                Log.d(TAG, "📝 Inserindo tarefas no banco...")

                val tasksToInsert = listOf(
                    // BÁSICO
                    TasksEntity(
                        description = "Use uma ecobag ou sacola reutilizável em suas compras",
                        missionTitle = "Usar ecobag/sacola reutilizável",
                        finalPercentual = 100f,
                        missionType = "Resíduos",
                        missionStatus = "Básico", // ✅ Corrigido
                        rewardPoints = 7,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Diário"
                    ),
                    TasksEntity(
                        description = "Feche a torneira enquanto escova os dentes",
                        missionTitle = "Fechar torneira ao escovar dentes",
                        finalPercentual = 100f,
                        missionType = "Água",
                        missionStatus = "Básico", // ✅ Corrigido
                        rewardPoints = 6,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Diário"
                    ),
                    TasksEntity(
                        description = "Separe e descarte corretamente seu lixo reciclável",
                        missionTitle = "Separar lixo reciclável",
                        finalPercentual = 100f,
                        missionType = "Resíduos",
                        missionStatus = "Básico", // ✅ Corrigido
                        rewardPoints = 10,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Diário"
                    ),

                    // INTERMEDIÁRIO
                    TasksEntity(
                        description = "Use bicicleta, transporte público ou vá a pé",
                        missionTitle = "Mobilidade sustentável",
                        finalPercentual = 100f,
                        missionType = "Mobilidade",
                        missionStatus = "Intermediário", // ✅ Corrigido
                        rewardPoints = 20,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Diário"
                    ),
                    TasksEntity(
                        description = "Substitua uma lâmpada comum por LED",
                        missionTitle = "Trocar lâmpada por LED",
                        finalPercentual = 100f,
                        missionType = "Energia",
                        missionStatus = "Intermediário", // ✅ Corrigido
                        rewardPoints = 15,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Nunca"
                    ),
                    TasksEntity(
                        description = "Assista documentário sobre sustentabilidade",
                        missionTitle = "Educação ambiental",
                        finalPercentual = 100f,
                        missionType = "Conscientização",
                        missionStatus = "Intermediário", // ✅ Corrigido
                        rewardPoints = 30,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Semanal"
                    ),
                    TasksEntity(
                        description = "Doe um item que não usa mais",
                        missionTitle = "Doar item não utilizado",
                        finalPercentual = 100f,
                        missionType = "Consumo",
                        missionStatus = "Intermediário", // ✅ Corrigido
                        rewardPoints = 25,
                        initialProgressValue = 0f,
                        durationInDays = 0,
                        resetFrequency = "Nunca"
                    )
                )

                tasksToInsert.forEach { task ->
                    tasksRepository.addMission(task)
                }

                delay(500)

                val finalCount = tasksRepository.getAllTasks().first().size
                Log.d(TAG, "✅ $finalCount tarefas inseridas com sucesso!")

                _refreshTrigger.value = System.currentTimeMillis()

            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao popular banco: ${e.message}")
                e.printStackTrace()
            } finally {
                _isPopulating.value = false
            }
        }
    }
}