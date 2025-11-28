package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.*
import com.github.rafaabrito.projectgreenmind.domain.entities.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE userId = :userId")
    fun getStreakByUserId(userId: Int): Flow<StreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: StreakEntity)

    @Query("DELETE FROM streak WHERE userId = :userId")
    suspend fun deleteStreak(userId: Int)
}