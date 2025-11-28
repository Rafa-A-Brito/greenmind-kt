package com.github.rafaabrito.projectgreenmind.data.model
import com.github.rafaabrito.projectgreenmind.R

data class UserRanking(
    val name: String,
    val points: Int,
    val avatarRes: Int // Para simular a imagem do avatar
)

val mockRankingUsers = listOf(
    UserRanking("Sarah Gomes", 4000, R.drawable.avatar_v1),
    UserRanking("Sarah Omero", 3192, R.drawable.avatar_v4),
    UserRanking("Alex Bocchero", 2780, R.drawable.avatar_v2),
    UserRanking("Guilherme Fé", 2420, R.drawable.avatar_v3),
    UserRanking("Joãozinho Paz", 1500, R.drawable.avatar_v5),
).sortedByDescending { it.points }