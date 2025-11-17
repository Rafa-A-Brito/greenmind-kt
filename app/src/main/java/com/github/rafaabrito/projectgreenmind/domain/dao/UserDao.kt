package com.github.rafaabrito.projectgreenmind.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity
@Dao
interface UserDao {
    // Inserção de dados do usuário
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(user: UserEntity)

    // Filtragem do usuário pelo id
    @Query("SELECT * FROM user WHERE userId = :id")
    fun getUser(id: Int): UserEntity

    @Query("""SELECT * FROM user
    WHERE email = :email
    AND hashPassword = :hashPassword
    LIMIT 1""")
    fun verifyEmailAndPassword(email: String, hashPassword: String) : UserEntity
}