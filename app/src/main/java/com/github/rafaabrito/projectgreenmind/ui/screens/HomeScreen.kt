package com.github.rafaabrito.projectgreenmind.ui.screens


import android.media.Image
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.github.rafaabrito.projectgreenmind.ui.theme.GrotesqueGreen
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.exyte.animatednavbar.utils.noRippleClickable
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.components.BottomBarComponent
import com.github.rafaabrito.projectgreenmind.ui.components.TopBarComponent
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.BlackShade
import com.github.rafaabrito.projectgreenmind.ui.theme.BrightCyanGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkSpringGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.ForestGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightShadeGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumBlack
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.Micro5
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.RobotoMono
import com.github.rafaabrito.projectgreenmind.ui.theme.SeafomGreen

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PlaceholderUserImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.White)
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
        TopBarComponent()
        Spacer(modifier = Modifier.height(16.dp))
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
    // Cabeçalho de Boas-vindas e Nível
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
                    fontSize = 12.sp,
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "NÍVEL 15",
                fontSize = 14.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.xp_total),
                contentDescription = "Nível",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "4800 XP",
                fontSize = 14.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = White
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Cards de Conquistas (Pontos, Ofensiva, Desafios)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AchievementCard(
            title = "Pontos",
            value = "38420 XP",
            icon = Icons.Default.EmojiEvents,
            iconTint = White,
            backgroundColor = Color(0xFF0BA858),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AchievementCard(
            title = "Ofensiva (streak)",
            value = "3 semanas",
            icon = Icons.Default.LocalFireDepartment, // Ícone de Chama
            iconTint = Color.Red,
            backgroundColor = Color(0xFF0BA858),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AchievementCard(
            title = "Desafios concluídos",
            value = "85%",
            icon = Icons.Default.Stars,
            iconTint = GreenCyanLight,
            backgroundColor = Color(0xFF0BA858),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AchievementCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Valor
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            textAlign = TextAlign.Center
        )
        // Título
        Text(
            text = title,
            fontSize = 12.sp,
            color = GreenCyanLight,
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
            .background(Color(0xFF006400)) // Um verde escuro similar ao da imagem
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dicas semanais",
                color = Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrightCyanGreen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                text = "Sustentabilidade",
                color = White,
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
                .padding(15.dp)
                .background(BlackShade)
        ) {
            Text(
                text = "Antes de comprar, pense: eu realmente preciso disso? Praticar o consumo consciente ajuda a economizar recursos naturais, reduzir o desperdício e até poupar dinheiro. 🌿",
                color = White,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x20FFFFFF)) // Um box mais escuro para o botão de conclusão
                .padding(8.dp)
                .clickable { /* Ação de marcar conclusão */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = "Marcar conclusão",
                tint = GreenCyanLight
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .background(BlackShade)
            ) {
                Text(
                    text = "Marcar conclusão (+ 25XP)",
                    color = White,
                    fontWeight = FontWeight.SemiBold
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
            tint = DarkSpringGreen,
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionCard(
            text = "Ecopontos",
            icon = Icons.Default.LocationOn,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        QuickActionCard(
            text = "Registrar descarte",
            icon = Icons.Default.Recycling,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        QuickActionCard(
            text = "Desafios",
            icon = Icons.Default.EmojiEvents,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0BA858))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone com fundo arredondado
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(White)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF0BA858),
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
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
        // Título/Contador de XP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PONTOS",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenCyanLight
            )
            Text(
                text = "300/450 XP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Barra de Progresso Customizada
        LinearProgressIndicatorCustom(
            progress = 300f / 450f, // Exemplo: 300 de 450 XP
            progressColor = BrightCyanGreen,
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