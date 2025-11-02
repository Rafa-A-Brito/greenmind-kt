package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LightShadeGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumBlack
import com.github.rafaabrito.projectgreenmind.ui.theme.RobotoMono
import com.github.rafaabrito.projectgreenmind.ui.theme.SeafomGreen

@Composable
fun TopBarComponent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightShadeGreen, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Logo do App
                Image(
                    modifier = Modifier.size(45.dp)
                        .clip(CircleShape)
                        .background(MediumBlack),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "GreenMind",
                        fontSize = 18.sp,
                        letterSpacing = 2.sp,
                        color = Color.Black,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Inove. Aprenda. Aperfeiçoe",
                        fontSize = 13.sp,
                        color = Color.Black,
                        fontFamily = RobotoMono,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            // Menu Icon
            Box(
                modifier = Modifier
                    .background(SeafomGreen, shape = RoundedCornerShape(5.dp))
                    .size(35.dp)
            ) {
                IconButton( onClick = {}) {
                    Image(
                        modifier = Modifier.size(25.dp),
                        colorFilter = ColorFilter.tint(Color.Black),
                        painter = painterResource(R.drawable.hamburguer_icon),
                        contentDescription = null
                        )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBarComponent()
}