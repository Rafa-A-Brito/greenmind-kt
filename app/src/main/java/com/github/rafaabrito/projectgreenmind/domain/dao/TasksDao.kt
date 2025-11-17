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
    /**
     * Insere uma nova tarefa no banco de dados.
     * Usa REPLACE em caso de conflito, útil se o ID for definido manualmente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TasksEntity)

    // --- Atualização da tarefa segundo temas específicos ---
    /**
     * Atualiza uma tarefa existente. A atualização é baseada no 'taskId' primário.
     */
    @Update
    suspend fun updateTask(task: TasksEntity)

    // --- Remoção da tarefa após conclusão ---
    /**
     * Remove uma tarefa existente do banco de dados (geralmente após a conclusão, como você especificou).
     */
    @Delete
    suspend fun deleteTask(task: TasksEntity)

    // --- Funções Adicionais Úteis (Leitura) ---

    /**
     * Busca uma tarefa específica pelo seu ID.
     */
    @Query("SELECT * FROM tasks WHERE taskId = :id")
    fun getTaskById(id: Int): Flow<TasksEntity>

    /**
     * Busca todas as tarefas, ordenadas pelo título da missão.
     */
    @Query("SELECT * FROM tasks ORDER BY missionTitle ASC")
    fun getAllTasks(): Flow<List<TasksEntity>>

    /**
     * Busca tarefas por um tipo de missão específico (tema).
     */
    @Query("SELECT * FROM tasks WHERE missionType = :type ORDER BY missionTitle ASC")
    fun getTasksByType(type: String): Flow<List<TasksEntity>>
}