// AuthService.kt (Refatorado)

package com.github.rafaabrito.projectgreenmind.domain.utils

interface AuthService {

    suspend fun signInWithEmailPassword(email: String, password: String): AuthResponse

    suspend fun signUpWithEmailPassword(email: String, password: String): AuthResponse

    fun startSocialSignIn(provider: SocialProvider)
    suspend fun authenticateWithGoogleToken(idToken: String): AuthResponse
    suspend fun authenticateWithFacebookToken(accessToken: String): AuthResponse

    sealed interface AuthResponse {
        data class Success(
            val authId: String,
            val email: String,
            val name: String?,
            val isNewUser: Boolean
        ) : AuthResponse

        data class Error(val message: String) : AuthResponse
    }

    enum class SocialProvider { GOOGLE, FACEBOOK }
}