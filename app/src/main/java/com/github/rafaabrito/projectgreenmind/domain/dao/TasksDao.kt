package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    // --- Inserção de tarefas a serem feitas ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TasksEntity)

    @Update
    suspend fun updateTask(task: TasksEntity)

    @Delete
    suspend fun deleteTask(task: TasksEntity)

    @Query("SELECT * FROM tasks WHERE taskId = :id LIMIT 1")
    suspend fun getTaskById(id: Int): TasksEntity?

    @Query("SELECT * FROM tasks ORDER BY missionTitle ASC")
    fun getAllTasks(): Flow<List<TasksEntity>>

    @Query("SELECT * FROM tasks WHERE missionType = :type ORDER BY missionTitle ASC")
    fun getTasksByType(type: String): Flow<List<TasksEntity>>
}