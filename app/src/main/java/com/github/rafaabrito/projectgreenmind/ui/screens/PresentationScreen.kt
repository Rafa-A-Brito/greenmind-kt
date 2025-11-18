package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayBlue
import com.github.rafaabrito.projectgreenmind.ui.theme.ForestGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Green
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightGray
import com.github.rafaabrito.projectgreenmind.ui.theme.LimeGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumLightGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumBlack
import com.github.rafaabrito.projectgreenmind.ui.theme.MintLightGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.OpenSans
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.github.rafaabrito.projectgreenmind.ui.theme.ForestDarkGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.LimeGreenDarker

@Composable
fun PresentationScreen(
    onNavigateToHome: () -> Unit,
    onExit: () -> Unit
){
    Surface(
        color = DarkGrayBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ContainerImage()
            Spacer(modifier = Modifier.height(35.dp))
            ContentPresentation()
            Spacer(modifier = Modifier.height(35.dp))
            ContainerBottomText(onNavigateToHome, onExit)
        }
    }
}

@Composable
fun ContainerImage() {

    // Lista de tons de verde
    val colors = listOf(
        Color(0xFF5EDB8C), // Menta Viva
        Color(0xFF55D48D), // Folha Suave
        Color(0xFF3DA16E), // Verde Hortelã
        Color(0xFF35875C), // Chá de Ervas
        Color(0xFF256F4C)  // Verde Floresta
    )

    // Estado para saber qual cor está ativa
    var index by remember { mutableIntStateOf(0) }

    // Anima mudança de cor
    val animatedColor by animateColorAsState(
        targetValue = colors[index],
        animationSpec = tween(durationMillis = 1500),
        label = "shadowColorAnimation"
    )

    // Troca de cor automaticamente
    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            index = (index + 1) % colors.size
        }
    }

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(300.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Box(
            Modifier
                .dropShadow(
                    CircleShape,
                    shadow = Shadow(
                        radius = 15.dp,
                        spread = 8.dp,
                        color = animatedColor,
                        offset = DpOffset(0.dp, 8.dp)
                    )
                ),
        ) {
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
                    .clip(CircleShape)
                    .background(MintLightGreen),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null
            )
        }
    }
}

@Composable
fun ContentPresentation() {
    // Título principal

    Text(
        text = "SEJA BEM-VINDO!",
        fontSize = 25.sp,
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 0.08.sp,
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Texto de introdução

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Inter,
                    fontSize = 18.sp
                )
            ) {
                append("ao aplicativo ")
            }
            withStyle(
                SpanStyle(
                    color = Green, fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            ) {
                append("GREENMIND ")
            }
            withStyle(
                SpanStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Inter,
                    fontSize = 18.sp
                )
            ) {
            append("\uD83C\uDF33 ! Aproveite a experiência de aprendizado e contribuição social.")
             }
        },
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Citação

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 25.dp),
        horizontalArrangement = Arrangement.Start
    ){
        Box(
            Modifier
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .innerShadow(
                    RoundedCornerShape(10.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 2.dp,
                        color = MinimumBlack,
                        offset = DpOffset(2.dp, 4.dp)
                    ),
                )
                .width(220.dp)
                .height(105.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(15.dp),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = "“Aprender é um tesouro que seguirá seu dono em todos os lugares.”",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ContainerBottomText(
    onNavigateToHome: () -> Unit, // Função para ir para Home
    onExit: () -> Unit           // Função para sair do app
) {
    // Mudança de cor "Saída"
    val exitInteractionSource = remember { MutableInteractionSource() }
    val isExitPressed by exitInteractionSource.collectIsPressedAsState()

    val exitTargetColor = if (isExitPressed) ForestDarkGreen else ForestGreen
    val exitAnimatedColor by animateColorAsState(exitTargetColor, label = "ExitButtonColorAnimation")

    // Mudança de cor "Começo"
    val homeInteractionSource = remember { MutableInteractionSource() }
    val isHomePressed by homeInteractionSource.collectIsPressedAsState()

    val homeTargetColor = if (isHomePressed) LimeGreenDarker else LimeGreen
    val homeAnimatedColor by animateColorAsState(homeTargetColor, label = "HomeButtonColorAnimation")

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                        text = buildAnnotatedString{
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontFamily = Inter,
                            fontSize = 22.sp
                        )
                    ){
                        append("Clique em ")
                    }

                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    ){
                        append("Começar")
                    }

                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontFamily = Inter,
                            fontSize = 22.sp
                        )
                    ){
                        append(" para aproveitar essa incrível ferramenta.")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botões "Sair" e "Começar"

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = onExit,
                interactionSource = exitInteractionSource,
                colors = ButtonDefaults.buttonColors(
                    containerColor = exitAnimatedColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.width(135.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("Sair",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = OpenSans)
            }

            Button(
                onClick = onNavigateToHome,
                interactionSource = homeInteractionSource,
                colors = ButtonDefaults.buttonColors(
                    containerColor = homeAnimatedColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.width(140.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("Começar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = OpenSans)
            }
        }
    }
}

@Preview
@Composable
private fun PresentationPreview(
    onNavigateToHome: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    PresentationScreen(onNavigateToHome = onNavigateToHome , onExit = onExit )
}