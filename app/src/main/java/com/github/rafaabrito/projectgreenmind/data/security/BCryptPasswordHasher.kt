package com.github.rafaabrito.projectgreenmind.data.security

import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BCryptPasswordHasher @Inject constructor() : PasswordHasher {

    private val saltLogRounds = 10

    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(saltLogRounds))
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // Verifica se a senha simples corresponde ao hash armazenado.
        return BCrypt.checkpw(password, hashedPassword)
    }
}