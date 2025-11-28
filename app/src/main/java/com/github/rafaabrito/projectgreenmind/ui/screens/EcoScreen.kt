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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.core.net.toUri
import com.github.rafaabrito.projectgreenmind.domain.utils.permissions.rememberLocationPermissionState
import com.github.rafaabrito.projectgreenmind.ui.theme.BlackShade
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
        AddressDialog(
            userLocation = userLocation,
            onDismiss = { showAddressDialog = false }
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Conteúdo principal
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
        }

        // Mapa expandido como overlay (popup)
        if (isMapExpanded) {
            // Sombra de fundo (overlay escurecido)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { }
            )

            // Mapa com elevação
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
    var showHint by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
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
                modifier = Modifier.fillMaxSize()
            )

            // Botão fechar
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

            // ✅ Mensagem de dica (não é dialog)
            if (showHint) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF5ED88B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Toque no ícone do ecoponto para traçar a rota",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Inter
                            )
                        }
                        IconButton(
                            onClick = { showHint = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar dica",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
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
                .height(56.dp),
            placeholder = {
                Text(
                    "Filtrar ecopontos...",
                    color = Color.LightGray,
                    maxLines = 1
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
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {

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

        // Mapa
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

        Spacer(modifier = Modifier.height(8.dp))

        // Botão para expandir o mapa
        Button(
            onClick = { onMapToggle(true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5ED88B)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Expandir Mapa",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ver Mapa em Tela Cheia",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = Inter
            )
        }
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
            .padding(horizontal = 8.dp, vertical = 6.dp),
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informação",
                    tint = Color.Gray,
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
                setBuiltInZoomControls(false)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            mapView.controller.animateTo(initialCenter)
            mapView.controller.setZoom(zoomLevel)

            // Marcador do usuário
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

                // Desabilitar InfoWindow padrão
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                userMarker.setOnMarkerClickListener { _, _ ->
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

                // Desabilitar InfoWindow padrão
                ecoMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                ecoMarker.setOnMarkerClickListener { _, _ ->
                    onEcoPointMarkerClick(ecopoint)
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
                if (userLocation != null && ecoPoint.distance.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Distância estimada: ${ecoPoint.distance}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5ED88B)
                    )
                    Text(
                        "(A rota real pode variar)",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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
    onEcoPointClick: (LocalEcoEntity) -> Unit

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
                    onClick = { onEcoPointClick(ecopoint) }
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
                .background(BlackShade)
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
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(LightAqua, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = ecopoint.localName,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${ecopoint.street}, ${ecopoint.numero} - ${ecopoint.city}",
                    fontSize = 13.sp,
                    fontFamily = Inter,
                    color = Black
                )

                if (ecopoint.distance.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Distância estimada: ${ecopoint.distance}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Inter,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ecopoint.recyclableTypes.joinToString(", "),
                    fontSize = 12.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    color = Black
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

@Preview
@Composable
private fun EcoLocalPreview() {
    EcoScreen()
}