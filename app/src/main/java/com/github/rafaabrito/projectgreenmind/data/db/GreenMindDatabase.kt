package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import com.github.rafaabrito.projectgreenmind.domain.dao.UserDao
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.dao.FilesDao
import com.github.rafaabrito.projectgreenmind.domain.dao.LocalEcoDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreDao
import com.github.rafaabrito.projectgreenmind.domain.dao.ScoreProgressDao
import com.github.rafaabrito.projectgreenmind.domain.dao.SustentabilityBannerDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksDao
import com.github.rafaabrito.projectgreenmind.domain.dao.TasksProgressDao
import com.github.rafaabrito.projectgreenmind.domain.entities.CredentialsEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.FilesEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreProgressEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.SustentabilityBanner
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.TasksProgressEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity

@Database(entities = [
    CredentialsEntity::class,
    FilesEntity::class,
    LocalEcoEntity::class,
    ScoreEntity::class,
    ScoreProgressEntity::class,
    SustentabilityBanner::class,
    TasksEntity::class,
    TasksProgressEntity::class,
    UserEntity::class
], version = 1)
abstract class GreenMindDatabase: RoomDatabase(){

    abstract fun userDao(): UserDao
    abstract fun credentialsDao(): CredentialsDao
    abstract fun filesDao(): FilesDao
    abstract fun localEcoDao(): LocalEcoDao
    abstract fun scoreDao(): ScoreDao
    abstract fun scoreProgressDao(): ScoreProgressDao
    abstract fun sustentabilityBannerDao(): SustentabilityBannerDao
    abstract fun tasksDao(): TasksDao
    abstract fun tasksProgressDao(): TasksProgressDao
    
    companion object {
        @Volatile private var INSTANCE: GreenMindDatabase?=null

        fun getDatabase(context: Context):GreenMindDatabase{
            return INSTANCE?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    GreenMindDatabase::class.java,
                    "greenmind_db"
                ).build()
                .also{ INSTANCE = it}
            }
        }
    }
}