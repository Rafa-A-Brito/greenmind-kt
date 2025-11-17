package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.entities.CredentialsEntity

@Dao
interface CredentialsDao {

    // Inserção de credenciais
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredential(credentials: CredentialsEntity)

    // Busca credencial pelo ID do usuário 
    @Query("SELECT * FROM credentials WHERE userId = :userId LIMIT 1")
    suspend fun getCredentialByUserId(userId: Int): CredentialsEntity?

    // Buscar credencial pelo ID de autenticação 
    @Query("SELECT * FROM credentials WHERE authId = :authId LIMIT 1")
    suspend fun getCredentialByAuthId(authId: String): CredentialsEntity?
    
    // Filtragem do usuário pelo id 
    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getCredential(id: Int): CredentialsEntity?
    
    // Seleção da lista de credenciais de usuarios
    @Query("Select * from credentials order by id ASC")
    fun getAllCredentials() : LiveData<List<CredentialsEntity>>
}
