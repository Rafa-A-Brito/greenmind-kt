package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class CredentialsEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
)
