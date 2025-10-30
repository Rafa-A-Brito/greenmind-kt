package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.github.rafaabrito.projectgreenmind.data.db.UserEntity

@Entity(
    tableName = "scoreProgressEntity",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = TasksEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class ScoreProgressEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    @ColumnInfo(name="userId") val userId: Int,
    @ColumnInfo(name="taskId") val taskId: String
)
