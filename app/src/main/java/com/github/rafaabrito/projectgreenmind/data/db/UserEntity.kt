package com.github.rafaabrito.projectgreenmind.data.db

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "user" )
data class UserEntity (
    @PrimaryKey(autogenerate = true) val  id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "password") val password: String?,
)
