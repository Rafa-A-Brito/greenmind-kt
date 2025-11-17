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

    // Filtragem do usuário pelo id
    @Query("SELECT * FROM credentials WHERE id = :id")
    fun getCredential(id: Int): CredentialsEntity

    // Verificação de autenticidade (email e senha) pelo id retornado
    @Query(""" SELECT * FROM credentials WHERE userId = :userId AND authId = :authId LIMIT 1 """)
    suspend fun verifyAuthCredential(userId: Int, authId : String) : CredentialsEntity?
?

    // Seleção da lista de credenciais de usuarios
    @Query("Select * from credentials order by id ASC")
    fun getAllCredentials() : LiveData<List<CredentialsEntity>>
}
