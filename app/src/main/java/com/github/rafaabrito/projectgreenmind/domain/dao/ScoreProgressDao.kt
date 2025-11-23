package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreProgressDao {

    // --- Inserção de pontos por progresso ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ScoreProgressEntity)

    // --- Atualização da pontuação por progresso ---
    @Update
    suspend fun updateProgress(progress: ScoreProgressEntity)

    // --- Remover pontos ---
    @Delete
    suspend fun deleteProgress(progress: ScoreProgressEntity)

    // --- Funções Adicionais Úteis ---

    @Query("SELECT * FROM scoreProgress WHERE userId = :userId AND taskId = :taskId LIMIT 1")
    fun getProgressByTask(userId: Int, taskId: Int): Flow<ScoreProgressEntity?>

    @Query("SELECT SUM(scoreEarned) FROM scoreProgress WHERE userId = :userId AND isCompleted = 1")
    fun getTotalScoreForUser(userId: Int): Flow<Int?>


    // Versão Suspend (para uso direto/imediato)
    @Query("""
        SELECT COALESCE(SUM(scoreEarned), 0) 
        FROM scoreProgress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    suspend fun getTotalScoreForUserDirect(userId: Int): Int
}