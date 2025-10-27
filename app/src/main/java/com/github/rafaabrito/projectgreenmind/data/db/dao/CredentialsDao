package com.github.rafaabrito.projectgreenmind.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.data.entities.CredentialsEntity

@Dao
interface UserDao {
    // Inserção de credencias
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(credentials: CredentialsEntity)

    // Filtragem do usuário pelo id
    @Query("SELECT * FROM credentials WHERE id = :id")
    fun getUser(id: Int): CredentialsEntity

    // Verificação de autenticidade (email e senha) pelo id retornado
    @Query("SELECT id FROM credentials WHERE email = :email and password = :hashPassword")
    fun login(email: String, hasPassword: String): Int
}
