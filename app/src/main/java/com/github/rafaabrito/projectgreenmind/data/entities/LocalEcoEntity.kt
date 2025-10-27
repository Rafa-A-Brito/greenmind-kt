package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_eco")
data class LocalEcoEntity (
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    val lat: Double,
    val long: Double,
    val local_name: String
)