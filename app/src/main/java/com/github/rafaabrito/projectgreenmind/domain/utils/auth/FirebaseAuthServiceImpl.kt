package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.credentials.GetCredentialException
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
import com.google.firebase.auth.*
import com.github.rafaabrito.projectgreenmind.R
import kotlin.coroutines.cancellation.CancellationException
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

class FirebaseAuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    @param:ApplicationContext private val context: Context
) : AuthService {
    private fun mapFirebaseUserToAuthResponse(user: FirebaseUser): AuthService.AuthResponse.Success {
        return AuthService.AuthResponse.Success(
            authId = user.uid,
            email = user.email ?: "",
            name = user.displayName,
            profilePictureUrl = user.photoUrl?.toString()
        )
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
                mapFirebaseUserToAuthResponse(user)
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
                mapFirebaseUserToAuthResponse(user)
            } else {
                AuthService.AuthResponse.Error("Falha ao obter usuário após o registro.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mapFirebaseExceptionToError(e)
        }
    }

    override suspend fun getGoogleIdCredentialRequest(): GetCredentialRequest? {
        return try {
            val serverClientId = context.getString(R.string.default_web_client_id)

            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("🔍 CRIANDO GOOGLE ID REQUEST")
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("📦 Package: ${context.packageName}")
            println("🔑 Web Client ID: ${serverClientId.take(50)}...")
            println("🔑 Termina com .apps.googleusercontent.com: ${serverClientId.endsWith(".apps.googleusercontent.com")}")
            println("🔑 Tamanho: ${serverClientId.length} caracteres")

            if (serverClientId.length < 60 || !serverClientId.endsWith(".apps.googleusercontent.com")) {
                println("❌ ERRO: Web Client ID parece estar incorreto!")
                return null
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            println("✅ GoogleIdOption criado")

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            println("✅ GetCredentialRequest criado com sucesso")
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            return request

        } catch (e: Exception) {
            println("❌ ERRO ao criar request:")
            println("   Tipo: ${e.javaClass.simpleName}")
            println("   Mensagem: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): AuthService.AuthResponse {
        return try {
            println("🔐 Iniciando autenticação com idToken...")
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val user = result.user

            if (user != null) {
                println("✅ Usuário autenticado: ${user.email}")
                mapFirebaseUserToAuthResponse(user)
            } else {
                println("❌ User null após autenticação")
                AuthService.AuthResponse.Error("Falha ao obter usuário após autenticação social.")
            }
        } catch(e: Exception) {
            println("❌ Exceção no signInWithGoogleIdToken: ${e.message}")
            e.printStackTrace()
            mapFirebaseExceptionToError(e)
        }
    }
}