package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Inserção de dados do usuário 
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    //Filtragem do usuário pelo id (
    @Query("SELECT * FROM user WHERE userId = :id")
    fun getUser(id: Int): Flow<UserEntity?> // Retorna Flow para observação

    // Busca user pelo Uid
    @Query("SELECT * FROM user WHERE firebaseUid = :uid")
    suspend fun getUserByFirebaseUid(uid: String): UserEntity?

    //Busca user pelo Email local
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
}