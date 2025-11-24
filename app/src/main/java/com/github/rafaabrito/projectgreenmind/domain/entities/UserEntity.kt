package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user",
       indices = [Index(value = ["email"], unique = true),
       Index(value = ["firebaseUid"], unique = true)]) // Garante que Firebase UID único])
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val  userId: Int = 0,
    @ColumnInfo(name="name") val name: String?,
    @ColumnInfo(name="email") val email: String,
    @ColumnInfo(name = "firebaseUid") val firebaseUid: String?,
    @ColumnInfo(name="hashPassword") val hashPassword: String?,
)
