package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.components.BottomBarComponent
import com.github.rafaabrito.projectgreenmind.ui.components.TopBarComponent
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.BlackShade
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
import com.github.rafaabrito.projectgreenmind.ui.theme.PlhilippineSilver
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto

@Composable
fun EcoScreen() {
    Surface {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            topBar = { TopBarComponent() },
            bottomBar = { BottomBarComponent() }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                EcoLocalTopSection()
                Spacer(modifier = Modifier.height(20.dp))
                EcoLocalBottomSection()
            }
        }
    }
}

@Composable
fun EcoLocalTopSection() {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

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
            Text(
                text = "Pesquise as cidades, ecopontos ...",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilterSectionDesign()

        Spacer(modifier = Modifier.height(12.dp))

        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFFE8E8E8), RoundedCornerShape(12.dp))
        ) {
            // Placeholder for map - you would integrate actual map here
            Text(
                text = "🗺️ Mapa",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EcoLocalBottomSection() {
    Column(
        modifier = Modifier
            .background(DarkMutedGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(5.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Ecoponto 1
        EcoPointCard(
            name = "Ecoponto Vila Rio",
            distance = "400 m",
            types = "Eletrodomésticos, móveis"
        )

        // Ecoponto 2
        EcoPointCard(
            name = "Ecoponto Jd. Adriana",
            distance = "3.0 km",
            types = "Resto de poda de árvores, pneus"
        )

        // Ecoponto 3
        EcoPointCard(
            name = "Ecoponto Vila Barros",
            distance = "5.5 km",
            types = "Entulhos, pilhas"
        )
    }
}

@Composable
fun FilterSectionDesign() {
    var selectedFilter by remember { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Plástico", "Papel", "Vidro", "Metal", "Outros")

    // Estado da rolagem para a barra indicativa
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {

        // 1. Linha dos Filtros (Agora sem setas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GreenCyan)
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) DarkGreen else MediumGreen // Cores ajustadas
                        )
                        .clickable { selectedFilter = filter }
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
fun EcoPointCard(
    name: String,
    distance: String,
    types: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackShade, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Location icon
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

            // Ecopoint info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(LightAqua, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = name,
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$distance - $types",
                    fontSize = 13.sp,
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
