package com.github.rafaabrito.projectgreenmind.di // Pacote de módulos DI

import com.github.rafaabrito.projectgreenmind.data.security.BCryptPasswordHasher
import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    // 🔑 @Binds: Vincula a interface PasswordHasher à sua implementação concreta
    @Singleton
    @Binds
    abstract fun bindPasswordHasher(
        hasher: BCryptPasswordHasher
    ): PasswordHasher
}