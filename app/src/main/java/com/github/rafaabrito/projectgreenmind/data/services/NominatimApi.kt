package com.github.rafaabrito.projectgreenmind.data.services

import com.github.rafaabrito.projectgreenmind.data.model.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Esta interface é usada APENAS pelo Retrofit
interface NominatimApi {

    @GET("search")
    suspend fun searchAddress(
        @Query("q") address: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<NominatimResponse>

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): NominatimResponse
}