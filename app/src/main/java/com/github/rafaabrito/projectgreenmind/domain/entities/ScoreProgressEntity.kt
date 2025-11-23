package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scoreProgress",
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
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["taskId"])
    ]
)
data class ScoreProgressEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    @ColumnInfo(name="userId") val userId: Int,
    @ColumnInfo(name="taskId") val taskId: Int,
    @ColumnInfo(name="scoreEarned") val scoreEarned: Int, // Pontuação ganha por completar esta tarefa
    @ColumnInfo(name="isCompleted") val isCompleted: Boolean = false, // Status de conclusão da tarefa
    @ColumnInfo(name = "dateEarned") val dateEarned: Long = System.currentTimeMillis()

)
