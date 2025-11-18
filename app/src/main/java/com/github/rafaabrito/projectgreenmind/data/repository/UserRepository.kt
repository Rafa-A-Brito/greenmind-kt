package com.github.rafaabrito.projectgreenmind.data.repository

import androidx.room.Query
import com.github.rafaabrito.projectgreenmind.domain.dao.UserDao
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.CredentialsEntity //
import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

public class UserRepository @Inject constructor(
    private val userDao: UserDao, 
    private val credentialsDao: CredentialsDao,
    private val passwordHasher: PasswordHasher
) {

    // Função para guardar as informações do User
    suspend fun saveUser(user: UserEntity){
        userDao.saveUser(user)
    }

    // Função para obter usuário por ID (retorna Flow)
    fun getUserById(id: Int): Flow<UserEntity?> {
        return userDao.getUser(id)
    }

    suspend fun createNewUser(name: String, email: String, password: String): Boolean {
        if (userDao.getUserByEmail(email) != null) {
            return false // Email já cadastrado
        }
        
        val hashedPassword = passwordHasher.hashPassword(password)

        val newUser = UserEntity(
            name = name,
            email = email,
            hashPassword = hashedPassword,
            firebaseUid = "ROOM_LOCAL_${System.currentTimeMillis()}" 
        )
        
        userDao.saveUser(newUser)
        return true
    }

    suspend fun getUserByAuthId(authId: String): UserEntity? {
        val credential = credentialsDao.getCredentialByAuthId(authId)
        if (credential != null) {
            return userDao.getUser(credential.userId).first() // Obtém o UserEntity
        }
        return userDao.getUserByFirebaseUid(authId)
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)

        if (user != null && user.hashPassword != null) {
            
            val isPasswordCorrect = passwordHasher.verifyPassword(password, user.hashPassword)
            
            if (isPasswordCorrect) {
                return user 
            }
        }
        return null 
    }

    suspend fun associateFirebaseUser(
        name: String?,
        email: String,
        authId: String
    ): UserEntity {

        val existingCredential = credentialsDao.getCredentialByAuthId(authId)

        if (existingCredential != null) {
            return userDao.getUser(existingCredential.userId).first()!!
        }

        val newUser = UserEntity(
            name = name,
            email = email,
            hashPassword = null,
            firebaseUid = authId
        )
        userDao.saveUser(newUser)

        val userWithId = userDao.getUserByFirebaseUid(authId)!!

        val newCredential = CredentialsEntity(
            userId = userWithId.userId,
            authId = authId
        )
        credentialsDao.saveCredential(newCredential)

        return userWithId
    }
}