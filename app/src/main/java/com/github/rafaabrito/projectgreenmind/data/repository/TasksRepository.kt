package com.github.rafaabrito.projectgreenmind.data.repository

import android.util.Log
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksProgressDao
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val tasksDao: TasksDao,
    private val tasksProgressDao: TasksProgressDao
) {
    private val TAG = "TasksRepository"

    // ✅ Retorna Flow de todas as tarefas
    fun getAllTasks(): Flow<List<TasksEntity>> {
        return tasksDao.getAllTasks()
    }

    // ✅ CORRIGIDO: Retorna TasksEntity? (nullable)
    suspend fun getTaskById(taskId: Int): TasksEntity? {
        return try {
            tasksDao.getTaskById(taskId)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao buscar tarefa $taskId: ${e.message}")
            null
        }
    }

    suspend fun addMission(task: TasksEntity) {
        try {
            tasksDao.insertTask(task)
            Log.d(TAG, "✅ Tarefa inserida: ${task.missionTitle}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao inserir tarefa: ${e.message}")
            throw e
        }
    }

    // ✅ CRÍTICO: Busca ou cria progresso para uma tarefa
    suspend fun getOrCreateProgress(userId: Int, taskId: Int): TasksProgressEntity {
        return try {
            // Busca progresso existente
            val existingProgress = tasksProgressDao.getProgressByMission(userId, taskId).first()

            if (existingProgress != null) {
                Log.d(TAG, "📊 Progresso encontrado para taskId=$taskId, userId=$userId")
                existingProgress
            } else {
                // Cria novo progresso
                val newProgress = TasksProgressEntity(
                    userId = userId,
                    taskId = taskId, // ✅ Usa taskId, não missionId
                    currentProgress = 0f,
                    missionStatus = "Em andamento",
                    lastUpdated = System.currentTimeMillis()
                )
                tasksProgressDao.insertProgress(newProgress)
                Log.d(TAG, "✨ Novo progresso criado para taskId=$taskId, userId=$userId")
                newProgress
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao obter/criar progresso: ${e.message}")
            e.printStackTrace()
            // Retorna progresso padrão em caso de erro
            TasksProgressEntity(
                userId = userId,
                taskId = taskId,
                currentProgress = 0f,
                missionStatus = "Em andamento",
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    // ✅ Atualiza progresso da missão
    suspend fun updateMissionProgress(
        progressEntity: TasksProgressEntity,
        newProgressValue: Int,
        totalPercentual: Float
    ) {
        try {
            val updatedProgress = progressEntity.copy(
                currentProgress = newProgressValue.toFloat(),
                missionStatus = if (newProgressValue >= totalPercentual) "Completa" else "Em andamento",
                lastUpdated = System.currentTimeMillis()
            )

            tasksProgressDao.updateProgress(updatedProgress)
            Log.d(TAG, "✅ Progresso atualizado: taskId=${progressEntity.taskId}, status=${updatedProgress.missionStatus}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao atualizar progresso: ${e.message}")
            throw e
        }
    }
}