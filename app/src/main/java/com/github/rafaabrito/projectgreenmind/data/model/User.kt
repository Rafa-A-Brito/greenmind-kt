package com.github.rafaabrito.projectgreenmind.data.model

import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity

data class User(
    val userId: Int,
    val name: String,
    val email: String,
    val firebaseUid: String? = null
    )

data class UserState(
    val user: User? = null,
    val photoUrl: String? = null,
    val userXP: Int = 0,           // ✅ NOVO: XP do usuário
    val userLevel: Int = 0,        // ✅ NOVO: Nível do usuário
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Helper para obter apenas o primeiro nome do usuário
    val userName: String?
        get() = user?.name?.split(" ")?.firstOrNull()
}

fun UserEntity.toDomainModel(): User {
    return User(
        userId = this.userId,
        name = this.name ?: "Usuário", // Usar um fallback se o nome for null
        email = this.email,
        firebaseUid = this.firebaseUid
    )
}