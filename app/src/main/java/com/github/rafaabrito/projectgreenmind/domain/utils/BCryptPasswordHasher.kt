package com.github.rafaabrito.projectgreenmind.domain.utils

import org.mindrot.jbcrypt.BCrypt

// A implementação concreta que usa a biblioteca BCrypt
class BCryptPasswordHasher : PasswordHasher {

    // O fator 'log rounds' (cost) define a complexidade do hashing.
    // 10 é um bom valor padrão. Valores mais altos são mais seguros, mas mais lentos.
    private val saltRounds = 10

    override fun hashPassword(password: String): String {
        // BCrypt.hashpw cuida da geração do salt e do hashing
        return BCrypt.hashpw(password, BCrypt.gensalt(saltRounds))
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // BCrypt.checkpw é um método seguro para comparação de senha
        // Ele extrai o salt do hash armazenado e verifica a senha
        return BCrypt.checkpw(password, hashedPassword)
    }
}