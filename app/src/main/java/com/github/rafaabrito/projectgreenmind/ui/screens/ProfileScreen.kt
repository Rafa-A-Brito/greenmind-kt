package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R // Importe R.drawable
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayBlue
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.OutGreen

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            // --- A. Header do Perfil ---
            ProfileHeader(
                userName = "Nome do Usuário",
                userRank = "Herói da Natureza",
                onNavigateToSettings = onNavigateToSettings
            )
        }

        item {
            // --- B. Bloco de Estatísticas (Pontos, Ofensiva, Conclusão) ---
            Spacer(modifier = Modifier.height(24.dp))
            StatsBlock()
        }

        item {
            // --- C. Bloco de Badges / Conquistas ---
            Spacer(modifier = Modifier.height(24.dp))
            BadgesBlock()
        }

        item {
            // --- D. Bloco de Configurações e Notificações (Toggles) ---
            Spacer(modifier = Modifier.height(24.dp))
            SettingsToggleBlock(onSignOut = onSignOut)
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userRank: String,
    onNavigateToSettings: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OutGreen, RoundedCornerShape(15.dp)) // Cor verde do cabeçalho
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color(0xFF5ED88B),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Nome e Classificação
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = userRank, // Ex: "Herói da Natureza"
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        // Botão de Configurações (AÇÃO CLICÁVEL)
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
fun StatsBlock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatsCard(
            title = "Pontos",
            value = "38420 XP",
            iconRes = R.drawable.trophy, // Ícone de Troféu
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatsCard(
            title = "Ofensiva",
            value = "200 dias",
            iconRes = R.drawable.fire, // Ícone de Fogo
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatsCard(
            title = "Conclusão",
            value = "20",
            iconRes = R.drawable.medal_v2, // Ícone de Medalha
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    iconRes: Int,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9)) // Fundo verde claro
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
            Text(text = title, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                    iconRes = R.drawable.green_fire // Ícone de Folha e Fogo
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Badge "Guardião da Água"
                BadgeItem(
                    title = "Guardião da Água",
                    subTitle = "+ 100 XP",
                    iconRes = R.drawable.guard // Ícone de Gota d'água e Pessoa
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Badges de Placeholder à direita
            Column(modifier = Modifier.weight(1f)) {
                PlaceholderBadgeItem()
                Spacer(modifier = Modifier.height(8.dp))
                PlaceholderBadgeItem()
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
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = subTitle, fontSize = 12.sp, color = Color.Gray)
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
        // Placeholder com ícone e linhas (simulando texto)
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

@Composable
fun SettingsToggleBlock(onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0)) // Fundo cinza claro
            .padding(16.dp)
    ) {
        // Toggles (usando um componente simplificado)
        ToggleItem(title = "\uD83C\uDFAC Pop-up de dicas extras ", isChecked = true)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)
        ToggleItem(title = "\uD83D\uDD14 Notificações semanais  ", isChecked = false)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)
        ToggleItem(title = "\uD83D\uDD25 Expor ofensiva a todos ", isChecked = true)

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de Logout (da sua implementação original)
        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sair", color = Color.White)
        }
    }
}

@Composable
fun ToggleItem(title: String, isChecked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f)
                .background(DarkGrayBlue, RoundedCornerShape(8.dp))
                .padding(7.dp)

            ) {
            Text(text = title, fontSize = 16.sp, color = Color.White)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { /* Ação de toggle */ },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5ED88B)
            )
        )
    }
}

@Preview
@Composable
private fun ProfilePreview() {
    ProfileScreen(onSignOut = {}, onNavigateToSettings = {})
}