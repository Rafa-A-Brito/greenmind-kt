package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TasksEntity(
    @PrimaryKey(autoGenerate = true) val  taskId: Int = 0,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="missionTitle") val missionTitle: String,
    @ColumnInfo(name="finalPercentual") val finalPercentual: Float,
    @ColumnInfo(name="missionType") val missionType: String,
    @ColumnInfo(name="rewardsPoints") val rewardPoints: Int,

    )
