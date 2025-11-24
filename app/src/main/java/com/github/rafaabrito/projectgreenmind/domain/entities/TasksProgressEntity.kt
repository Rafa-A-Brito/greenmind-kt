package com.github.rafaabrito.projectgreenmind.domain.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "tasks_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TasksEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["taskId"])
    ]
)
data class TasksProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "taskId") val taskId: Int,
    @ColumnInfo(name = "missionStatus") val missionStatus: String,
    @ColumnInfo(name = "currentProgress") val currentProgress: Float,
    @ColumnInfo(name = "startDate") val startDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "lastUpdated") val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "attemptsCount") val attemptsCount: Int = 0,
    @ColumnInfo(name = "finishingDate") val finishingDate: Long? = null
)
