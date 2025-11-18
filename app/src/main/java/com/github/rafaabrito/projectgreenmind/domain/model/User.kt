package com.github.rafaabrito.projectgreenmind.domain.model

import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity

data class User(
    val userId: Int,
    val name: String,
    val email: String,
    val firebaseUid: String? = null
    )

fun UserEntity.toDomainModel(): User {
    return User(
        userId = this.userId,
        name = this.name ?: "Usuário", // Usar um fallback se o nome for null
        email = this.email,
        firebaseUid = this.firebaseUid
    )
}