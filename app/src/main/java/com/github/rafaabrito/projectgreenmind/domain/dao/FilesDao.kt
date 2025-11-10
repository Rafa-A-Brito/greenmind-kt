package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.entities.FilesEntity

@Dao
interface FilesDao {
    //Inserção de arquivos na base de dados
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFiles(files: FilesEntity)

    // Atualização de arquivos
    
    // Exclusão de mídia
}