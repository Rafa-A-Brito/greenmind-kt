package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_progress")
data class TasksProgressEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,

    )
