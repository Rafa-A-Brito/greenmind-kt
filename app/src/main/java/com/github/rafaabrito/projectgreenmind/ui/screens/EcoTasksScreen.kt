package com.github.rafaabrito.projectgreenmind.ui.screens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.components.BottomBarComponent
import com.github.rafaabrito.projectgreenmind.ui.components.TopBarComponent
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayViolet
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray
import com.github.rafaabrito.projectgreenmind.ui.theme.PaleGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.SaturedGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.SlightlyGreen
private var R_DRAWABLE_CHALLENGES = 1
private var R_DRAWABLE_RECYCLE_ICON = 2
private var R_DRAWABLE_BUCKET_ICON = 3
private var R_DRAWABLE_LAMP_ICON = 4
private var R_DRAWABLE_TIPS_IMAGE = 5
// Data Class para modelar uma Tarefa
data class EcoTask(
    val title: String,
    val xp: String,
    val duration: String,
    val iconResId: Int, // Placeholder para R.drawable.*
    val progress: Float
)
private val mockTasks = listOf(
    EcoTask(
        title = "Descarte 3kg de recicláveis",
        xp = "350 XP",
        duration = "3 meses",
        iconResId = R_DRAWABLE_RECYCLE_ICON,
        progress = 0.75f
    ),
    EcoTask(
        title = "Economize 15L de água na semana",
        xp = "110 XP",
        duration = "até Domingo",
        iconResId = R_DRAWABLE_BUCKET_ICON,
        progress = 0.90f
    ),
    EcoTask(
        title = "Troque 6 lâmpadas para LED",
        xp = "110 XP",
        duration = "4 dias",
        iconResId = R_DRAWABLE_LAMP_ICON,
        progress = 0.20f
    )
)
@Composable
fun EcoTasksScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
        ) {
        Spacer(modifier = Modifier.height(16.dp))
        EcoTasksTopSection()
        Spacer(modifier = Modifier.height(16.dp))
        EcoTasksSearchBar()
        Spacer(modifier = Modifier.height(16.dp))

        // Adicionado o Container das Tarefas (LazyColumn)
        EcoTasksContainer()

        Spacer(modifier = Modifier.height(16.dp))
        // Card de Dicas
        TipsTasks()
        }
}

@Composable
fun EcoTasksTopSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGrayViolet, RoundedCornerShape(12.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
    )
    {
        Row(
            modifier = Modifier
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.challenges),
                    contentDescription = "Desafios",
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desafios Semanais",
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EcoTasksSearchBar() {
    var searchText by remember { mutableStateOf("") }

    val barHeight = 50.dp
    val cornerRadius = 28.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MinimumGray)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone de Busca (Leading Icon)
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Texto de Busca
        Text(
            text = "Pesquise ou filtre os desafios ...",
            color = Color.White,
            fontSize = 15.sp,
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        // Ícone de Filtro
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // Ação de clique para abrir o filtro
                }
        )
    }
}

@Composable
fun EcoTasksContainer() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mockTasks) { task ->
            EcoTasksCard(
                taskTitle = task.title,
                taskProgress = task.progress,
                iconResId = task.iconResId,
                xp = task.xp,
                duration = task.duration
            )
        }
    }
}

@Composable
fun EcoTasksCard(
    taskTitle: String,
    taskProgress: Float,
    iconResId: Int,
    xp: String,
    duration: String
) {
    Box(
        Modifier
            .background(SaturedGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(2.dp)
    ){
        Column(
            Modifier
                .background(Color.White, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SaturedGreen)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(iconResId),
                        contentDescription = "Ícone da Tarefa",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = taskTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Row {
                        Text(
                            text = "$xp - $duration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = { /* Ação de Concluir */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaturedGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "Concluir", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SaturedGreen.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(taskProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SaturedGreen)
                )
            }
        }
    }
}

@Composable
fun TipsTasks(){
    Box(
        Modifier
            .background(PaleGreen, RoundedCornerShape(10.dp)) // Fundo verde claro/pálido
            .fillMaxWidth()
            .padding(2.dp) // Borda verde pálida
    ){
        Row(
            Modifier
                .background(Color.White, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título "Dicas"
                Box(
                    modifier = Modifier
                        .background(PaleGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Dicas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Texto da Dica
                Text(
                    text = "Doe ou reaproveite roupas e objetos em vez de descartá-los.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.Black
                )
            }

            Spacer(Modifier.width(16.dp))

            // Imagem da Dica (Placeholder)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(R.drawable.contribution_clothes),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun EcoTasksPreview() {
    EcoTasksScreen()
}