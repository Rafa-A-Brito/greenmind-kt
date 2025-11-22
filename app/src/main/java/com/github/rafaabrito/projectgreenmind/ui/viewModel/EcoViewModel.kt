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

    // Estados
    private val _ecoPoints = MutableStateFlow<List<LocalEcoEntity>>(emptyList())
    val ecoPoints: StateFlow<List<LocalEcoEntity>> = _ecoPoints

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ✅ Novo: Ecoponto selecionado para ampliar no mapa
    private val _selectedEcoPoint = MutableStateFlow<LocalEcoEntity?>(null)
    val selectedEcoPoint: StateFlow<LocalEcoEntity?> = _selectedEcoPoint

    // ✅ Novo: Controla se deve mostrar o diálogo de navegação
    private val _showNavigationDialog = MutableStateFlow<LocalEcoEntity?>(null)
    val showNavigationDialog: StateFlow<LocalEcoEntity?> = _showNavigationDialog

    fun updateSearchTerm(newTerm: String) {
        _searchTerm.value = newTerm
    }

    // ✅ Selecionar ecoponto para ampliar mapa
    fun selectEcoPoint(ecoPoint: LocalEcoEntity) {
        _selectedEcoPoint.value = ecoPoint
    }

    // ✅ Limpar seleção
    fun clearSelectedEcoPoint() {
        _selectedEcoPoint.value = null
    }

    // ✅ Mostrar diálogo de navegação
    fun showNavigationDialog(ecoPoint: LocalEcoEntity) {
        _showNavigationDialog.value = ecoPoint
    }

    // ✅ Fechar diálogo de navegação
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
                // ✅ Busca em múltiplos campos
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

    // ✅ Função pública para buscar localização (chamada após permissão concedida)
    fun fetchUserLocation() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()

                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                if (location != null) {
                    val addressResult = getStreetAndCityFromCoords(location.latitude, location.longitude)

                    _userLocation.value = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        street = addressResult.street ?: "Rua Indisponível",
                        numero = addressResult.numero,
                        city = addressResult.city ?: "Cidade Indisponível"
                    )

                    // ✅ Calcular distâncias após obter localização
                    calculateDistances()
                } else {
                    setDefaultLocation("Localização falhou. Usando localização padrão.")
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Permissão negada: ${e.message}")
                setDefaultLocation("Permissão de localização negada.")
            } catch (e: Exception) {
                Log.e(TAG, "Erro: ${e.message}")
                setDefaultLocation("Erro ao obter localização: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Função auxiliar para localização padrão (Guarulhos)
    private fun setDefaultLocation(message: String) {
        _userLocation.value = null // Não define localização do usuário
        showToast(message)
    }

    // ✅ Calcular distância entre usuário e ecopontos
    private fun calculateDistances() {
        val userLoc = _userLocation.value ?: return

        viewModelScope.launch {
            val updatedPoints = _ecoPoints.value.map { ecoPoint ->
                val distance = calculateDistance(
                    userLoc.latitude, userLoc.longitude,
                    ecoPoint.lat, ecoPoint.long
                )
                ecoPoint.copy(distance = String.format("%.2f km", distance))
            }
            _ecoPoints.value = updatedPoints
        }
    }

    // ✅ Fórmula de Haversine para calcular distância
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private suspend fun getStreetAndCityFromCoords(lat: Double, lon: Double): AddressResult = withContext(
        Dispatchers.IO) {
        if (!Geocoder.isPresent()) {
            Log.e(TAG, "Geocoder indisponível no dispositivo.")
            return@withContext AddressResult("Serviço de Geocodificação indisponível", null, null)
        }

        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            val address = addresses?.firstOrNull()

            if (address != null) {
                AddressResult(
                    street = address.thoroughfare,
                    numero = address.subThoroughfare,
                    city = address.locality ?: address.adminArea
                )
            } else {
                AddressResult("Endereço não encontrado", null, null)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Falha no Geocoder (IO): ${e.message}")
            AddressResult("Erro de rede/serviço ao buscar endereço", null, null)
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
                        lat = -23.46883, long = -46.53913,
                        localName = "Ecoponto Gopoúva",
                        street = "Rua Guarulhos",
                        numero = "34",
                        neighborhood = "Gopoúva",
                        city = "Guarulhos",
                        cep = "07020-201",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Móveis", "Poda", "Vidro", "Metal", "Plástico", "Papelão")
                    ),
                    LocalEcoEntity(
                        lat = -23.45495, long = -46.52576,
                        localName = "Ecoponto Paraventi",
                        street = "Rua Apolônia Vieira de Jesus",
                        numero = "91",
                        neighborhood = "Paraventi",
                        city = "Guarulhos",
                        cep = "07120-060",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Madeira", "Poda", "Vidro", "Metal", "Plástico", "Papel")
                    ),
                    LocalEcoEntity(
                        lat = -23.41066, long = -46.37419,
                        localName = "Ecoponto Jardim Álamo",
                        street = "Rua Gentil da Silva Leite Filho",
                        numero = "15",
                        neighborhood = "Jardim Álamo",
                        city = "Guarulhos",
                        cep = "07176-680",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Móveis", "Poda", "Madeira", "Óleo", "Eletrodomésticos", "Gesso")
                    ),
                    LocalEcoEntity(
                        lat = -23.40948, long = -46.45981,
                        localName = "Ecoponto Santos Dumont",
                        street = "Estrada do Saboó",
                        numero = "795",
                        neighborhood = "Santos Dumont",
                        city = "Guarulhos",
                        cep = "07152-000",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Móveis", "Poda", "Eletrônicos", "Pneus", "Recicláveis")
                    ),
                    LocalEcoEntity(
                        lat = -23.41066, long = -46.53330,
                        localName = "Ecoponto Presidente Dutra",
                        street = "Avenida João Bassi",
                        numero = "707",
                        neighborhood = "Jardim Presidente Dutra",
                        city = "Guarulhos",
                        cep = "07171-137",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Madeira", "Poda", "Móveis", "Eletrodomésticos", "Recicláveis")
                    ),
                    LocalEcoEntity(
                        lat = -23.43719, long = -46.40997,
                        localName = "Ecoponto Pimentas",
                        street = "Rua Itália",
                        numero = "13",
                        neighborhood = "Parque das Nações",
                        city = "Guarulhos",
                        cep = "07243-313",
                        distance = "indisponível",
                        recyclableTypes = listOf("Entulho", "Poda", "Madeira", "Móveis", "Eletrodomésticos", "Papel", "Vidro", "Metal", "Plástico")
                    )
                )
                mockEcopoints.forEach {
                    localEcoRepository.saveLocalEco(it)
                }
            }
        }
    }
}