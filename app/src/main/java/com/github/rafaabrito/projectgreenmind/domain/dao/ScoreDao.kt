package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    // Inserção de pontos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity)

    // Atualização da pontuação
    @Update
    suspend fun updateScore(score: ScoreEntity)

    // Remover pontos
    @Delete
    suspend fun deleteScore(score: ScoreEntity)

    @Query("SELECT * FROM score WHERE userId = :userId LIMIT 1")
    fun getScoreByUserId(userId: Int): Flow<ScoreEntity?>

    @Query("SELECT * FROM score ORDER BY totalScore DESC")
    fun getAllScores(): Flow<List<ScoreEntity>>

    @Query("""
        SELECT COALESCE(SUM(scoreEarned), 0) 
        FROM scoreProgress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    suspend fun getTotalScoreByUserId(userId: Int): Int

    @Query("SELECT COALESCE(totalScore, 0) FROM score WHERE userId = :userId LIMIT 1")
    suspend fun getTotalScoreFromScoreTable(userId: Int): Int
}