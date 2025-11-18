package com.github.rafaabrito.projectgreenmind.data.repository

import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreProgressDao
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ScoreRepository(
    private val scoreDao: ScoreDao,
    private val scoreProgressDao: ScoreProgressDao
) {

    // Retorna a pontuação total e nível do usuário logado (observável).

    fun getScoreByUserId(userId: Int): Flow<ScoreEntity?> {
        return scoreDao.getScoreByUserId(userId)
    }

    // Retorna o ranking de todos os usuários (ordenado por pontuação total).
    fun getAllScores(): Flow<List<ScoreEntity>> {
        return scoreDao.getAllScores()
    }

    suspend fun addScoreForCompletedTask(userId: Int, taskId: Int, scoreEarned: Int) {

        // Tenta buscar o progresso existente para evitar duplicidade ou atualizar
        val progressEntry = scoreProgressDao.getProgressByTask(userId, taskId).first()

        if (progressEntry == null) {
            // Insere um novo registro de ganho de pontos
            val progressEntry = ScoreProgressEntity(
                userId = userId,
                taskId = taskId,
                scoreEarned = scoreEarned,
                isCompleted = true // Marca como concluída
            )
            scoreProgressDao.insertProgress(progressEntry)
        } else if (!progressEntry.isCompleted) {
            // Atualiza se o registro existe, mas a tarefa não foi marcada como concluída antes
            val updatedProgress = progressEntry.copy(
                scoreEarned = scoreEarned,
                isCompleted = true
            )
            scoreProgressDao.updateProgress(updatedProgress)
        } else {
            return
        }


        val newTotalScore = scoreProgressDao.getTotalScoreForUser(userId).first() ?: 0

        val currentScoreEntity = scoreDao.getScoreByUserId(userId).first()

        val finalScoreEntity = if (currentScoreEntity == null) {
            // Cria novo registro (primeira pontuação)
            ScoreEntity(
                userId = userId,
                totalScore = newTotalScore,
                scoreLevel = calculateLevel(newTotalScore), // Cálculo de nível
                missionScore = scoreEarned
            )
        } else {
            // Atualiza registro existente
            currentScoreEntity.copy(
                totalScore = newTotalScore,
                scoreLevel = calculateLevel(newTotalScore),
                missionScore = scoreEarned // Pontuação da última missão
            )
        }

        scoreDao.updateScore(finalScoreEntity)
    }

    private fun calculateLevel(totalScore: Int): Int {
        // Exemplo: 1000 pontos por nível
        return (totalScore / 1000) + 1
    }
}