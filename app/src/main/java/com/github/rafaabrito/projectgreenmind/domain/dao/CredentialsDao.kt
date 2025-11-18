package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.entities.CredentialsEntity
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.LiveData // Mantido se LiveData é obrigatório

@Dao
interface CredentialsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredential(credentials: CredentialsEntity)

    @Query("SELECT * FROM credentials WHERE userId = :userId LIMIT 1")
    suspend fun getCredentialByUserId(userId: Int): CredentialsEntity?

    @Query("SELECT * FROM credentials WHERE authId = :authId LIMIT 1")
    suspend fun getCredentialByAuthId(authId: String): CredentialsEntity?

    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getCredential(id: Int): CredentialsEntity?

    @Query("Select * from credentials order by id ASC")
    fun getAllCredentials() : LiveData<List<CredentialsEntity>>
}