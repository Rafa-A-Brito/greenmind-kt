package com.github.rafaabrito.projectgreenmind.domain.utils

import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher
import javax.inject.Inject

class SimplePasswordHasherImpl @Inject constructor() : PasswordHasher {

    // ⚠️ Atenção: Esta é uma implementação SIMPLES para demonstração.
    // Em um projeto real, use bibliotecas robustas como BCrypt.

    override fun hashPassword(password: String): String {
        // Simplesmente reverte e concatena para simular hashing (MUITO INSEGURO!)
        return password.reversed() + "SALT"
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // Verifica se a senha corresponde ao hash simulado
        return hashedPassword == password.reversed() + "SALT"
    }
}