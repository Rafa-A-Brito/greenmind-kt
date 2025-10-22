package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.rafaabrito.projectgreenmind.data.db.dao.UserDao

@Database(entities = [UserEntity::class], version = 1)
abstract class GreenMindDatabase: RoomDatabase(){

    abstract fun userDao(): UserDao
    //
}