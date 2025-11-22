@file:Suppress("DEPRECATION")

package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.domain.entities.LocalEcoEntity
import com.github.rafaabrito.projectgreenmind.ui.viewModel.UserLocation
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkBrown
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayViolet
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkMutedGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightAqua
import com.github.rafaabrito.projectgreenmind.ui.theme.LightGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray
import com.github.rafaabrito.projectgreenmind.ui.viewModel.EcoViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.core.net.toUri
import com.github.rafaabrito.projectgreenmind.domain.utils.rememberLocationPermissionState
import kotlinx.coroutines.delay

private val PermittedMaterials = listOf(
    "Entulhos (Concretos, tijolos, pisos) - Máx. 1m³/dia",
    "Madeiras (Tábuas e móveis velhos desmontados) - Máx. 1m³/dia",
    "Sofás e Colchões (Máx. 2 unidades/ano)",
    "Pneus (Máximo 5 peças/ano)",
    "Óleo de cozinha (em recipientes bem fechados)",
    "Material de Coleta Seletiva (Papel, plástico, metal, vidro)"
)

private val ForbiddenMaterials = listOf(
    "Amianto (Exige contratação de caçamba)",
    "Lixo Doméstico (Encaminhar para coleta comum)",
    "Resíduos de serviços de saúde (Seringas, agulhas)",
    "Resíduos Automotivos (Peças de carro, para-brisas)",
    "Cartuchos e toners de impressoras (Entregar em lojas do ramo)",
    "Lâmpadas e Pilhas/Baterias"
)

private const val ECOPONTO_URL = "https://www.guarulhos.sp.gov.br/ecoponto"

@Composable
fun EcoScreen(
    viewModel: EcoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Estados do ViewModel
    val filteredEcoPoints by viewModel.filteredEcoPoints.collectAsState()
    val searchTerm by viewModel.searchTerm.collectAsState()
    val ecoPoints by viewModel.ecoPoints.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEcoPoint by viewModel.selectedEcoPoint.collectAsState()
    val showNavigationDialog by viewModel.showNavigationDialog.collectAsState()

    val locationPermissionState = rememberLocationPermissionState(
        onPermissionGranted = {
            viewModel.fetchUserLocation()
        },
        onPermissionDenied = {
            Toast.makeText(
                context,
                "Permissão de localização negada. Mostrando apenas ecopontos de Guarulhos.",
                Toast.LENGTH_LONG
            ).show()
        }
    )
    LaunchedEffect(Unit) {
        if (!locationPermissionState.hasPermission) {
            locationPermissionState.requestPermission()
        } else {
            viewModel.fetchUserLocation()
        }
    }
    // Estados Locais
    var isMapExpanded by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showPermittedDialog by remember { mutableStateOf(false) }
    var showForbiddenDialog by remember { mutableStateOf(false) }

    val mapHeight = 300.dp

    showNavigationDialog?.let { ecoPoint ->
        NavigationDialog(
            ecoPoint = ecoPoint,
            userLocation = userLocation,
            onDismiss = { viewModel.dismissNavigationDialog() },
            onNavigate = { destination ->
                val gmmIntentUri =
                    "google.navigation:q=${destination.lat},${destination.long}&mode=d".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    // Fallback para navegador
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.google.com/maps/dir/?api=1&destination=${destination.lat},${destination.long}".toUri()
                    )
                    context.startActivity(browserIntent)
                }
                viewModel.dismissNavigationDialog()
            }
        )
    }

    if (showAddressDialog) {
        val locationData = userLocation

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            confirmButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("OK", color = Color(0xFF5ED88B))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PersonPin,
                        contentDescription = "Localização",
                        tint = Color(0xFF5ED88B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sua Localização Atual")
                }
            },
            text = {
                val street = locationData?.street ?: "Buscando o nome da rua..."
                val numero = locationData?.numero ?: "N/D"
                val city = locationData?.city ?: "N/D"

                val addressText = "Rua: $street\n" +
                        "Número: $numero\n" +
                        "Cidade: $city\n" +
                        "Coordenadas: ${locationData?.latitude ?: "N/D"}, ${locationData?.longitude ?: "N/D"}"

                Text(addressText, color = Color.Black)
            }
        )
    }

    if (showPermittedDialog) {
        MaterialInfoDialog(
            title = "Materiais Permitidos",
            materials = PermittedMaterials,
            onDismiss = { showPermittedDialog = false }
        )
    }

    if (showForbiddenDialog) {
        MaterialInfoDialog(
            title = "Materiais Proibidos",
            materials = ForbiddenMaterials,
            onDismiss = { showForbiddenDialog = false }
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        EcoLocalTopSection(
            viewModel = viewModel,
            userLocation = userLocation,
            ecoPoints = ecoPoints,
            selectedEcoPoint = selectedEcoPoint,
            isLoading = isLoading,
            mapHeight = mapHeight,
            isMapExpanded = isMapExpanded,
            onMapToggle = { isMapExpanded = true },
            onUserLocationClick = { showAddressDialog = true },
            onEcoPointMarkerClick = { ecoPoint ->
                viewModel.showNavigationDialog(ecoPoint)
            },
            onPermittedClick = { showPermittedDialog = true },
            onForbiddenClick = { showForbiddenDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))

        EcoLocalBottomSection(
            ecoPoints = filteredEcoPoints,
            onEcoPointClick = { ecoPoint ->
                viewModel.selectEcoPoint(ecoPoint)
                isMapExpanded = true
            }
        )

        if (isMapExpanded) {
            ExpandedMapOverlay(
                userLocation = userLocation,
                ecoPoints = ecoPoints,
                selectedEcoPoint = selectedEcoPoint,
                onClose = {
                    isMapExpanded = false
                    viewModel.clearSelectedEcoPoint()
                },
                onUserLocationClick = { showAddressDialog = true },
                onEcoPointMarkerClick = { ecoPoint ->
                    viewModel.showNavigationDialog(ecoPoint)
                }
            )
        }
    }
}
@Composable
fun MaterialInfoDialog(
    title: String,
    materials: List<String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Informação",
                    tint = if (title.contains("Permitidos")) Color(0xFF5ED88B) else Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                materials.forEach { material ->
                    Text("• $material", fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    buildAnnotatedString {
                        append("Para mais detalhes, ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF63D4E6),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Veja ➕")
                        }
                    },
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, ECOPONTO_URL.toUri())
                            context.startActivity(intent)
                            onDismiss()
                        }
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.Gray
                )
            }
        }
    )
}

@Composable
fun ExpandedMapOverlay(
    userLocation: UserLocation?,
    ecoPoints: List<LocalEcoEntity>,
    onClose: () -> Unit,
    selectedEcoPoint: LocalEcoEntity?,
    onUserLocationClick: () -> Unit,
    onEcoPointMarkerClick: (LocalEcoEntity) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OsmMapView(
            userLocation = userLocation,
            ecoPoints = ecoPoints,
            selectedEcoPoint = selectedEcoPoint,
            onUserLocationClick = onUserLocationClick,
            onEcoPointMarkerClick = onEcoPointMarkerClick,
            onMapClick = onClose,
            modifier = Modifier.fillMaxSize()
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Fechar Mapa",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
                .clickable(onClick = onClose)
                .padding(8.dp)
        )
    }
}

@Composable
fun EcoLocalTopSection(
    viewModel: EcoViewModel,
    userLocation: UserLocation?,
    ecoPoints: List<LocalEcoEntity>,
    selectedEcoPoint: LocalEcoEntity?,
    isLoading: Boolean,
    mapHeight: Dp,
    isMapExpanded: Boolean,
    onMapToggle: (Boolean) -> Unit,
    onUserLocationClick: () -> Unit,
    onEcoPointMarkerClick: (LocalEcoEntity) -> Unit,
    onPermittedClick: () -> Unit,
    onForbiddenClick: () -> Unit
) {
    val context = LocalContext.current
    val searchTerm by viewModel.searchTerm.collectAsState()
    val filteredCount by viewModel.filteredEcoPoints.collectAsState()

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
        OutlinedTextField(
            value = searchTerm,
            onValueChange = { newValue ->
                viewModel.updateSearchTerm(newValue)

                // ✅ Mostrar toast com resultado do filtro
                if (newValue.isNotBlank()) {
                    val count = filteredCount.size
                    Toast.makeText(
                        context,
                        if (count > 0) "Encontrados $count ecoponto(s)" else "Nenhum ecoponto encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // ✅ Altura fixa
            placeholder = {
                Text(
                    "Filtrar ecopontos...",
                    color = Color.LightGray,
                    maxLines = 1 // ✅ Placeholder em 1 linha
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.White
                )
            },
            trailingIcon = {
                if (searchTerm.isNotEmpty()) {
                    // ✅ Botão para limpar
                    IconButton(onClick = { viewModel.updateSearchTerm("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Limpar",
                            tint = Color.White
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filtro",
                        tint = Color.White
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MinimumGray,
                unfocusedContainerColor = MinimumGray
            ),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search // ✅ Botão de busca no teclado
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // ✅ Ao pressionar Enter/Search, fecha o teclado
                    // e mostra resultado
                    val count = filteredCount.size
                    Toast.makeText(
                        context,
                        if (count > 0) "Encontrados $count ecoponto(s)" else "Nenhum ecoponto encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        RecyclingInfoSection(
            onPermittedClick = onPermittedClick,
            onForbiddenClick = onForbiddenClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (userLocation != null) {
                OsmMapView(
                    userLocation = userLocation,
                    ecoPoints = ecoPoints,
                    selectedEcoPoint = selectedEcoPoint,
                    onUserLocationClick = onUserLocationClick,
                    onEcoPointMarkerClick = onEcoPointMarkerClick,
                    onMapClick = { onMapToggle(true) },
                    modifier = Modifier.fillMaxSize()
                )
            } else if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Text(
                    "Localização do usuário não disponível",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        MapBoxWithLongPress(
            userLocation = userLocation,
            ecoPoints = ecoPoints,
            selectedEcoPoint = selectedEcoPoint,
            isLoading = isLoading,
            mapHeight = mapHeight,
            onMapToggle = onMapToggle,
            onUserLocationClick = onUserLocationClick,
            onEcoPointMarkerClick = onEcoPointMarkerClick
        )
    }
}

@Composable
fun RecyclingInfoSection(
    onPermittedClick: () -> Unit,
    onForbiddenClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MaterialBox(
            title = "Materiais Permitidos",
            bgColor = Color(0xFF4CAF50),
            onClick = onPermittedClick,
            modifier = Modifier.weight(1f)
        )

        MaterialBox(
            title = "Materiais Proibidos",
            bgColor = Color(0xFFF44336),
            onClick = onForbiddenClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MaterialBox(
    title: String,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informação",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OsmMapView(
    userLocation: UserLocation?,
    ecoPoints: List<LocalEcoEntity>,
    selectedEcoPoint: LocalEcoEntity? = null,
    onUserLocationClick: () -> Unit,
    onEcoPointMarkerClick: (LocalEcoEntity) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val guarulhosCenter = GeoPoint(-23.4665, -46.5385)

    val initialCenter = when {
        selectedEcoPoint != null -> GeoPoint(selectedEcoPoint.lat, selectedEcoPoint.long)
        userLocation != null -> GeoPoint(userLocation.latitude, userLocation.longitude)
        else -> guarulhosCenter
    }

    val zoomLevel = if (selectedEcoPoint != null) 16.0 else 14.0

    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(initialCenter)
                this.setOnClickListener {
                    onMapClick()
                }
                setBuiltInZoomControls(false)
                setMultiTouchControls(true)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            mapView.controller.animateTo(initialCenter)
            mapView.controller.setZoom(zoomLevel)

            // ✅ Marcador do usuário
            if (userLocation != null) {
                val userGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
                val userMarker = Marker(mapView)
                userMarker.position = userGeoPoint
                userMarker.icon = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_local_person,
                    null
                )
                userMarker.title = "📍 Sua Localização"
                userMarker.snippet = buildString {
                    append("${userLocation.street}")
                    if (userLocation.numero != null) {
                        append(", ${userLocation.numero}")
                    }
                    append("\n${userLocation.city}")
                }

                userMarker.setOnMarkerClickListener { marker, _ ->
                    marker.showInfoWindow()
                    onUserLocationClick()
                    true
                }

                mapView.overlays.add(userMarker)
            }

            // Marcadores dos ecopontos
            ecoPoints.forEach { ecopoint ->
                val ecoGeoPoint = GeoPoint(ecopoint.lat, ecopoint.long)
                val ecoMarker = Marker(mapView)
                ecoMarker.position = ecoGeoPoint
                ecoMarker.icon = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_eco_location,
                    null
                )

                ecoMarker.title = "♻️ ${ecopoint.localName}"
                ecoMarker.snippet = buildString {
                    append("📍 ${ecopoint.street}, ${ecopoint.numero}\n")
                    append("🏙️ ${ecopoint.neighborhood} - ${ecopoint.city}\n")

                    if (ecopoint.distance != "indisponível") {
                        append("📏 Distância: ${ecopoint.distance}\n")
                    }

                    append("\n♻️ Aceita:\n")
                    ecopoint.recyclableTypes.take(4).forEach {
                        append("  • $it\n")
                    }
                    if (ecopoint.recyclableTypes.size > 4) {
                        append("  ...e mais ${ecopoint.recyclableTypes.size - 4}")
                    }
                }

                // ✅ Customizar a Info Window
                ecoMarker.infoWindow = object : org.osmdroid.views.overlay.infowindow.InfoWindow(
                    org.osmdroid.library.R.layout.bonuspack_bubble,
                    mapView
                ) {
                    override fun onOpen(item: Any?) {
                        // Quando a info window abre
                        val marker = item as? Marker
                        view.findViewById<android.widget.TextView>(
                            org.osmdroid.library.R.id.bubble_title
                        )?.text = marker?.title

                        view.findViewById<android.widget.TextView>(
                            org.osmdroid.library.R.id.bubble_description
                        )?.text = marker?.snippet

                        // ✅ Botão "Navegar" na info window
                        view.findViewById<android.widget.Button>(
                            org.osmdroid.library.R.id.bubble_moreinfo
                        )?.apply {
                            text = "Navegar"
                            visibility = android.view.View.VISIBLE
                            setOnClickListener {
                                close()
                                onEcoPointMarkerClick(ecopoint)
                            }
                        }
                    }

                    override fun onClose() {
                        val defaultCenter = when {
                            selectedEcoPoint != null -> GeoPoint(selectedEcoPoint.lat, selectedEcoPoint.long)
                            userLocation != null -> GeoPoint(userLocation.latitude, userLocation.longitude)
                            else -> GeoPoint(-23.4665, -46.5385) // Guarulhos
                        }

                        mapView.controller.animateTo(defaultCenter)
                        mapView.controller.setZoom(14.0)
                    }
                }

                // ✅ Ao clicar no marcador
                ecoMarker.setOnMarkerClickListener { marker, _ ->
                    // Mostra a info window nativa
                    marker.showInfoWindow()

                    // Após 1 segundo, abre o diálogo de navegação
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        onEcoPointMarkerClick(ecopoint)
                    }, 1000)

                    true
                }

                mapView.overlays.add(ecoMarker)
            }

            mapView.invalidate()
        }
    )
}
@Composable
fun NavigationDialog(
    ecoPoint: LocalEcoEntity,
    userLocation: UserLocation?,
    onDismiss: () -> Unit,
    onNavigate: (LocalEcoEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Navigation,
                contentDescription = "Navegação",
                tint = Color(0xFF5ED88B)
            )
        },
        title = {
            Text("Navegar até ${ecoPoint.localName}?")
        },
        text = {
            Column {
                Text("Deseja abrir o Google Maps para navegar até este local?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${ecoPoint.street}, ${ecoPoint.numero} - ${ecoPoint.city}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (userLocation != null && ecoPoint.distance != "indisponível") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Distância: ${ecoPoint.distance}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5ED88B)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onNavigate(ecoPoint) }) {
                Text("Navegar", color = Color(0xFF5ED88B))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun EcoLocalBottomSection(
    ecoPoints: List<LocalEcoEntity>,
    onEcoPointClick: (LocalEcoEntity) -> Unit // ✅ Adicionar callback

) {
    Column(
        modifier = Modifier
            .background(DarkMutedGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (ecoPoints.isEmpty()) {
            Text(
                text = "Nenhum ecoponto encontrado na sua área.",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            ecoPoints.forEach { ecopoint ->
                EcoPointCard(
                    ecopoint = ecopoint,
                    onClick = { onEcoPointClick(ecopoint) } // ✅ Passar callback
                )
            }
        }
    }
}

@Composable
fun EcoPointCard(
    ecopoint: LocalEcoEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
            Column(
                modifier = Modifier.weight(1f)
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

@Composable
fun AddressDialog(
    userLocation: UserLocation?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = Color(0xFF5ED88B))
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PersonPin,
                    contentDescription = "Localização",
                    tint = Color(0xFF5ED88B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sua Localização Atual")
            }
        },
        text = {
            if (userLocation != null) {
                val street = userLocation.street ?: "Não disponível"
                val numero = userLocation.numero ?: "S/N"
                val city = userLocation.city ?: "Não disponível"

                Column {
                    Text(
                        text = "Endereço:",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$street, $numero",
                        fontFamily = Inter,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cidade:",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = city,
                        fontFamily = Inter,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Coordenadas:",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${String.format("%.6f", userLocation.latitude)}, ${String.format("%.6f", userLocation.longitude)}",
                        fontSize = 14.sp,
                        fontFamily = Inter,
                        color = Color.Black,
                    )
                }
            } else {
                Text(
                    fontFamily = Inter,
                    text = "Localização não disponível. Verifique se concedeu permissão de localização.",
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun MapBoxWithLongPress(
    userLocation: UserLocation?,
    ecoPoints: List<LocalEcoEntity>,
    selectedEcoPoint: LocalEcoEntity?,
    isLoading: Boolean,
    mapHeight: Dp,
    onMapToggle: (Boolean) -> Unit,
    onUserLocationClick: () -> Unit,
    onEcoPointMarkerClick: (LocalEcoEntity) -> Unit
) {
    var isLongPressing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(mapHeight)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        // ✅ Long press detectado - expandir mapa
                        isLongPressing = true
                        onMapToggle(true)
                    },
                    onPress = {
                        // ✅ Detecta quando o usuário começa a pressionar
                        val press = try {
                            awaitRelease()
                        } catch (e: Exception) {
                            false
                        }
                    }
                )
            }
    ) {
        if (userLocation != null || !isLoading) {
            OsmMapView(
                userLocation = userLocation,
                ecoPoints = ecoPoints,
                selectedEcoPoint = selectedEcoPoint,
                onUserLocationClick = onUserLocationClick,
                onEcoPointMarkerClick = onEcoPointMarkerClick,
                onMapClick = { }, // Não expandir com tap simples
                modifier = Modifier.fillMaxSize()
            )

            // ✅ Indicador visual de long press
            if (isLongPressing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }

                // Reset após abrir
                LaunchedEffect(Unit) {
                    delay(300)
                    isLongPressing = false
                }
            }

            // ✅ Hint visual para o usuário
            Text(
                text = "Pressione e segure para expandir",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = Inter
            )
        } else if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Text(
                "Localização do usuário não disponível",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
private fun EcoLocalPreview() {
    EcoScreen()
}