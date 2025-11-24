package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TasksProgressEntity)

    @Update
    suspend fun updateProgress(progress: TasksProgressEntity)

    @Query("SELECT * FROM tasks_progress WHERE userId = :userId AND taskId = :taskId LIMIT 1")
    fun getProgressByMission(userId: Int, taskId: Int): Flow<TasksProgressEntity?>

    @Query("SELECT * FROM tasks_progress WHERE userId = :userId AND missionStatus = :status ORDER BY lastUpdated DESC")
    fun getTasksByStatus(userId: Int, status: String): Flow<List<TasksProgressEntity>>

    // Método para buscar TODAS as progressões do usuário
    @Query("SELECT * FROM tasks_progress WHERE userId = :userId")
    fun getAllProgressByUser(userId: Int): Flow<List<TasksProgressEntity>>
}