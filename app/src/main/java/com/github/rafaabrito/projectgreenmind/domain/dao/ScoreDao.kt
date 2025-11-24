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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity)

    @Update
    suspend fun updateScore(score: ScoreEntity)

    @Delete
    suspend fun deleteScore(score: ScoreEntity)

    @Query("SELECT * FROM score WHERE userId = :userId LIMIT 1")
    fun getScoreByUserId(userId: Int): Flow<ScoreEntity?>

    @Query("SELECT * FROM score ORDER BY totalScore DESC")
    fun getAllScores(): Flow<List<ScoreEntity>>

    // ✅ CORRIGIDO: Usa score_progress (snake_case)
    @Query("""
        SELECT COALESCE(SUM(scoreEarned), 0) 
        FROM score_progress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    suspend fun getTotalScoreByUserId(userId: Int): Int

    @Query("SELECT COALESCE(totalScore, 0) FROM score WHERE userId = :userId LIMIT 1")
    suspend fun getTotalScoreFromScoreTable(userId: Int): Int
}