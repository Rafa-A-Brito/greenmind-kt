package com.github.rafaabrito.projectgreenmind.domain.utils

import com.github.rafaabrito.projectgreenmind.domain.utils.AuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Singleton
    @Binds
    abstract fun bindAuthService(
        authServiceImpl: FirebaseAuthServiceImpl
    ): AuthService
}