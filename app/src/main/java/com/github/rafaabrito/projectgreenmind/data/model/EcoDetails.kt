package com.github.rafaabrito.projectgreenmind.data.model

data class EcoDetails(
    val street: String,
    val numero: String,
    val neighborhood: String,
    val city: String,
    val cep: String,
    val distance: String,
    val recyclableTypes: List<String>
)