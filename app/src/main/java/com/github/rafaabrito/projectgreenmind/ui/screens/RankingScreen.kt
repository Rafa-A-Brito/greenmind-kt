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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.rafaabrito.projectgreenmind.ui.components.RankingItem
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.data.model.UserRanking
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import com.github.rafaabrito.projectgreenmind.ui.theme.*
import com.github.rafaabrito.projectgreenmind.ui.viewModel.MainViewModel
import com.github.rafaabrito.projectgreenmind.ui.viewModel.RankingViewModel

// Mapeia o ScoreEntity do Room para o objeto de UI UserRanking
fun ScoreEntity.toUserRanking(index: Int): UserRanking {
    val mockNames = listOf(
        "Sarah Gomes",
        "Alex Bocchero",
        "Guilherme Fé",
        "Joãozinho Paz",
        "Maria Silva",
        "Carlos Santos",
        "Ana Paula",
        "Roberto Lima",
        "Juliana Costa",
        "Pedro Alves"
    )
    val mockAvatars = listOf(
        R.drawable.avatar_v1,
        R.drawable.avatar_v2,
        R.drawable.avatar_v3,
        R.drawable.avatar_v4,
        R.drawable.avatar_v5,
        R.drawable.avatar_v1,
        R.drawable.avatar_v2,
        R.drawable.avatar_v3,
        R.drawable.avatar_v4,
        R.drawable.avatar_v5
    )

    val rankIndex = index.coerceIn(0, mockNames.size - 1)

    return UserRanking(
        name = mockNames[rankIndex],
        points = this.totalScore,
        avatarRes = mockAvatars[rankIndex]
    )
}

@Composable
fun RankingScreen(
    onNavigateToTasks: () -> Unit = {},
    viewModel: RankingViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel() //
) {
    val scores by viewModel.rankingUsers.collectAsState()
    val userState by mainViewModel.userState.collectAsStateWithLifecycle()

    val isLoading = userState.isLoading
    val currentUserXP = userState.userXP
    val currentUserName = userState.user?.name ?: "Você"
    val currentUserPhoto = userState.photoUrl

    // ✅ Mapeia ScoreEntity para UserRanking
    val rankingUsers = scores.mapIndexed { index, scoreEntity ->
        scoreEntity.toUserRanking(index)
    }

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                                .background(DarkGrayBlue, CircleShape)
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

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1CCCC))
                    .clickable { onNavigateToTasks() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Ir para Desafios",
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        CurrentUserRankCard(
            userName = currentUserName,
            userPhotoUrl = currentUserPhoto,
            userXP = currentUserXP,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Skeleton Loading
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LowGreen),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                items(5) { index ->
                    RankingSkeletonItem()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else if (rankingUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum ranker ainda.\nSeja o primeiro!",
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Lista de Ranking
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LowGreen),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                itemsIndexed(rankingUsers) { index, user ->
                    RankingItem(rank = index + 1, user = user)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CurrentUserRankCard(
    userName: String,
    userPhotoUrl: String?,
    userXP: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SaturedGreen, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isLoading) MediumGray else Color.White)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    color = Color.White
                )
            } else if (userPhotoUrl != null) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = "Foto do usuário",
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.image_person_error)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuário",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Informações do Usuário
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (isLoading) "Carregando..." else "$userXP XP",
                fontSize = 14.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Image(
            painter = painterResource(R.drawable.trophy),
            contentDescription = "Troféu",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun RankingSkeletonItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MediumGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número do Rank (Skeleton)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MinimumGray, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Avatar (Skeleton)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MinimumGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Nome e Pontos (Skeleton)
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .background(MinimumGray, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
                    .background(MinimumGray, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Preview
@Composable
private fun RankingScreenPreview() {
    RankingScreen()
}