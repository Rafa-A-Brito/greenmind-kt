package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,

    )
