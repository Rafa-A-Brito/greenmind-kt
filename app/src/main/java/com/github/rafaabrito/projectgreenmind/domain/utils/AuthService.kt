package com.github.rafaabrito.projectgreenmind.domain.utils

interface AuthService {
    suspend fun signInWithEmail(email: String, password: String): AuthResult?

    suspend fun signUpWithEmail(email: String, password: String, name: String?): AuthResult?

    fun startSocialSignIn(provider: SocialProvider)
    suspend fun authenticateWithGoogleToken(idToken: String): AuthResult?
    suspend fun authenticateWithFacebookToken(accessToken: String): AuthResult?

    // Estrutura de retorno para simplificar
    data class AuthResult(
        val authId: String,
        val email: String,
        val name: String?,
        val isNewUser: Boolean
    )

    enum class SocialProvider { GOOGLE, FACEBOOK }
}