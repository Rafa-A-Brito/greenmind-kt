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
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["missionId"])
    ]
)
data class TasksProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "missionId") val missionId: String,

    // Campos Originais
    @ColumnInfo(name = "missionStatus") val missionStatus: String, // Ex: "PENDING", "IN_PROGRESS", "COMPLETED"
    @ColumnInfo(name = "currentProgress") val currentProgress: Int, // Ex: 0 a 100

    // Campos Adicionais Sugeridos
    @ColumnInfo(name = "startDate") val startDate: Long, // Timestamp de início
    @ColumnInfo(name = "lastUpdated") val lastUpdated: Long, // Timestamp da última alteração
    @ColumnInfo(name = "attemptsCount") val attemptsCount: Int = 0,

    @ColumnInfo(name = "finishingDate") val finishingDate: Long? = null, // Melhor usar Long e permitir Nullable
)
