package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import kotlin.collections.emptyList

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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "EcoViewModel"

    // Estado da lista de ecopontos no Room
    private val _ecoPoints = MutableStateFlow<List<LocalEcoEntity>>(emptyList())
    val ecoPoints: StateFlow<List<LocalEcoEntity>> = _ecoPoints

    // Estado da localização atual do usuário
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation
    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm
    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun updateSearchTerm(newTerm: String) {
        _searchTerm.value = newTerm
    }

    val filteredEcoPoints: StateFlow<List<LocalEcoEntity>> = combine(
        _ecoPoints,
        _searchTerm
    ) { points, term ->
        if (term.isBlank()) {
            points
        } else {
            points.filter {
                it.localName.contains(term, ignoreCase = true)
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
        fetchUserLocation()
        insertMockData()
    }

    private fun getUserLocation() {
        _isLoading.value = true

        // CUIDADO: É necessário verificar permissões ANTES de chamar esta função
        // No escopo deste exemplo, assumimos que as permissões foram concedidas.
        if (false /* Lógica real de checagem de permissão: ContextCompat.checkSelfPermission(...) != PackageManager.PERMISSION_GRANTED */) {
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
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
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Permissão de Localização negada: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter localização: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    private fun fetchUserLocation() {
        _isLoading.value = true
        // NOTA: A verificação de permissão deve ser feita antes.

        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()

                // 1. Solicita a localização atual (Lat/Long)
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                if (location != null) {
                    // 2. Chama a função de Geocoding para obter o endereço
                    val addressResult = getStreetAndCityFromCoords(location.latitude, location.longitude)

                    _userLocation.value = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        street = addressResult.street ?: "Rua Indisponível",
                        numero = addressResult.numero,
                        city = addressResult.city ?: "Cidade Indisponível"
                    )
                } else {
                    // Fallback para localização e endereço padrão
                    _userLocation.value = UserLocation(
                        latitude = -23.5505,
                        longitude = -46.6333,
                        street = "Localização padrão (Av. Paulista)",
                        numero = "1374",
                        city = "São Paulo"
                    )
                    showToast("Localização falhou. Usando localização padrão.")
                }
            } catch (e: SecurityException) {
                // Fallback para permissão negada
                _userLocation.value = UserLocation(
                    latitude = -23.5505,
                    longitude = -46.6333,
                    street = "Permissão negada (Av. Paulista)",
                    numero = "1374",
                    city = "São Paulo"
                )
                showToast("Permissão de localização negada. Usando localização padrão.")
            } catch (e: Exception) {
                // Tratar outros erros de localização
                _userLocation.value = UserLocation(
                    latitude = -23.5505,
                    longitude = -46.6333,
                    street = "Erro ao buscar (Av. Paulista)",
                    numero = "1374",
                    city = "São Paulo"
                )
                showToast("Erro ao obter localização: ${e.message}. Usando localização padrão.")
            } finally {
                _isLoading.value = false
            }
        }
    }
    private suspend fun getStreetAndCityFromCoords(lat: Double, lon: Double): AddressResult = withContext(
        Dispatchers.IO) {
        if (!Geocoder.isPresent()) {
            Log.e(TAG, "Geocoder indisponível no dispositivo.")
            return@withContext AddressResult("Serviço de Geocodificação indisponível", null, null)
        }

        try {
            // Usando a localização padrão do sistema
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
            // Captura erros de rede ou serviço
            Log.e(TAG, "Falha no Geocoder (IO): ${e.message}")
            AddressResult("Erro de rede/serviço ao buscar endereço", null, null)
        } catch (e: Exception) {
            // Captura outros erros
            Log.e(TAG, "Erro inesperado no Geocoder: ${e.message}")
            AddressResult("Erro ao processar endereço", null, null)
        }
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