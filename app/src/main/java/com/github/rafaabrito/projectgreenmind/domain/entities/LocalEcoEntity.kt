package com.github.rafaabrito.projectgreenmind.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localEco")
data class LocalEcoEntity (
    @PrimaryKey(autoGenerate = true) val  id: Int = 0,
    @ColumnInfo(name="latitude") val lat: Double,
    @ColumnInfo(name="longitude") val long: Double,
    @ColumnInfo(name="localName") val localName: String,
    @ColumnInfo(name = "street") val street: String,
    @ColumnInfo(name = "number") val numero: String,
    @ColumnInfo(name = "neighborhood") val neighborhood: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "cep") val cep: String,
    @ColumnInfo(name = "distance") val distance: String,
    @ColumnInfo(name = "recyclableTypes") val recyclableTypes: List<String>

)