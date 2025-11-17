package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.FilesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilesDao {

    // Inserção de arquivos na base de dados
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFiles(files: FilesEntity)

    // Atualização de arquivos
    @Update
    suspend fun updateFile(files: FilesEntity)

    // Exclusão de mídia
    @Delete
    suspend fun deleteFile(files: FilesEntity)

    // Função para obter todos os arquivos
    @Query("SELECT * FROM files WHERE id = :fileId")
    fun getFileById(fileId: Int): Flow<FilesEntity>
}