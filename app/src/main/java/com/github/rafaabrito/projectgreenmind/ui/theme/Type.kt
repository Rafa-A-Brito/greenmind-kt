package com.github.rafaabrito.projectgreenmind.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R

val Inter = FontFamily(
    listOf(
        Font(resId = R.font.inter_medium, weight = FontWeight.Medium  ),
        Font(resId = R.font.inter_regular, weight = FontWeight.Normal  )
    )
)

val Roboto = FontFamily(
    listOf(
        Font(resId = R.font.roboto_regular, weight = FontWeight.Normal  ),
        Font(resId = R.font.roboto_medium, weight = FontWeight.Medium  ),
        Font(resId = R.font.roboto_bold, weight = FontWeight.Bold  ),
        Font(resId = R.font.roboto_extrabold, weight = FontWeight.ExtraBold)
    )
)

val Micro5 = FontFamily(
    listOf(
        Font(resId = R.font.micro5_regular, weight = FontWeight.Normal  )
    )
)

val OpenSans = FontFamily(
    listOf(
        Font(resId = R.font.opensans_regular, weight = FontWeight.Normal),
        Font(resId = R.font.opensans_bold, weight = FontWeight.Bold)
    )
)
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
)