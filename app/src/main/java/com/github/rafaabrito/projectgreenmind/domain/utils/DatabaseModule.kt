package com.github.rafaabrito.projectgreenmind.domain.utils

import android.content.Context
import androidx.room.Room
import com.github.rafaabrito.projectgreenmind.data.db.GreenMindDatabase
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.dao.FilesDao
import com.github.rafaabrito.projectgreenmind.domain.dao.LocalEcoDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreProgressDao
import com.github.rafaabrito.projectgreenmind.domain.dao.SustentabilityBannerDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksProgressDao
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

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GreenMindDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GreenMindDatabase::class.java,
            "greenmind_db"
        ).build()
    }

    // Provedores de DAO para injeção
    @Provides
    fun provideUserDao(database: GreenMindDatabase): UserDao = database.userDao()

    @Provides
    fun provideCredentialsDao(database: GreenMindDatabase): CredentialsDao = database.credentialsDao()

    @Provides
    fun provideFilesDao(database: GreenMindDatabase): FilesDao = database.filesDao()

    @Provides
    fun provideLocalEcoDao(database: GreenMindDatabase): LocalEcoDao = database.localEcoDao()

    @Provides
    fun provideScoreDao(database: GreenMindDatabase): ScoreDao = database.scoreDao()

    @Provides
    fun provideScoreProgressDao(database: GreenMindDatabase): ScoreProgressDao = database.scoreProgressDao()

    @Provides
    fun provideSustentabilityBannerDao(database: GreenMindDatabase): SustentabilityBannerDao = database.sustentabilityBannerDao()

    @Provides
    fun provideTasksDao(database: GreenMindDatabase): TasksDao = database.tasksDao()

    @Provides
    fun provideTasksProgressDao(database: GreenMindDatabase): TasksProgressDao = database.tasksProgressDao()
}