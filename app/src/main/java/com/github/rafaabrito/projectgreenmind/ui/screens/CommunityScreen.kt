package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayViolet
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightWhite
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.OuterSpace
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.SeaGreen

@Composable
fun CommunityScreen() {
    Surface {
        Scaffold(
            topBar = { TopBarComponent() },
            bottomBar = { BottomBarComponent() }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CommunityTopSection()
                Spacer(modifier = Modifier.height(16.dp))
                CommunitySearchBar()
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        CommunityMessageCard(
                            userName = "Mauricio Henrique",
                            message = "Você viu que a associação de moradores vai começar aquele projeto de hortas comunitárias? Achei uma ideia excelente! Além de ajudar na alimentação, é uma forma de incentivar a sustentabilidade aqui no bairro."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        CommunityMessageCard(
                            userName = "Lorena Alves",
                            message = "Sim, eu vi! Fiquei super empolgada. Acho importante a gente pensar mais no meio ambiente e em pequenas atitudes podem fazer diferença. Cultivar alimentos sem agrotóxicos, reutilizar materiais, fazer compostagem... tudo isso ajuda muito."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

//                    item {
//                        CommunityUserDraftCard()
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
                }

                CommunityUserKeyboard()
            }
        }
    }
}

@Composable
fun CommunityTopSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGrayViolet, RoundedCornerShape(12.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
    )
        {
        Row(
            modifier = Modifier
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.house),
                    contentDescription = "Casa",
                    modifier = Modifier.size(45.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Feed da comunidade",
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CommunitySearchBar() {
    var searchText by remember { mutableStateOf("") }

    val barHeight = 50.dp
    val cornerRadius = 28.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MinimumGray)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone de Busca (Leading Icon)
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Texto de Busca
        Text(
            text = "Pesquise ou filtre os comentários ...",
            color = Color.White,
            fontSize = 15.sp,
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        // Ícone de Filtro
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // Ação de clique para abrir o filtro
                }
        )
    }
}

@Composable
fun CommunityMessageCard(
    userName: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SeaGreen, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            // User info header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF00D9A3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Message box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightWhite, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

//@Composable
//fun CommunityUserDraftCard() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(SeaGreen, RoundedCornerShape(16.dp))
//            .padding(12.dp)
//    ) {
//        Column {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(bottom = 10.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(44.dp)
//                        .background(Color.Black, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "User",
//                        tint = Color.White,
//                        modifier = Modifier.size(26.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.dp))
//
//                // Placeholder loading lines
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .width(120.dp)
//                            .height(12.dp)
//                            .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
//                    )
//                    Spacer(modifier = Modifier.height(6.dp))
//                    Box(
//                        modifier = Modifier
//                            .width(80.dp)
//                            .height(12.dp)
//                            .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
//                    )
//                }
//            }
//
//            // Box da mensagem do User
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(LightWhite, RoundedCornerShape(12.dp))
//                    .padding(16.dp)
//            ) {
//                Column {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(10.dp)
//                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth(0.9f)
//                            .height(10.dp)
//                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth(0.7f)
//                            .height(10.dp)
//                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun CommunityUserKeyboard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.DarkGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Box para mensagem
        Box(
            modifier = Modifier
                .weight(1f)
                .background(DarkGrayViolet, RoundedCornerShape(10.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Escreva sua mensagem",
                    fontSize = 15.sp,
                    color = Color.LightGray,
                    modifier = Modifier.weight(1f)
                )

                // Botões de Câmera e Adição
                Row(
                    modifier = Modifier
                        .background(OuterSpace, RoundedCornerShape(10.dp))
                        .padding(5.dp)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Câmera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Adição",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    CommunityScreen()
}
