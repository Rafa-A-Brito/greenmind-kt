package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey
    val userId: Int,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCheckInDate: String = "", // Formato: "yyyy-MM-dd"
    val totalCheckIns: Int = 0
)