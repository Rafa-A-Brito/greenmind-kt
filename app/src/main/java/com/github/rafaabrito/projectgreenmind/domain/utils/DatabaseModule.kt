package com.github.rafaabrito.projectgreenmind.domain.utils

import android.content.Context
import androidx.room.Room
import com.github.rafaabrito.projectgreenmind.data.db.GreenMindDatabase
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "greenmind_db"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GreenMindDatabase {
        return Room.databaseBuilder(
            context,
            GreenMindDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideUserDao(
        database: GreenMindDatabase
    ): UserDao {
        // Assume que o método em AppDatabase é userDao()
        return database.userDao()
    }

    @Provides
    fun provideCredentialsDao(
        database: GreenMindDatabase
    ): CredentialsDao {
        // Assume que o método na sua AppDatabase é credentialsDao()
        return database.credentialsDao()
    }
}