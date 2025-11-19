package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import com.github.rafaabrito.projectgreenmind.ui.components.TopBarComponent
import com.github.rafaabrito.projectgreenmind.ui.viewModel.UserLocation
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkBrown
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGray
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayViolet
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkMutedGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyan
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightAqua
import com.github.rafaabrito.projectgreenmind.ui.theme.LightGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.viewModel.EcoViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration

@Composable
fun EcoScreen(
    viewModel: EcoViewModel = hiltViewModel()
) {
    val ecoPoints by viewModel.ecoPoints.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isMapExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredEcoPoints = remember(ecoPoints, searchText, selectedFilter) {
        ecoPoints.filter { ecopoint ->
            val searchMatch = if (searchText.isBlank()) true else {
                val query = searchText.trim().lowercase()
                ecopoint.localName.lowercase().contains(query) ||
                        ecopoint.city.lowercase().contains(query) ||
                        ecopoint.street.lowercase().contains(query)
            }

            val filterMatch = if (selectedFilter == "Todos") true else {
                // Filtra pelo tipo de material
                ecopoint.recyclableTypes.contains(selectedFilter)
            }
            searchMatch && filterMatch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp)
            .verticalScroll(scrollState) // Habilita a rolagem vertical
    ) {
        TopBarComponent()
        Spacer(modifier = Modifier.height(16.dp))

        // Seção Superior (Localização e Mapa)
        EcoLocalTopSection(
            userLocation = userLocation,
            ecoPoints = ecoPoints,
            isLoading = isLoading,
            isMapExpanded = isMapExpanded,
            onMapToggle = { isMapExpanded = it },
            searchText = searchText, // NOVO
            onSearchTextChange = { searchText = it },
            selectedFilter = selectedFilter, // NOVO
            onFilterSelected = { selectedFilter = it }
            )
        Spacer(modifier = Modifier.height(20.dp))

        // Seção Inferior (Lista de Ecopontos)
        EcoLocalBottomSection(ecoPoints = ecoPoints)
    }

    if (isMapExpanded) {
        userLocation?.let { location ->
            ExpandedMapOverlay(
                userLocation = location,
                ecoPoints = ecoPoints,
                onClose = { isMapExpanded = false }
            )
        }
    }
}

@Composable
fun ExpandedMapOverlay(
    userLocation: UserLocation,
    ecoPoints: List<LocalEcoEntity>,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        OsmMapView(
            userLocation = userLocation,
            ecoPoints = ecoPoints,
            modifier = Modifier.fillMaxSize()
        )

        // Botão Fechar ('X')
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Fechar Mapa",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.TopEnd) // Posiciona no canto superior direito
                .padding(24.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f)) // Fundo semi-transparente
                .clickable(onClick = onClose) // Ação para fechar
                .padding(8.dp)
        )
    }
}

@Composable
fun EcoLocalTopSection(
    userLocation: UserLocation?,
    ecoPoints: List<LocalEcoEntity>,
    isLoading: Boolean,
    isMapExpanded: Boolean,
    onMapToggle: (Boolean) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGrayViolet, RoundedCornerShape(12.dp))
                .padding(horizontal = 5.dp, vertical = 5.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.sust_localization),
                        contentDescription = "Reciclagem",
                        modifier = Modifier.size(45.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Localização e Reciclagem",
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MinimumGray)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text(
                        text = "Pesquise as cidades, ecopontos ...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                    )
                },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilterSectionDesign(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isMapExpanded) 0.dp else 300.dp) // Altura 300dp quando não expandido
                .clickable(enabled = userLocation != null && !isMapExpanded) {
                    onMapToggle(true)
                }
        ){
            if (userLocation != null && !isMapExpanded) {
                OsmMapView(
                    userLocation = userLocation,
                    ecoPoints = ecoPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            } else if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (userLocation == null && !isLoading) {
                Text("Localização do usuário não disponível", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun OsmMapView(userLocation: UserLocation, ecoPoints: List<LocalEcoEntity>, modifier: Modifier) {
    val context = LocalContext.current

    // Inicializa a configuração do osmdroid (necessário antes de criar a View)
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Define uma altura fixa para o mapa
            .clip(RoundedCornerShape(12.dp)),
        factory = {
            MapView(it).apply {
                // Configurações iniciais
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                // Centraliza inicialmente o mapa
                controller.setCenter(GeoPoint(userLocation.latitude, userLocation.longitude))
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Centraliza o mapa na localização do usuário
            val userGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
            mapView.controller.setCenter(userGeoPoint)

            // Adiciona o marcador do usuário
            val userMarker = Marker(mapView)
            userMarker.position = userGeoPoint
            userMarker.icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_local_person, null)
            userMarker.title = "Sua Localização"
            mapView.overlays.add(userMarker)

            // Adiciona marcadores para os Ecopontos
            ecoPoints.forEach { ecopoint ->
                val ecoGeoPoint = GeoPoint(ecopoint.lat, ecopoint.long)
                val ecoMarker = Marker(mapView)
                ecoMarker.position = ecoGeoPoint
                ecoMarker.icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eco_location, null)
                ecoMarker.title = ecopoint.localName
                ecoMarker.subDescription = "${ecopoint.street}, ${ecopoint.city}"
                mapView.overlays.add(ecoMarker)
            }
            mapView.invalidate()
        }
    )
}
@Composable
fun EcoLocalBottomSection(ecoPoints: List<LocalEcoEntity>) {
    Column(
        modifier = Modifier
            .background(DarkMutedGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(10.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (ecoPoints.isEmpty()) {
            Text(
                text = "Nenhum ecoponto encontrado na sua área.",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // Itera sobre a lista de ecopontos do ViewModel
            ecoPoints.forEach { ecopoint ->
                EcoPointCard(ecopoint = ecopoint)
            }
        }
    }
}

@Composable
fun FilterSectionDesign(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Todos", "Plástico", "Papel", "Vidro", "Metal", "Outros")

    // Estado da rolagem para a barra indicativa
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GreenCyan)
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEachIndexed { index, filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) DarkGreen else MediumGreen
                        )
                        .clickable {
                            onFilterSelected(filter) // Atualiza o filtro no EcoScreen
                            coroutineScope.launch {
                                // Lógica de rolagem:
                                val itemWidthWithSpacing = 110.dp
                                val containerWidthDp = 360.dp 

                                // Calcula o deslocamento para o item selecionado
                                val targetScrollX = (index * itemWidthWithSpacing.value).toInt()
                                // Calcula a posição final para centralizar o item na tela
                                val centerOffset = (containerWidthDp.value / 2).toInt() - (itemWidthWithSpacing.value / 2).toInt()

                                // Rola para a posição, garantindo que não seja negativo
                                scrollState.animateScrollTo(
                                    maxOf(0, targetScrollX - centerOffset)
                                )
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkGreen.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val scrollProgress = if (scrollState.maxValue > 0) {
            scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        } else 0f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◀",
                color = Color.DarkGray,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { /* Lógica de scroll */ }
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Barra de Progresso
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.LightGray)
            ) {
                Spacer(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkGray)
                        .align(Alignment.CenterStart)
                        .offset(x = (scrollProgress * 0.8f).dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Seta Direita
            Text(
                text = "▶",
                color = Color.DarkGray,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { /* Lógica de scroll */ }
            )
        }
    }
}
@Composable
fun EcoPointCard(ecopoint: LocalEcoEntity) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Ação ao clicar no card */ }
    ) {
        Row(
            modifier = Modifier
                .background(LightAqua)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(LightGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.local),
                    modifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do Ecoponto
            Column(modifier = Modifier.weight(1f)
                .background(LightAqua, RoundedCornerShape(8.dp))
                .padding(12.dp)
            ) {
                Text(
                    text = ecopoint.localName,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${ecopoint.street}, ${ecopoint.city} (${ecopoint.distance})",
                    fontSize = 13.sp,
                    fontFamily = Inter,
                    color = DarkBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Mostrando os tipos de materiais
                Text(
                    text = "Aceita: ${ecopoint.recyclableTypes.joinToString(", ")}",
                    fontSize = 12.sp,
                    fontFamily = Inter,
                    color = DarkBrown
                )
            }
        }
    }
}

@Preview
@Composable
private fun EcoLocalPreview() {
    EcoScreen()
}