package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sustentabilityBanner")
data class SustentabilityBanner(
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnInfo(name="titleBanner") val titleBanner : String,
    @ColumnInfo(name="contentBanner") val contentBanner : String,
    @ColumnInfo(name="statusBanner") val statusBanner : Boolean,
    @ColumnInfo(name="linkActionBanner") val linkActionBanner : String,
    @ColumnInfo(name="idFile") val idFile : Int,
    )
