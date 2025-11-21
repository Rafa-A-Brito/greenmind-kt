package com.github.rafaabrito.projectgreenmind.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
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
import com.github.rafaabrito.projectgreenmind.ui.components.RankingItem
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.data.model.mockRankingUsers
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayBlue
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.LowGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MediumGray

@Composable
fun RankingScreen(
    onBackClick: () -> Unit
) {
    val users = mockRankingUsers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LowGreen)
            .padding(horizontal = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1CCCC))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                        .align(Alignment.Center)

                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .background(MediumGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .background(DarkGrayBlue,CircleShape)
                            .size(45.dp),
                        painter = painterResource(R.drawable.rank_image),
                        contentDescription = "Ranking",
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ranking Semanal",
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .background(LowGreen)
            ,
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            itemsIndexed(users) { index, user ->
                RankingItem(rank = index + 1, user = user)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun RankingScreenPreview() {
    RankingScreen(onBackClick = {})
}