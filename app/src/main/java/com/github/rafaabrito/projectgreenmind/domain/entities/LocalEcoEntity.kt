package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localEco")
data class LocalEcoEntity (
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    val lat: Double,
    val long: Double,
    val localName: String
)