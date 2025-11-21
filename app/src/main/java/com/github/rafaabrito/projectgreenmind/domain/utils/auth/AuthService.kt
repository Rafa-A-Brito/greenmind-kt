package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import android.content.Intent
import android.content.IntentSender

interface AuthService {

    suspend fun signInWithEmailPassword(email: String, password: String): AuthResponse

    suspend fun signUpWithEmailPassword(email: String, password: String, name: String?): AuthResponse
    suspend fun startSocialSignIn(provider: SocialProvider): IntentSender?
    suspend fun completeSocialSignIn(intent: Intent): AuthResponse

    data class AuthResult(
        val authId: String,
        val email: String,
        val name: String?
    )

    sealed interface AuthResponse {
        data class Success(
            val authId: String,
            val email: String,
            val name: String?,
            val isNewUser: Boolean
        ) : AuthResponse

        data class Error(val message: String) : AuthResponse
    }

    enum class SocialProvider {
        GOOGLE
    }}