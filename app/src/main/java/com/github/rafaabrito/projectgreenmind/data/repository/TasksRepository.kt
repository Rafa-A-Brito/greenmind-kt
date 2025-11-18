package com.github.rafaabrito.projectgreenmind.data.repository

import com.github.rafaabrito.projectgreenmind.domain.dao.TasksDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksProgressDao
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TasksRepository(
    private val tasksDao: TasksDao,
    private val tasksProgressDao: TasksProgressDao
) {

    fun getAllTasks(): Flow<List<TasksEntity>> {
        return tasksDao.getAllTasks()
    }

    fun getTasksByType(type: String): Flow<List<TasksEntity>> {
        return tasksDao.getTasksByType(type)
    }

    suspend fun getTaskById(id: Int): TasksEntity? {

        return tasksDao.getTaskById(id).first()
    }

    suspend fun getOrCreateProgress(userId: Int, missionId: Int): TasksProgressEntity {
        // Tenta buscar o progresso existente
        val existingProgress = tasksProgressDao.getProgressByMission(userId, missionId).first()

        if (existingProgress != null) {
            return existingProgress
        }

        // Se não existe, cria um novo registro
        val currentTime = System.currentTimeMillis()
        val newProgress = TasksProgressEntity(
            userId = userId,
            missionId = missionId,
            missionStatus = "Pendente",
            currentProgress = 0,
            startDate = currentTime,
            lastUpdated = currentTime,
            attemptsCount = 0,
            finishingDate = null
        )
        // Salva o novo registro no banco
        tasksProgressDao.insertProgress(newProgress)

        return tasksProgressDao.getProgressByMission(userId, missionId).first() ?: newProgress
    }

    // Atualiza o progresso de uma missão para um usuário.
    suspend fun updateMissionProgress(
        progressEntity: TasksProgressEntity,
        newProgressValue: Int,
        totalPercentual: Float
    ) {
        val isCompleted = newProgressValue >= totalPercentual.toInt()
        val currentTime = System.currentTimeMillis()

        val updatedProgress = progressEntity.copy(
            currentProgress = newProgressValue.coerceIn(0, totalPercentual.toInt()),
            missionStatus = if (isCompleted) "Completa" else "Em progresso",
            lastUpdated = currentTime,
            finishingDate = if (isCompleted) currentTime else null
        )

        tasksProgressDao.updateProgress(updatedProgress)
    }


     // Função que um Admin usaria para adicionar novas missões ao jogo.

    suspend fun addMission(task: TasksEntity) {
        tasksDao.insertTask(task)
    }

    //Função que um Admin usaria para remover uma missão.

    suspend fun removeMission(task: TasksEntity) {
        tasksDao.deleteTask(task)
    }

}