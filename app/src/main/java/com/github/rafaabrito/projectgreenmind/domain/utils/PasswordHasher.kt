package com.github.rafaabrito.projectgreenmind.domain.utils
interface PasswordHasher {
    fun hashPassword(password: String): String

    fun verifyPassword(password: String, hashedPassword: String): Boolean
}