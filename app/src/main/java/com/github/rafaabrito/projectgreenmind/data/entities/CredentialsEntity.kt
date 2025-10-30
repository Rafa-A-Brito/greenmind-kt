package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "credentials", foreignKeys = [ForeignKey(
        entity = UserEntity::class,  
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
       )])
data class CredentialsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name="userId") val userId: Int,
    @ColumnInfo(name="authId") val authId: String?
)