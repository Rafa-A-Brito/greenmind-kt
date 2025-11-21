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

        if (coordinates != null) {

            val ecoDetails = osmService.getEcoDetailsForCoordinates(
                coordinates.lat,
                coordinates.long
            ) ?: getFallbackEcoDetails()

            val newEntity = LocalEcoEntity(
                lat = coordinates.lat,
                long = coordinates.long,
                localName = name,
                street = ecoDetails.street,
                numero = ecoDetails.numero,
                neighborhood = ecoDetails.neighborhood,
                city = ecoDetails.city,
                cep = ecoDetails.cep,
                distance = ecoDetails.distance,
                recyclableTypes = ecoDetails.recyclableTypes
            )

            localEcoDao.insert(newEntity)
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