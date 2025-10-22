package com.github.rafaabrito.projectgreenmind.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.data.db.UserEntity

@Dao
interface UserDao {
    // Inserção de dados do usuário
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(user: UserEntity)

    // Filtragem do usuário pelo id
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: Int): UserEntity

    // Verificação de autenticidade (email e senha) pelo id retornado
    @Query("SELECT id FROM user WHERE email = :email and password = :password")
    fun login(email: String, password: String): Int
}