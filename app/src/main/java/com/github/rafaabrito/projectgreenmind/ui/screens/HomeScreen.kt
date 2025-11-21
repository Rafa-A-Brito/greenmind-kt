package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.BlackShade
import com.github.rafaabrito.projectgreenmind.ui.theme.BrightCyanGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.CyanLime
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGray
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkSpringGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.Micro5
import com.github.rafaabrito.projectgreenmind.ui.theme.OutGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.StrongGreen

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PlaceholderUserImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(White)
    ) {
        // Ícone placeholder ou imagem real do usuário
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Usuário",
            tint = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .background(White)
            .verticalScroll(scrollState)
    ){
        Spacer(modifier = Modifier.height(16.dp))

        TopSection()
        Spacer(modifier = Modifier.height(16.dp))

        MiddleSection()
        Spacer(modifier = Modifier.height(16.dp))

        BottomSection()
    }
}

@Composable
fun TopSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlaceholderUserImage()
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Bem vindo(a)",
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Text(
                    text = "JOÃO",
                    fontSize = 22.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }

        // Nível e XP
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MediumGray)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.medal),
                contentDescription = "Nível",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "NÍVEL 15",
                fontSize = 18.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.xp_total),
                contentDescription = "Nível",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "4800 XP",
                fontSize = 18.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = White
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
            .background(StrongGreen)
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AchievementCard(
            title = "Pontos",
            value = "38420 XP",
            icon = R.drawable.trophy,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AchievementCard(
            title = "Ofensiva (streak)",
            value = "3 semanas",
            icon = R.drawable.fire, // Ícone de Chama
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AchievementCard(
            title = "Desafios concluídos",
            value = "85%",
            icon = R.drawable.medal_v2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AchievementCard(
    title: String,
    value: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenCyanLight)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF26B6AF),
                textAlign = TextAlign.Center
            )
        }
    }

@Composable
fun MiddleSection() {
    // Banner de Sustentabilidade
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyanLime)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dicas semanais",
                color = Black,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrightCyanGreen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                text = "Sustentabilidade",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSpringGreen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .background(BlackShade.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Antes de comprar, pense: eu realmente preciso disso? Praticar o consumo consciente ajuda a economizar " +
                        "recursos naturais, reduzir o desperdício e até poupar dinheiro. 🌿",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 18   .sp,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BlackShade.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(3.dp)
                .clickable { /* Ação de marcar conclusão */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.
                    padding(horizontal = 6.dp),
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = "Marcar conclusão",
                tint = GreenCyanLight
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = "Marcar conclusão (+ 25XP)",
                    color = White,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Ações Rápidas
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = "Ações Rápidas",
            tint = DarkGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Ações Rápidas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
            .background(StrongGreen, RoundedCornerShape(10.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionCard(
            text = "Ecopontos",
            icon = R.drawable.local,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        QuickActionCard(
            text = "Registrar descarte",
            icon = R.drawable.sustainable_logo,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        QuickActionCard(
            text = "Desafios",
            icon = R.drawable.trophy_2d,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BlackShade)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone com fundo arredondado
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = text,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
                textAlign = TextAlign.Center
            )
        }
    }

@Composable
fun BottomSection() {
    // Progresso da Semana
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🚀 Progresso da Semana",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Caixa de Progresso (XP)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0BA858))
            .padding(16.dp)
    ) {
        // Título/ de XP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(

            ) {
                Image(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(R.drawable.xp_user),
                    contentDescription = "Xp"
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Pontos",
                    fontFamily = Micro5,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Normal,
                    color = White
                )
            }
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .background(OutGreen,RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "300/450 XP",
                    fontSize = 30.sp,
                    fontFamily = Micro5,
                    fontWeight = FontWeight.Normal,
                    color = White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Barra de Progresso Customizada
        LinearProgressIndicatorCustom(
            progress = 300f / 450f,
            progressColor = DarkSpringGreen,
            backgroundColor = Color(0xFF4C4D4E),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mensagem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BlackShade)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Complete desafios para alcançar a meta e subir de nível 👌",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Composable de Progresso Linear (para replicar o visual)
@Composable
fun LinearProgressIndicatorCustom(
    progress: Float,
    progressColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(backgroundColor)) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(progressColor)
        )
    }
}


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }){ onClick()
        }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}