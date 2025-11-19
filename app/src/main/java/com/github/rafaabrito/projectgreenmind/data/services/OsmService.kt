package com.github.rafaabrito.projectgreenmind.data.services

import com.github.rafaabrito.projectgreenmind.data.model.Coordinates
import com.github.rafaabrito.projectgreenmind.data.model.EcoDetails
import com.github.rafaabrito.projectgreenmind.data.model.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Query
// Interface de serviço para injeção
interface OsmService {
    // Converte endereço em Latitude e Longitude
    suspend fun getCoordinatesForAddress(address: String): Coordinates?

    // Converte Lat/Long em detalhes do local
    suspend fun getEcoDetailsForCoordinates(lat: Double, long: Double): EcoDetails?
}