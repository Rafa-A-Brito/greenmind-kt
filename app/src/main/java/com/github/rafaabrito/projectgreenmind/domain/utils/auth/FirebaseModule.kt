package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Instala o módulo para que as dependências vivam enquanto a aplicação estiver rodando
object FirebaseModule {
    @Provides
    @Singleton // Garante que apenas uma única instância será criada
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}