package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.rafaabrito.projectgreenmind.data.db.dao.UserDao
import android.content.Context

@Database(entities = [UserEntity::class], version = 1)
abstract class GreenMindDatabase: RoomDatabase(){

    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile private var INSTANCE: GreenMindDatabase?=null

        fun getDatabase(context: Context):GreenMindDatabase{
            return INSTANCE?: synchronized(this){
                Room.databaseBuilder(
                    content.ApplicationContext,
                    GreenMindDatabase::class.java,
                    "greenmind_db
                ).build
                .also{ INSTANCE = it}
            }
        }
    }
}