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

    //  Inserção do progresso inicial ou novo registro
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TasksProgressEntity)

    //  Atualização de progresso da tarefa
    @Update
    suspend fun updateProgress(progress: TasksProgressEntity)

    // Busca o progresso de uma tarefa específica para um usuário.

    @Query("SELECT * FROM tasks_progress WHERE userId = :userId AND missionId = :missionId LIMIT 1")
    fun getProgressByMission(userId: String, missionId: String): Flow<TasksProgressEntity?>

    // Busca todas as tarefas que estão em um determinado status para um usuário
    @Query("SELECT * FROM tasks_progress WHERE userId = :userId AND missionStatus = :status ORDER BY lastUpdated DESC")
    fun getTasksByStatus(userId: String, status: String): Flow<List<TasksProgressEntity>>
}