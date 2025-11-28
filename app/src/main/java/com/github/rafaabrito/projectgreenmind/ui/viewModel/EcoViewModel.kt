package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.repository.LocalEcoRepository
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val street: String? = null,
    val numero: String? = null,
    val city: String? = null
)

data class AddressResult(
    val street: String?,
    val numero: String?,
    val city: String?
)

@HiltViewModel
class EcoViewModel @Inject constructor(
    private val localEcoRepository: LocalEcoRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "EcoViewModel"

    private val _ecoPoints = MutableStateFlow<List<LocalEcoEntity>>(emptyList())
    val ecoPoints: StateFlow<List<LocalEcoEntity>> = _ecoPoints

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedEcoPoint = MutableStateFlow<LocalEcoEntity?>(null)
    val selectedEcoPoint: StateFlow<LocalEcoEntity?> = _selectedEcoPoint

    private val _showNavigationDialog = MutableStateFlow<LocalEcoEntity?>(null)
    val showNavigationDialog: StateFlow<LocalEcoEntity?> = _showNavigationDialog

    fun updateSearchTerm(newTerm: String) {
        _searchTerm.value = newTerm
    }

    fun selectEcoPoint(ecoPoint: LocalEcoEntity) {
        _selectedEcoPoint.value = ecoPoint
    }

    fun clearSelectedEcoPoint() {
        _selectedEcoPoint.value = null
    }

    fun showNavigationDialog(ecoPoint: LocalEcoEntity) {
        _showNavigationDialog.value = ecoPoint
    }

    fun dismissNavigationDialog() {
        _showNavigationDialog.value = null
    }

    val filteredEcoPoints: StateFlow<List<LocalEcoEntity>> = combine(
        _ecoPoints,
        _searchTerm
    ) { points, term ->
        if (term.isBlank()) {
            points
        } else {
            points.filter { ecopoint ->
                ecopoint.localName.contains(term, ignoreCase = true) ||
                        ecopoint.street.contains(term, ignoreCase = true) ||
                        ecopoint.neighborhood.contains(term, ignoreCase = true) ||
                        ecopoint.city.contains(term, ignoreCase = true) ||
                        ecopoint.cep.contains(term, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            localEcoRepository.getAllLocalEco().collect {
                _ecoPoints.value = it
                _isLoading.value = false
            }
        }
        insertMockData()
    }

    //  Função pública para buscar localização com retry
    fun fetchUserLocation() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()

                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                    Log.d(TAG, "Localização obtida: ${location.latitude}, ${location.longitude}")

                    val addressResult = getStreetAndCityFromCoords(location.latitude, location.longitude)

                    _userLocation.value = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        street = addressResult.street ?: "Endereço não disponível",
                        numero = addressResult.numero,
                        city = addressResult.city ?: "Cidade não disponível"
                    )

                    // Calcular distâncias após obter localização
                    calculateDistances()
                } else {
                    Log.w(TAG, "Localização nula ou inválida")
                    setDefaultLocation("Não foi possível obter sua localização. Mostrando ecopontos de Guarulhos.")
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Permissão negada: ${e.message}")
                setDefaultLocation("Permissão de localização negada.")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter localização: ${e.message}")
                setDefaultLocation("Erro ao obter localização. Mostrando ecopontos de Guarulhos.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun setDefaultLocation(message: String) {
        _userLocation.value = null
        showToast(message)
    }

    // Calcular distância com fórmula de Haversine (linha reta)
    private fun calculateDistances() {
        val userLoc = _userLocation.value ?: return

        viewModelScope.launch {
            val updatedPoints = _ecoPoints.value.map { ecoPoint ->
                val distance = calculateHaversineDistance(
                    userLoc.latitude, userLoc.longitude,
                    ecoPoint.lat, ecoPoint.long
                )

                // ✅ Estimativa de distância real (30% a mais que linha reta)
                val estimatedDistance = distance * 1.3

                val distanceText = when {
                    estimatedDistance < 1.0 -> "~${(estimatedDistance * 1000).toInt()} m"
                    else -> "~${"%.1f".format(estimatedDistance)} km"
                }

                ecoPoint.copy(distance = distanceText)
            }
            _ecoPoints.value = updatedPoints
        }
    }

    // Fórmula de Haversine para calcular distância em linha reta
    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Raio da Terra em km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private suspend fun getStreetAndCityFromCoords(lat: Double, lon: Double): AddressResult = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) {
            Log.e(TAG, "Geocoder indisponível no dispositivo.")
            return@withContext AddressResult("Serviço de Geocodificação indisponível", null, null)
        }

        try {
            val geocoder = Geocoder(context, Locale("pt", "BR"))

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            val address = addresses?.firstOrNull()

            if (address != null) {
                AddressResult(
                    street = address.thoroughfare,
                    numero = address.subThoroughfare,
                    city = address.locality ?: address.subAdminArea ?: address.adminArea
                )
            } else {
                AddressResult("Endereço não encontrado", null, null)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Falha no Geocoder (IO): ${e.message}")
            AddressResult("Erro de rede ao buscar endereço", null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado no Geocoder: ${e.message}")
            AddressResult("Erro ao processar endereço", null, null)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun insertMockData() {
        _isLoading.value = true
        viewModelScope.launch {
            if (localEcoRepository.getAllLocalEco().first().isEmpty()) {
                val mockEcopoints = listOf(
                    LocalEcoEntity(
                        lat = -23.469250238622532, long = -46.539038265135225,  // -23.469250238622532, -46.539038265135225
                        localName = "Ecoponto Gopoúva",
                        street = "Rua Guarulhos",
                        numero = "34",
                        neighborhood = "Gopoúva",
                        city = "Guarulhos",
                        cep = "07020-201",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat = -23.458095788726766, long = -46.52330283012511, //-23.458095788726766, -46.52330283012511
                        localName = "Ecoponto Paraventi",
                        street = "Rua Apolônia Vieira de Jesus",
                        numero = "91",
                        neighborhood = "Paraventi",
                        city = "Guarulhos",
                        cep = "07120-060",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat = -23.41066, long = -46.37419, //
                        localName = "Ecoponto Jardim Álamo",
                        street = "Rua Gentil da Silva Leite Filho",
                        numero = "15",
                        neighborhood = "Jardim Álamo",
                        city = "Guarulhos",
                        cep = "07176-680",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat = -23.406365750210636, long =  -46.46207049761836, // -23.406365750210636, -46.46207049761836
                        localName = "Ecoponto Santos Dumont",
                        street = "Estrada do Saboó",
                        numero = "795",
                        neighborhood = "Santos Dumont",
                        city = "Guarulhos",
                        cep = "07152-000",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat = -23.414416825252275, long =  -46.42805238385875, // -23.414416825252275, -46.42805238385875
                        localName = "Ecoponto Presidente Dutra",
                        street = "Avenida João Bassi",
                        numero = "707",
                        neighborhood = "Jardim Presidente Dutra",
                        city = "Guarulhos",
                        cep = "07171-137",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat = -23.43601591907332, long = -46.409574761426995, // -23.43601591907332, -46.409574761426995
                        localName = "Ecoponto Pimentas",
                        street = "Rua Itália",
                        numero = "13",
                        neighborhood = "Parque das Nações",
                        city = "Guarulhos",
                        cep = "07243-313",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    ),
                    LocalEcoEntity(
                        lat =  -23.419675954409175, long =  -46.52049088978411, // -23.419675954409175, -46.52049088978411
                        localName = "Ecoponto Jardim Adriana",
                        street = " Rua Valter Pereira de Lima",
                        numero = "105",
                        neighborhood = "Vila Sítio dos Morros",
                        city = "Guarulhos",
                        cep = " 07135-210",
                        distance = "",
                        recyclableTypes = listOf("Aceita resíduos conforme os materiais listados nos ícones acima do mapa")
                    )
                )
                mockEcopoints.forEach {
                    localEcoRepository.saveLocalEco(it)
                }
            }
        }
    }
}