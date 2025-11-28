package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import android.content.Intent
import android.content.IntentSender
import androidx.credentials.GetCredentialRequest

interface AuthService {

    suspend fun signInWithEmailPassword(email: String, password: String): AuthResponse

    suspend fun signUpWithEmailPassword(email: String, password: String, name: String?): AuthResponse

    suspend fun getGoogleIdCredentialRequest(): GetCredentialRequest?

    suspend fun signInWithGoogleIdToken(idToken: String): AuthResponse

    suspend fun getCurrentUserProfileDetails(): AuthProfileDetails?

    data class AuthResult(
        val authId: String,
        val email: String,
        val name: String?
    )

    sealed class AuthResponse {
        // A chave para o Room! O authId será o Firebase UID.
        data class Success(
            val name: String?,
            val email: String,
            val authId: String, // Firebase UID
            val profilePictureUrl: String?
        ) : AuthResponse()
        data class Error(val message: String) : AuthResponse()
        data object Cancelled : AuthResponse()
        data object Unknown : AuthResponse()
    }
    enum class SocialProvider {
        GOOGLE
    }

    data class AuthProfileDetails(
        val authId: String,
        val email: String,
        val name: String?,
        val profilePictureUrl: String?
    )
}