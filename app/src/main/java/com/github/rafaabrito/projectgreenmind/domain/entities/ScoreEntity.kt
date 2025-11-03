package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "score", foreignKeys = [ForeignKey(
    entity = UserEntity::class,
    parentColumns = ["id"],
    childColumns = ["userId"],
    onDelete = CASCADE,
    onUpdate = CASCADE
)])
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnInfo(name = "totalScore") val  totalScore: Int = 0,
    @ColumnInfo(name = "scoreLevel") val  scoreLevel: Int = 0,
    @ColumnInfo(name = "missionScore") val  missionScore: Int = 0,
    @ColumnInfo(name = "userId") val  userId: Int = 0,
    )
