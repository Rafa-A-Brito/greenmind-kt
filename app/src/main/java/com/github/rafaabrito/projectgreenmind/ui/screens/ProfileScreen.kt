package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.domain.utils.permissions.rememberLocationPermissionState
import com.github.rafaabrito.projectgreenmind.ui.theme.*
import com.github.rafaabrito.projectgreenmind.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhotoUrl by viewModel.userPhotoUrl.collectAsStateWithLifecycle()
    val userLevel by viewModel.userLevel.collectAsStateWithLifecycle()
    val totalScore by viewModel.totalScore.collectAsStateWithLifecycle()
    val userRank by viewModel.userRank.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // ✅ Estados dos toggles
    val showTipsPopup by viewModel.showTipsPopup.collectAsStateWithLifecycle()
    val enableWeeklyNotifications by viewModel.enableWeeklyNotifications.collectAsStateWithLifecycle()
    val allowLocation by viewModel.allowLocation.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    // ✅ Verificar se usuário tem conquistas (badges desbloqueados)
    val hasAchievements = totalScore > 0 || userLevel > 0

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF5ED88B))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                ProfileHeader(
                    userName = userName ?: "Usuário",
                    userPhotoUrl = userPhotoUrl,
                    userRank = userRank,
                    userLevel = userLevel,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // ✅ MUDANÇA 1: Mostrar skeleton se não tiver conquistas
                if (hasAchievements) {
                    StatsBlock(
                        totalScore = totalScore,
                        userLevel = userLevel
                    )
                } else {
                    StatsBlockSkeleton()
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // ✅ MUDANÇA 1: Mostrar skeleton de badges
                if (hasAchievements) {
                    BadgesBlock()
                } else {
                    BadgesBlockSkeleton()
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // ✅ MUDANÇA 2 e 3: Toggles com dialogs e permission handler
                SettingsToggleBlock(
                    showTipsPopup = showTipsPopup,
                    enableWeeklyNotifications = enableWeeklyNotifications,
                    allowLocation = allowLocation,
                    onShowTipsPopupChange = { viewModel.updateShowTipsPopup(it) },
                    onEnableNotificationsChange = { viewModel.updateEnableNotifications(it) },
                    onAllowLocationChange = { viewModel.updateAllowLocation(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userPhotoUrl: String?,
    userRank: String,
    userLevel: Int,
    onNavigateToSettings: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OutGreen, RoundedCornerShape(15.dp))
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (userPhotoUrl != null && userPhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.image_person_error)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF5ED88B),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = userRank,
                fontFamily = Inter,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = "Nível $userLevel",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold
            )
        }

        IconButton(onClick = onNavigateToSettings) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configurações",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun StatsBlock(totalScore: Int, userLevel: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatsCard(
            title = "Pontos",
            value = "$totalScore XP",
            iconRes = R.drawable.trophy,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatsCard(
            title = "Nível",
            value = "$userLevel",
            iconRes = R.drawable.medal_v2,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatsCard(
            title = "Ofensiva",
            value = "0 dias",
            iconRes = R.drawable.fire,
            modifier = Modifier.weight(1f)
        )
    }
}

// ✅ MUDANÇA 1: Skeleton para StatsBlock
@Composable
fun StatsBlockSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(3) {
            StatsCardSkeleton(modifier = Modifier.weight(1f))
            if (it < 2) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun StatsCardSkeleton(modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp),
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
                .fillMaxWidth(0.6f)
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(14.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun StatsCard(title: String, value: String, iconRes: Int, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp),
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
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier
                    .size(30.dp)
                    .padding(2.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 14.sp, fontFamily = Inter, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun BadgesBlock() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BadgeItem(
                    title = "Chama Sustentável",
                    subTitle = "+ 100 XP",
                    iconRes = R.drawable.green_fire
                )
                Spacer(modifier = Modifier.height(8.dp))
                BadgeItem(
                    title = "Guardião da Água",
                    subTitle = "+ 100 XP",
                    iconRes = R.drawable.guard
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                PlaceholderBadgeItem()
                Spacer(modifier = Modifier.height(8.dp))
                PlaceholderBadgeItem()
            }
        }
    }
}

// ✅ MUDANÇA 1: Skeleton para Badges
@Composable
fun BadgesBlockSkeleton() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                repeat(2) {
                    PlaceholderBadgeItem()
                    if (it < 1) Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                repeat(2) {
                    PlaceholderBadgeItem()
                    if (it < 1) Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun BadgeItem(title: String, subTitle: String, iconRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
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
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontFamily = Inter, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = subTitle, fontFamily = Inter, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PlaceholderBadgeItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// ✅ MUDANÇA 2 e 3: Toggles com Dialogs e Permission Handler
@Composable
fun SettingsToggleBlock(
    showTipsPopup: Boolean,
    enableWeeklyNotifications: Boolean,
    allowLocation: Boolean,
    onShowTipsPopupChange: (Boolean) -> Unit,
    onEnableNotificationsChange: (Boolean) -> Unit,
    onAllowLocationChange: (Boolean) -> Unit
) {
    var showTipsDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    // ✅ Permission Handler
    val locationPermissionState = rememberLocationPermissionState(
        onPermissionGranted = { onAllowLocationChange(true) },
        onPermissionDenied = { onAllowLocationChange(false) }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        ToggleItem(
            title = "🎬 Pop-up de dicas extras",
            isChecked = showTipsPopup,
            onClick = { showTipsDialog = true }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)

        ToggleItem(
            title = "🔔 Notificações semanais",
            isChecked = enableWeeklyNotifications,
            onClick = { showNotificationsDialog = true }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)

        ToggleItem(
            title = "📍 Permitir localização atual",
            isChecked = allowLocation,
            onClick = { showLocationDialog = true }
        )
    }

    // ✅ Dialogs
    if (showTipsDialog) {
        SettingDialog(
            title = "Dicas Extras",
            message = "Deseja receber pop-ups com dicas extras sobre sustentabilidade?",
            currentState = showTipsPopup,
            onConfirm = {
                onShowTipsPopupChange(it)
                showTipsDialog = false
            },
            onDismiss = { showTipsDialog = false }
        )
    }

    if (showNotificationsDialog) {
        SettingDialog(
            title = "Notificações Semanais",
            message = "Deseja receber notificações semanais sobre seus progressos e desafios?",
            currentState = enableWeeklyNotifications,
            onConfirm = {
                onEnableNotificationsChange(it)
                showNotificationsDialog = false
            },
            onDismiss = { showNotificationsDialog = false }
        )
    }

    if (showLocationDialog) {
        SettingDialog(
            title = "Localização",
            message = "Permitir que o app acesse sua localização?",
            currentState = allowLocation,
            onConfirm = { enabled ->
                if (enabled && !locationPermissionState.hasPermission) {
                    locationPermissionState.requestPermission()
                } else {
                    onAllowLocationChange(enabled)
                }
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }
}

@Composable
fun ToggleItem(title: String, isChecked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(
                    if (isChecked) DarkGrayBlue else Color.DarkGray.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .padding(7.dp)
        ) {
            Text(text = title, fontFamily = Inter, fontSize = 16.sp, color = Color.White)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onClick() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5ED88B),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

// ✅ Dialog atraente
@Composable
fun SettingDialog(
    title: String,
    message: String,
    currentState: Boolean,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = message,
                fontFamily = Inter,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = { onConfirm(false) },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.DarkGray.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Não", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { onConfirm(true) },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color(0xFF5ED88B)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sim", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview
@Composable
private fun ProfilePreview() {
    ProfileScreen(onSignOut = {}, onNavigateToSettings = {})
}