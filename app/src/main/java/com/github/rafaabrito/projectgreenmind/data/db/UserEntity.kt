package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user" )
data class UserEntity (
    @PrimaryKey val  id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
)