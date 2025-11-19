// GoogleOneTapModule.kt

package com.github.rafaabrito.projectgreenmind.domain.utils.auth

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleOneTapModule {

    @Provides
    @Singleton
    fun provideOneTapClient(@ApplicationContext context: Context): SignInClient {
        // Identity.getSignInClient retorna a instância OneTapClient
        return Identity.getSignInClient(context)
    }
}