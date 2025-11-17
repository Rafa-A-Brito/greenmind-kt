package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalEcoDao {
    // Inserção da localidade (endereço -> rua, número, etc)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localEco: LocalEcoEntity)

    //  Atualização de endereço conforme mudanças de localização
    @Update
    suspend fun update(localEco: LocalEcoEntity)

    // Apagar o endereço no banco
    @Delete
    suspend fun delete(localEco: LocalEcoEntity)

    // Busca uma localidade específica pelo seu ID
    @Query("SELECT * FROM localEco WHERE id = :id")
    fun getLocalEcoById(id: Int): Flow<LocalEcoEntity> // Retorna Flow para observação

    // Busca todas as localidades cadastradas, ordenadas pelo nome.
    @Query("SELECT * FROM localEco ORDER BY localName ASC")
    fun getAllLocalEco(): Flow<List<LocalEcoEntity>>
}