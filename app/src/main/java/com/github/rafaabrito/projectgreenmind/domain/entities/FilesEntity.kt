package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FilesEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnInfo(name="imageName") val imageName : String,
    @ColumnInfo(name="idBanner") val idBanner : Int,

    )
