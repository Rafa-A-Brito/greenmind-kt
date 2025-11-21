package com.github.rafaabrito.projectgreenmind.data.services

import com.github.rafaabrito.projectgreenmind.data.model.Coordinates
import com.github.rafaabrito.projectgreenmind.data.model.EcoDetails
import com.github.rafaabrito.projectgreenmind.data.model.NominatimResponse
import javax.inject.Inject

class RealOsmService @Inject constructor(
    private val api: NominatimApi
) : OsmService{

    // Geocoding - Localização real
    override suspend fun getCoordinatesForAddress(address: String): Coordinates? {
        return try {
            val response = api.searchAddress(address = address)
            response.firstOrNull()?.toCoordinates()
        } catch (e: Exception) {
            null
        }
    }

    // Implemnetação mockada
    override suspend fun getEcoDetailsForCoordinates(lat: Double, long: Double): EcoDetails? {
        // ... (Lógica mockada ou real para buscar detalhes específicos)
        return EcoDetails(
            street = "Rua Gerada pela API",
            numero = "100",
            neighborhood = "Centro API",
            city = "São Paulo",
            cep = "01000-000",
            distance = "0.7 km (API)",
            recyclableTypes = listOf("Vidro", "Papel", "Orgânico")
        )
    }
}