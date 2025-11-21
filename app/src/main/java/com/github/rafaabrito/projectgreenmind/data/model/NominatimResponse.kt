package com.github.rafaabrito.projectgreenmind.data.model

import com.google.gson.annotations.SerializedName

// Mapeia a resposta básica de geocoding do Nominatim
data class NominatimResponse(
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("display_name") val displayName: String
    // Outros campos como 'type', 'category' podem ser adicionados
) {
    // Função auxiliar para converter strings em Double
    fun toCoordinates(): Coordinates? {
        return try {
            Coordinates(latitude.toDouble(), longitude.toDouble())
        } catch (e: NumberFormatException) {
            null
        }
    }
}