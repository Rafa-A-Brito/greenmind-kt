package com.github.rafaabrito.projectgreenmind.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayBlue
import com.github.rafaabrito.projectgreenmind.ui.theme.Green
import com.github.rafaabrito.projectgreenmind.ui.theme.GreenCyanLight
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.OutGreen

@Composable
fun AboutProjectScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título Principal
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Voltar",
                        tint = Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Título Principal
                Text(
                    text = "Sobre o Projeto",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Seção Integrantes
        item {
            SectionCard(
                icon = Icons.Default.EmojiPeople,
                title = "Integrante do Projeto",
                content = {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        TeamMember("Rafael Brito")
                    }
                }
            )
        }

        // Seção Objetivo
        item {
            SectionCard(
                icon = Icons.Default.FlagCircle,
                title = "Objetivo",
                content = {

                    Text(
                        text = buildAnnotatedString {
                            append("O ")
                            withStyle(
                                style = SpanStyle(
                                    color = OutGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("GreenMind")
                            }
                            append(" visa:\n\n")

                            append("• ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Conscientização Ambiental: ")
                            }
                            append("Promover o entendimento sobre práticas sustentáveis.\n")

                            append("• ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Educação Prática e Gamificação: ")
                            }
                            append("Tornar o aprendizado mais dinâmico.\n")

                            append("• ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Hábitos Sustentáveis: ")
                            }
                            append("Incentivar escolhas diárias mais responsáveis.\n")

                            append("• ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Descarte Correto: ")
                            }
                            append("Facilitar o acesso a informações e ecopontos\n")

                            append("• ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Engajamento Social: ")
                            }
                            append("Motivar a participação ativa da comunidade.\n")
                        },
                        fontSize = 15.sp,
                        fontFamily = Inter,
                        lineHeight = 22.sp,
                        color = Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            )
        }

        // Seção ODS
        item {
            SectionCard(
                icon = Icons.Default.Public,
                title = "Objetivos de Desenvolvimento Sustentável (ODS)",
                content = {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        ODSItem(
                            number = "4",
                            title = "Educação de Qualidade",
                            description = "Garantir educação inclusiva e de qualidade para todos."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ODSItem(
                            number = "11",
                            title = "Cidades e Comunidades Sustentáveis",
                            description = "Tornar cidades e assentamentos inclusivos, seguros e sustentáveis."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ODSItem(
                            number = "12",
                            title = "Consumo e Produção Responsáveis",
                            description = "Assegurar padrões sustentáveis de consumo e produção."
                        )
                    }
                }
            )
        }

        // Seção Redes Sociais
        item {
            SectionCard(
                icon = Icons.Default.School,
                title = "Conecte-se Conosco",
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialMediaButton(
                            icon = Icons.Default.Public,
                            label = "@app_greenmind",
                            url = "https://instagram.com/app_greenmind",
                            backgroundColor = Color(0xFFE1306C)
                        ) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/app_greenmind"))
                            context.startActivity(intent)
                        }

                        SocialMediaButton(
                            icon = Icons.Default.Public,
                            label = "GitHub - bit.ly/greenmind-kt",
                            url = "https://bit.ly/greenmind-kt",
                            backgroundColor = Color(0xFF333333)
                        ) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bit.ly/greenmind-kt"))
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }

        // Espaçamento final
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenCyanLight.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OutGreen,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = DarkGrayBlue
                )
            }
            content()
        }
    }
}

@Composable
private fun TeamMember(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(OutGreen, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            fontSize = 16.sp,
            fontFamily = Inter,
            color = Black
        )
    }
}

@Composable
private fun ODSItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(OutGreen, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Inter
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                color = Black,
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontFamily = Inter,
                color = Black.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SocialMediaButton(
    icon: ImageVector,
    label: String,
    url: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Inter,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutProjectPreview() {
    AboutProjectScreen()
}