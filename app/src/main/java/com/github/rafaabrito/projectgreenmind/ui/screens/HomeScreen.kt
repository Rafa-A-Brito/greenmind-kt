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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.BlackShade
import com.github.rafaabrito.projectgreenmind.ui.theme.BrightCyanGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.CyanLime
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGray
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkSpringGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.Micro5
import com.github.rafaabrito.projectgreenmind.ui.theme.OutGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.StrongGreen
import com.github.rafaabrito.projectgreenmind.ui.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val streakState by viewModel.streakState.collectAsStateWithLifecycle()
    val tasksProgress by viewModel.tasksProgress.collectAsStateWithLifecycle()
    val showStreakDialog by viewModel.showStreakDialog.collectAsStateWithLifecycle()
    val showLevelUpDialog by viewModel.showLevelUpDialog.collectAsStateWithLifecycle()
    val newLevel by viewModel.newLevel.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.performDailyCheckIn()
        viewModel.loadUserData()
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .background(White)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TopSection(
                userName = userState.user?.name,
                userPhotoUrl = userState.photoUrl,
                isLoading = userState.isLoading,
                userLevel = userState.userLevel,
                userXP = userState.userXP,
                currentStreak = streakState.currentStreak,
                completedTasksPercentage = tasksProgress.completedPercentage
            )

            Spacer(modifier = Modifier.height(16.dp))

            MiddleSection()
            Spacer(modifier = Modifier.height(16.dp))

            BottomSection(
                userXP = userState.userXP,
                userLevel = userState.userLevel
            )
            if (showStreakDialog) {
                StreakDialog(
                    streakDays = streakState.currentStreak,
                    isNewRecord = streakState.isNewRecord,
                    onDismiss = { viewModel.dismissStreakDialog() }
                )
            }

            // ✅ Dialog de Level Up
            if (showLevelUpDialog) {
                LevelUpDialog(
                    newLevel = newLevel,
                    onDismiss = { viewModel.dismissLevelUpDialog() }
                )
            }
        }
    }
}

@Composable
fun TopSection(
    userName: String? = null,
    userPhotoUrl: String? = null,
    isLoading: Boolean = false,
    userLevel: Int = 0,
    userXP: Int = 0,
    currentStreak: Int = 0,
    completedTasksPercentage: Float = 0f
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserImage(
                photoUrl = userPhotoUrl,
                isLoading = isLoading
            )
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
                    text = userName?.uppercase() ?: "USUÁRIO",
                    fontSize = 22.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }

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
                text = "NÍVEL $userLevel",
                fontSize = 18.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.xp_total),
                contentDescription = "XP",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "$userXP XP",
                fontSize = 18.sp,
                fontFamily = Micro5,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isLoading) {
        AchievementCardsSkeleton()
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StrongGreen)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AchievementCard(
                title = "Pontos",
                value = "$userXP XP",
                icon = R.drawable.trophy,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            AchievementCard(
                title = "Ofensiva (streak)",
                value = "$currentStreak dias",
                icon = R.drawable.fire,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            AchievementCard(
                title = "Desafios concluídos",
                value = "${(completedTasksPercentage * 100).toInt()}%",
                icon = R.drawable.medal_v2,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AchievementCardsSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrongGreen)
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(3) { index ->
            AchievementCardSkeleton(modifier = Modifier.weight(1f))
            if (index < 2) Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun AchievementCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(14.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun UserImage(
    photoUrl: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isLoading) MediumGray else White)
    ) {
        if (isLoading) {
            // Indicador de carregamento
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Carregando...",
                tint = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (photoUrl != null) {
            // Usa Coil para carregar a imagem do Firebase
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto do usuário",
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.image_person_error)
            )
        } else {
            // Ícone placeholder se não houver foto
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuário",
                tint = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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
            .background(Color.White)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
fun StreakDialog(
    streakDays: Int,
    isNewRecord: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.fire),
                    contentDescription = "Streak",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (streakDays == 1) "Bem-vindo de volta!" else "Ofensiva Mantida!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = OutGreen
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Você está em dia! 🎉",
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$streakDays ${if (streakDays == 1) "dia" else "dias"} consecutivos!",
                    fontSize = 32.sp,
                    fontFamily = Micro5,
                    fontWeight = FontWeight.Bold,
                    color = OutGreen,
                    textAlign = TextAlign.Center
                )
                if (isNewRecord) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🏆 Novo Recorde!",
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Continue assim para aprender ainda mais sobre sustentabilidade! 🌱",
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OutGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar", fontFamily = Inter, fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun LevelUpDialog(
    newLevel: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.medal),
                    contentDescription = "Level Up",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Parabéns! 🎊",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = OutGreen
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Você subiu de nível!",
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NÍVEL $newLevel",
                    fontSize = 48.sp,
                    fontFamily = Micro5,
                    fontWeight = FontWeight.Bold,
                    color = OutGreen
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Continue completando desafios para evoluir ainda mais! 💪",
                    fontSize = 14.sp,
                    fontFamily = Inter,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OutGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Incrível!", fontFamily = Inter, fontSize = 16.sp)
            }
        }
    )
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
                text = "Notícias",
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
        // slider de banner
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
fun BottomSection(
    userXP: Int = 0,
    userLevel: Int = 0
) {
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

    val nextLevelXP = (userLevel + 1) * 1000
    val currentLevelXP = userLevel * 1000
    val xpProgress = (userXP - currentLevelXP).coerceAtLeast(0)
    val xpNeeded = (nextLevelXP - currentLevelXP).coerceAtLeast(1)
    val progressPercentage = (xpProgress.toFloat() / xpNeeded.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0BA858))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
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
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .background(OutGreen, RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "$xpProgress/$xpNeeded XP",
                    fontSize = 30.sp,
                    fontFamily = Micro5,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicatorCustom(
            progress = progressPercentage,
            progressColor = DarkSpringGreen,
            backgroundColor = Color(0xFF4C4D4E),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BlackShade)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Complete desafios para alcançar a meta e subir de nível 💪",
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
    HomeScreen(viewModel = viewModel())
}