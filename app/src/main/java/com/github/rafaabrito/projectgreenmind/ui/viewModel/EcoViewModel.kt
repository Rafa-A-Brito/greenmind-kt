package com.github.rafaabrito.projectgreenmind.ui.viewModel

import android.content.Context
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
data class UserLocation(val latitude: Double, val longitude: Double)

@HiltViewModel
class EcoViewModel @Inject constructor(
    private val localEcoRepository: LocalEcoRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Estado da lista de ecopontos no Room
    private val _ecoPoints = MutableStateFlow<List<LocalEcoEntity>>(emptyList())
    val ecoPoints: StateFlow<List<LocalEcoEntity>> = _ecoPoints

    // Estado da localização atual do usuário
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Inicia a observação dos ecopontos do Room
        viewModelScope.launch {
            localEcoRepository.getAllLocalEco().collect {
                _ecoPoints.value = it
                _isLoading.value = false
            }
        }
        fetchUserLocation()
        insertMockData()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    private fun fetchUserLocation() {
        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()

                // Solicita a localização atual
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                if (location != null) {
                    _userLocation.value = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                } else {
                    // Aqui mantemos o mock como fallback, ou definimos um estado de erro
                    _userLocation.value = UserLocation(-23.5505, -46.6333)
                    showToast("Localização falhou. Usando localização padrão.")
                }
            } catch (e: SecurityException) {
                // Exemplo de fallback:
                _userLocation.value = UserLocation(-23.5505, -46.6333)
                showToast("Permissão de localização negada. Usando localização padrão.")
            } catch (e: Exception) {
                // Tratar outros erros de localização
                _userLocation.value = UserLocation(-23.5505, -46.6333)
                showToast("Erro ao obter localização: ${e.message}. Usando localização padrão.")            }
        }
    }

    private fun insertMockData() {
        _isLoading.value = true
        viewModelScope.launch {
            if (localEcoRepository.getAllLocalEco().first().isEmpty()) {
                val mockEcopoints = listOf(
                    LocalEcoEntity(
                        lat = -23.56138, long = -46.65651,
                        localName = "Ecoponto Centro", street = "Rua da Consolação",
                        numero = "1000", neighborhood = "Consolação",
                        city = "São Paulo", cep = "01301-000",
                        distance = "2.1 km", // NOVO CAMPO
                        recyclableTypes = listOf("Papel", "Metal")
                    ),
                    LocalEcoEntity(
                        lat = -23.54350, long = -46.66014,
                        localName = "Ecoponto Pinheiros", street = "Av. Rebouças",
                        numero = "2500", neighborhood = "Pinheiros",
                        city = "São Paulo", cep = "05401-000",
                        distance = "0.8 km", // NOVO CAMPO
                        recyclableTypes = listOf("Plástico", "Vidro", "Óleo")
                    ),
                    LocalEcoEntity(
                        lat = -23.5489, long = -46.6388,
                        localName = "Ecoponto Sé", street = "Praça da Sé",
                        numero = "10", neighborhood = "Sé",
                        city = "São Paulo", cep = "01001-000",
                        distance = "1.5 km",
                        recyclableTypes = listOf("Eletrônico", "Bateria")
                    )
                )
                mockEcopoints.forEach {
                    localEcoRepository.saveLocalEco(it)
                }
            }
        }
    }
}