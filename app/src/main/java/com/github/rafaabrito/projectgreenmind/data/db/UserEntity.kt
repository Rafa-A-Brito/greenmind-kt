package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user" )
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnInfo(name="name") val name: String?,
    @ColumnInfo(name="email") val email: String?,
    @ColumnInfo(name="password") val password: String?,
)