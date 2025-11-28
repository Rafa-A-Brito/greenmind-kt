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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ScoreProgressEntity)

    @Update
    suspend fun updateProgress(progress: ScoreProgressEntity)

    @Delete
    suspend fun deleteProgress(progress: ScoreProgressEntity)

    // CORRIGIDO: score_progress (snake_case)
    @Query("SELECT * FROM score_progress WHERE userId = :userId AND taskId = :taskId LIMIT 1")
    fun getProgressByTask(userId: Int, taskId: Int): Flow<ScoreProgressEntity?>

    // CORRIGIDO: score_progress
    @Query("SELECT SUM(scoreEarned) FROM score_progress WHERE userId = :userId AND isCompleted = 1")
    fun getTotalScoreForUser(userId: Int): Flow<Int?>

    // CORRIGIDO: score_progress
    @Query("""
        SELECT COALESCE(SUM(scoreEarned), 0) 
        FROM score_progress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    suspend fun getTotalScoreForUserDirect(userId: Int): Int
}