package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.github.rafaabrito.projectgreenmind.R
import kotlin.coroutines.cancellation.CancellationException
import dagger.hilt.android.qualifiers.ApplicationContext

class FirebaseAuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    @ApplicationContext private val context: Context
) : AuthService {
    private fun mapFirebaseUserToAuthResponse(user: FirebaseUser, isNewUser: Boolean): AuthService.AuthResponse.Success {
        return AuthService.AuthResponse.Success(
            authId = user.uid,
            email = user.email ?: "",
            name = user.displayName,
            isNewUser = isNewUser
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    // Função auxiliar para mapear exceções comuns
    private fun mapFirebaseExceptionToError(e: Exception): AuthService.AuthResponse.Error {
        val errorMessage = when (e) {
            is FirebaseAuthWeakPasswordException -> "A senha é muito fraca. Escolha uma senha mais forte."
            is FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas. Verifique seu email e senha."
            is FirebaseAuthInvalidUserException -> "O usuário não está registrado ou foi desativado."
            is FirebaseAuthUserCollisionException -> "Este email já está sendo usado por outra conta."
            else -> "Erro desconhecido: ${e.localizedMessage ?: "Falha na autenticação."}"
        }
        return AuthService.AuthResponse.Error(errorMessage)
    }


    override suspend fun signInWithEmailPassword(email: String, password: String): AuthService.AuthResponse {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                mapFirebaseUserToAuthResponse(user, isNewUser = false)
            } else {
                AuthService.AuthResponse.Error("Falha ao obter usuário após o login.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mapFirebaseExceptionToError(e)
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String, name: String?): AuthService.AuthResponse {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                if (name != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }
                mapFirebaseUserToAuthResponse(user, isNewUser = true)
            } else {
                AuthService.AuthResponse.Error("Falha ao obter usuário após o registro.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mapFirebaseExceptionToError(e)
        }
    }

    override suspend fun startSocialSignIn(provider: AuthService.SocialProvider): IntentSender? {
        if (provider != AuthService.SocialProvider.GOOGLE) return null

        return try {
            val result = oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
            result.pendingIntent.intentSender
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
    }

    override suspend fun completeSocialSignIn(intent: Intent): AuthService.AuthResponse {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
                ?: return AuthService.AuthResponse.Error("Token de ID do Google não encontrado.")

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val user = result.user

            if (user != null) {
                mapFirebaseUserToAuthResponse(user, isNewUser = result.additionalUserInfo?.isNewUser ?: false)
            } else {
                AuthService.AuthResponse.Error("Falha ao obter usuário após autenticação social.")
            }
        } catch(e: Exception) {
            e.printStackTrace()
            mapFirebaseExceptionToError(e)
        }
    }

}