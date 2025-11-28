package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.data.model.UserRanking
import com.github.rafaabrito.projectgreenmind.ui.theme.BackLightGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto

@Composable
fun RankingItem(
    rank: Int,
    user: UserRanking
) {
    val background = Color(0xFFF3F3F3) // Fundo do Card
    val rankColor = if (rank <= 3) Color.White else Color.Black // Cor do número

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(45.dp),
            contentAlignment = Alignment.Center
        ) {
            when (rank) {
                1 -> Image(
                    modifier = Modifier
                        .background(BackLightGreen, RoundedCornerShape(10.dp))
                        .size(35.dp)
                    ,
                    painter = painterResource(R.drawable.first_place),
                    contentDescription = "Primeiro Lugar",
                )
                2 -> Image(
                    painter = painterResource(R.drawable.second_place),
                    contentDescription = "Segundo Lugar",
                    modifier = Modifier
                        .background(BackLightGreen, RoundedCornerShape(10.dp))
                        .size(35.dp)
                )
                3 -> Image(
                    painter = painterResource(R.drawable.third_place),
                    contentDescription = "Terceiro Lugar",
                    modifier = Modifier
                        .background(BackLightGreen, RoundedCornerShape(10.dp))
                        .size(35.dp)
                )
                else ->
                    Text(
                        text = rank.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankColor
                        )
                }
          }

        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(user.avatarRes),
                contentDescription = "Avatar de ${user.name}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = user.name,
                fontSize = 16.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }

        Text(
            text = "${user.points} pts",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Preview
@Composable
private fun RankingItemPreview() {
    RankingItem(
        rank = 1,
        user = UserRanking(
            name = "Rafael",
            points = 2500,
            avatarRes = R.drawable.avatar_v1
        )
    )
}