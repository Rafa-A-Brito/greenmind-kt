package com.github.rafaabrito.projectgreenmind.domain.utils

import android.content.Context
import androidx.room.Room
import com.github.rafaabrito.projectgreenmind.data.db.GreenMindDatabase
import com.github.rafaabrito.projectgreenmind.data.repository.StreakRepository
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.dao.FilesDao
import com.github.rafaabrito.projectgreenmind.domain.dao.LocalEcoDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreProgressDao
import com.github.rafaabrito.projectgreenmind.domain.dao.StreakDao
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
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    // Provedores de DAO para injeção
    @Provides
    fun provideStreakDao(database: GreenMindDatabase): StreakDao {
        return database.streakDao()
    }
    @Provides
    fun provideUserDao(database: GreenMindDatabase): UserDao { return database.userDao()}

    @Provides
    fun provideCredentialsDao(database: GreenMindDatabase): CredentialsDao { return database.credentialsDao() }

    @Provides
    fun provideFilesDao(database: GreenMindDatabase): FilesDao { return database.filesDao()}

    @Provides
    fun provideLocalEcoDao(database: GreenMindDatabase): LocalEcoDao { return database.localEcoDao()}

    @Provides
    fun provideScoreDao(database: GreenMindDatabase): ScoreDao {return database.scoreDao()}

    @Provides
    fun provideScoreProgressDao(database: GreenMindDatabase): ScoreProgressDao {return database.scoreProgressDao()}

    @Provides
    fun provideSustentabilityBannerDao(database: GreenMindDatabase): SustentabilityBannerDao{return database.sustentabilityBannerDao()}
    @Provides
    fun provideTasksDao(database: GreenMindDatabase): TasksDao {return database.tasksDao()}

    @Provides
    fun provideTasksProgressDao(database: GreenMindDatabase): TasksProgressDao {return database.tasksProgressDao()}
    @Provides
    fun provideStreakRepository(
        streakDao: StreakDao
    ): StreakRepository = StreakRepository(streakDao)

}