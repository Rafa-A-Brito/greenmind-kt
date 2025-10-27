package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.ColumInfo
import androidx.room.PrimaryKey

@Entity(tableName = "credentials",
       foreignKeys = [ForeignKey(
        entity = UserEntity::class,  
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
       )]

data class CredentialsEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnIno(name="userId") val userId: Int,
    @ColumnIno(name="hashPassword") val hashPassword: String
)
