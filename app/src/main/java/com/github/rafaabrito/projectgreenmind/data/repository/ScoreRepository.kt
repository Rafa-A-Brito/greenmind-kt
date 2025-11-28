package com.github.rafaabrito.projectgreenmind.data.repository

import android.util.Log
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreProgressDao
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreProgressEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ScoreRepository @Inject constructor(
    private val scoreDao: ScoreDao,
    private val scoreProgressDao: ScoreProgressDao
) {
    private val TAG = "ScoreRepository"

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
            val updatedProgress = progressEntry.copy(
                scoreEarned = scoreEarned,
                isCompleted = true
            )
            scoreProgressDao.updateProgress(updatedProgress)
            Log.d(TAG, "✅ Tarefa atualizada para completa: +$scoreEarned XP")
        } else {
            Log.w(TAG, "⚠️ Tarefa $taskId já estava completa")
            return
        }

        val newTotalScore = scoreProgressDao.getTotalScoreForUserDirect(userId)

        val currentScoreEntity = scoreDao.getScoreByUserId(userId).first()

        val finalScoreEntity = currentScoreEntity?.copy(
            totalScore = newTotalScore,
            scoreLevel = calculateLevel(newTotalScore),
            missionScore = scoreEarned
        ) ?: ScoreEntity(
            userId = userId,
            totalScore = newTotalScore,
            scoreLevel = calculateLevel(newTotalScore),
            missionScore = scoreEarned
        )

        scoreDao.updateScore(finalScoreEntity.copy(totalScore = newTotalScore))
        Log.d(TAG, "✅ ScoreEntity atualizada: Level ${finalScoreEntity.scoreLevel}, Total: ${finalScoreEntity.totalScore}")
    }

    private fun calculateLevel(totalScore: Int): Int {
        return (totalScore / 300) + 1
    }

    suspend fun getTotalScoreByUserId(userId: Int): Int {
        return try {
            val total = scoreProgressDao.getTotalScoreForUserDirect(userId)
            Log.d(TAG, "📊 Total calculado da ScoreProgress: $total XP")
            total
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao calcular total: ${e.message}")
            0
        }
    }
}