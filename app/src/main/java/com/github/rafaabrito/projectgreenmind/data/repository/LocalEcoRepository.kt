package com.github.rafaabrito.projectgreenmind.data.repository

import com.github.rafaabrito.projectgreenmind.domain.dao.LocalEcoDao
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.github.rafaabrito.projectgreenmind.data.services.OsmService
import com.github.rafaabrito.projectgreenmind.data.model.EcoDetails
import com.github.rafaabrito.projectgreenmind.data.model.Coordinates



class LocalEcoRepository @Inject constructor(
    private val localEcoDao: LocalEcoDao,
    private val osmService: OsmService
) {

    fun getAllLocalEco(): Flow<List<LocalEcoEntity>> {
        return localEcoDao.getAllLocalEco()
    }

    fun getLocalEcoById(id: Int): Flow<LocalEcoEntity> {
        return localEcoDao.getLocalEcoById(id)
    }

    suspend fun saveLocalEco(localEco: LocalEcoEntity) {
        localEcoDao.insert(localEco)
    }

    suspend fun deleteLocalEco(localEco: LocalEcoEntity) {
        localEcoDao.delete(localEco)
    }

    suspend fun geocodeAndSaveLocalEco(address: String, name: String): LocalEcoEntity? {
        val coordinates = osmService.getCoordinatesForAddress(address)

        val ecoDetails = coordinates?.let {
            osmService.getEcoDetailsForCoordinates(
                it.lat,
                it.long
            )
        } ?: getFallbackEcoDetails()

        val street = ecoDetails.street
        val numero = ecoDetails.numero
        val city = ecoDetails.city

        if (coordinates != null) {

            val newEntity: LocalEcoEntity? = if (street != null && numero != null && city != null) {
                LocalEcoEntity(
                    lat = coordinates.lat,
                    long = coordinates.long,
                    localName = name,
                    street = street,
                    numero = numero,
                    neighborhood = ecoDetails.neighborhood,
                    city = city,
                    cep = ecoDetails.cep,
                    distance = ecoDetails.distance,
                    recyclableTypes = ecoDetails.recyclableTypes
                )
            } else {
                null
            }
            newEntity?.let {
                localEcoDao.insert(it)
            }

            return newEntity
        }

        return null
    }
     private fun getFallbackEcoDetails(): EcoDetails {
        return EcoDetails(
            street = "Rua Desconhecida",
            numero = "S/N",
            neighborhood = "Não Identificado",
            city = "Cidade Padrão",
            cep = "00000-000",
            distance = "99 km",
            recyclableTypes = listOf("Não especificado")
        )
    }
}