package com.github.rafaabrito.projectgreenmind.domain.utils

import com.github.rafaabrito.projectgreenmind.domain.utils.AuthService
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {

    private fun mapFirebaseUserToAuthResult(user: com.google.firebase.auth.FirebaseUser, isNewUser: Boolean): AuthService.AuthResult {
        return AuthService.AuthResult(
            authId = user.uid,
            email = user.email ?: "",
            name = user.displayName,
            isNewUser = isNewUser
        )
    }
    override suspend fun authenticateWithFacebookToken(accessToken: String): AuthService.AuthResult? {
        return try {
            // Cria a credencial do Firebase a partir do token de acesso do Facebook
            val credential = FacebookAuthProvider.getCredential(accessToken)

            // Faz o login no Firebase com a credencial
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                mapFirebaseUserToAuthResult(user, isNewUser = result.additionalUserInfo?.isNewUser ?: false)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthService.AuthResult? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // No login, assumimos que o usuário não é novo.
                mapFirebaseUserToAuthResult(user, isNewUser = false)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String?): AuthService.AuthResult? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                if (name != null) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }

                // No registro, o usuário é sempre novo.
                mapFirebaseUserToAuthResult(user, isNewUser = true)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun authenticateWithGoogleToken(idToken: String): AuthService.AuthResult? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                // Aqui você pode adicionar lógica para verificar se o usuário é novo
                // (usando o resultado da tarefa ou comparando com um banco de dados).
                mapFirebaseUserToAuthResult(user, isNewUser = result.additionalUserInfo?.isNewUser ?: false)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // --- 3. INÍCIO DO FLUXO SOCIAL ---
    // Esta função NÃO realiza a autenticação completa, pois a autenticação social
    // requer o uso de uma Activity (MainActivity) para obter o resultado do Google/Facebook.
    // Ela deve apenas disparar um evento que será capturado pela sua LoginScreen/Activity.
    override fun startSocialSignIn(provider: AuthService.SocialProvider) {
        // Como o fluxo social é complexo (requer Activity Result/Credential Manager),
        // esta implementação apenas notifica o ViewModel/Activity de que o fluxo deve começar.

        // No seu projeto, a LoginScreen chama onGoogleSignIn/onFacebookSignIn,
        // que são passados para a MainActivity.
        // Portanto, aqui, podemos apenas lançar um log ou manter a função vazia,
        // dependendo de como você integra o AuthService com o LoginViewModel.

        // Se você não usa este método no ViewModel, ele pode ficar vazio:
        println("Iniciando fluxo social para o provedor: $provider")

        // Se você precisar de um fluxo de evento mais robusto, o ViewModel deve
        // expor um evento de Ação para a Activity, mas o padrão atual (passar o
        // callback da Activity) já resolve isso.
    }
}