package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.OutGreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun NoConnectionScreen(
    onRetry: () -> Unit = {}
) {
    var isRetrying by remember { mutableStateOf(false) }

    // ✅ Animação de pulsação para o ícone WiFi
    val infiniteTransition = rememberInfiniteTransition(label = "wifi_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(500)) + scaleIn(tween(500))
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_splash_screen),
                    contentDescription = "Logo GreenMind",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Ícone WiFi cortado
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "Sem conexão",
                modifier = Modifier
                    .size(80.dp)
                    .alpha(alpha),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "Sem Conexão",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mensagem
            Text(
                text = "Você perdeu a conexão com a internet.\nVerifique sua conexão e tente novamente.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Inter,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Tentar Novamente
            Button(
                onClick = {
                    isRetrying = true
                    onRetry()
                    // Reset loading após 2 segundos
                    kotlinx.coroutines.GlobalScope.launch {
                        delay(2000)
                        isRetrying = false
                    }
                },
                enabled = !isRetrying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OutGreen,
                    disabledContainerColor = OutGreen.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isRetrying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tentar Novamente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Dicas expandidas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "💡 Dicas para resolver:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TipItem("• Verifique se o Wi-Fi está ativado")
                TipItem("• Ative os dados móveis")
                TipItem("• Tente desligar e ligar o modo avião")
                TipItem("• Verifique se há sinal de internet")
            }
        }
    }
}

@Composable
private fun TipItem(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontFamily = Inter,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Preview
@Composable
private fun NoConnectionScreenPreview() {
    NoConnectionScreen()
}