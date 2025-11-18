package com.github.rafaabrito.projectgreenmind.domain.utils

import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher : PasswordHasher {
    private val saltRounds = 10

    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(saltRounds))
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {

        return BCrypt.checkpw(password, hashedPassword)
    }
}