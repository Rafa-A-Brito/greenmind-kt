package com.github.rafaabrito.projectgreenmind.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FilesEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    )
