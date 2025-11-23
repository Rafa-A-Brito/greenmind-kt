package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.*
import com.github.rafaabrito.projectgreenmind.ui.viewModel.EcoTaskUi
import com.github.rafaabrito.projectgreenmind.ui.viewModel.EcoTasksViewModel

@Composable
fun EcoTasksScreen(
    viewModel: EcoTasksViewModel = hiltViewModel(),
    onNavigateToRanking: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.populateTasksDatabase()
    }

    val tasks by viewModel.ecoTasks.collectAsState()
    val userLevelState by viewModel.userLevelState.collectAsState()
    val showCongratulationsDialog by viewModel.showCongratulationsDialog.collectAsState()
    val lastCompletedTask by viewModel.lastCompletedTask.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(tasks, searchText, selectedLevel) {
        tasks.filter { task ->
            val matchesSearch = searchText.isBlank() ||
                    task.title.contains(searchText, ignoreCase = true) ||
                    task.category.contains(searchText, ignoreCase = true)

            val matchesLevel = selectedLevel == null || task.level == selectedLevel

            matchesSearch && matchesLevel
        }
    }

    // Diálogo de parabéns
    if (showCongratulationsDialog && lastCompletedTask != null) {
        CongratulationsDialog(
            task = lastCompletedTask!!,
            userLevel = userLevelState,
            onDismiss = { viewModel.dismissCongratulationsDialog() }
        )
    }

    // Diálogo de filtro
    if (showFilterDialog) {
        FilterDialog(
            currentFilter = selectedLevel,
            onFilterSelected = { level ->
                selectedLevel = level
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de nível
        UserLevelIndicator(userLevelState)

        Spacer(modifier = Modifier.height(16.dp))

        EcoTasksTopSection(
            onNavigateToRanking = onNavigateToRanking,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        EcoTasksSearchBar(
            searchText = searchText,
            onSearchChange = { searchText = it },
            selectedLevel = selectedLevel,
            onFilterClick = { showFilterDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = SaturedGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Carregando desafios...",
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f)) {
                EcoTasksContainer(
                    tasks = filteredTasks,
                    onCompleteTaskAction = viewModel::completeTask
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TipsTasks()
    }
}

@Composable
fun UserLevelIndicator(userLevelState: com.github.rafaabrito.projectgreenmind.ui.viewModel.UserLevelState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SaturedGreen, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Nível ${userLevelState.currentLevel}: ${userLevelState.levelName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Inter
                )
                Text(
                    text = "${userLevelState.currentPoints}/${userLevelState.pointsToNextLevel} XP",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = Inter
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Troféu",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun FilterDialog(
    currentFilter: String?,
    onFilterSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Filtrar por Nível",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilterOption(
                    text = "Todos",
                    isSelected = currentFilter == null,
                    onClick = { onFilterSelected(null) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterOption(
                    text = "Básico",
                    isSelected = currentFilter == "Básico",
                    onClick = { onFilterSelected("Básico") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterOption(
                    text = "Intermediário",
                    isSelected = currentFilter == "Intermediário",
                    onClick = { onFilterSelected("Intermediário") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Fechar", color = SaturedGreen)
                }
            }
        }
    }
}

@Composable
fun FilterOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) SaturedGreen.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) SaturedGreen else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = Inter,
            color = if (isSelected) SaturedGreen else Color.Black
        )
    }
}

@Composable
fun CongratulationsDialog(
    task: EcoTaskUi,
    userLevel: com.github.rafaabrito.projectgreenmind.ui.viewModel.UserLevelState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Troféu",
                    tint = SaturedGreen,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Parabéns! 🎉",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Você completou:",
                    fontSize = 16.sp,
                    fontFamily = Inter,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .background(SaturedGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+${task.scoreValue} XP",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                            color = SaturedGreen
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Nível ${userLevel.currentLevel}: ${userLevel.levelName}",
                            fontSize = 14.sp,
                            fontFamily = Inter,
                            color = Color.Gray
                        )

                        Text(
                            text = "${userLevel.currentPoints}/${userLevel.pointsToNextLevel} XP",
                            fontSize = 12.sp,
                            fontFamily = Inter,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaturedGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continuar",
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EcoTasksTopSection(
    onNavigateToRanking: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MediumGray, RoundedCornerShape(10.dp))
            .padding(horizontal = 15.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp),
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
                    painter = painterResource(R.drawable.challenges),
                    contentDescription = "Desafios",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desafios Ecológicos",
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1CCCC))
                .clickable { onNavigateToRanking() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Voltar",
                tint = Color.Black,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
fun EcoTasksSearchBar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    selectedLevel: String?,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MinimumGray)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 15.sp,
                fontFamily = Roboto
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = "Pesquise tarefas ou categorias...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal
                    )
                }
                innerTextField()
            }
        )

        if (searchText.isNotEmpty()) {
            IconButton(
                onClick = { onSearchChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Limpar busca",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (selectedLevel != null) SaturedGreen else Color.Transparent,
                    CircleShape
                )
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filtrar",
                tint = if (selectedLevel != null) Color.White else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EcoTasksContainer(
    tasks: List<EcoTaskUi>,
    onCompleteTaskAction: (task: EcoTaskUi) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nenhum desafio encontrado",
                    fontSize = 16.sp,
                    fontFamily = Inter,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(tasks, key = { it.taskId }) { task ->
                EcoTasksCard(
                    task = task,
                    onCompleteTask = { onCompleteTaskAction(task) }
                )
            }
        }
    }
}

@Composable
fun EcoTasksCard(
    task: EcoTaskUi,
    onCompleteTask: () -> Unit
) {
    Box(
        Modifier
            .background(SaturedGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Column(
            Modifier
                .background(Color.White, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone da tarefa
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SaturedGreen)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(task.iconResId),
                        contentDescription = "Ícone da Tarefa",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Informações da tarefa
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 15.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge de nível
                        Box(
                            modifier = Modifier
                                .background(
                                    if (task.level == "Básico") PaleGreen else SaturedGreen.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.level,
                                fontSize = 10.sp,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                color = if (task.level == "Básico") SaturedGreen else SaturedGreen
                            )
                        }

                        // Badge de categoria
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.Gray.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.category,
                                fontSize = 10.sp,
                                fontFamily = Inter,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = task.xp,
                        fontSize = 12.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        color = SaturedGreen
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Botão de completar
                Button(
                    onClick = onCompleteTask,
                    enabled = !task.isCompleted,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaturedGreen,
                        disabledContainerColor = Color.Gray
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (task.isCompleted) "✓" else "Concluir",
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Barra de progresso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SaturedGreen.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(task.progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SaturedGreen)
                )
            }
        }
    }
}

@Composable
fun TipsTasks() {
    Box(
        Modifier
            .background(PaleGreen, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row(
            Modifier
                .background(Color.White, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(PaleGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "💡 Dica Eco",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Doe ou reaproveite roupas e objetos em vez de descartá-los.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = Inter,
                    color = Color.Black
                )
            }

            Spacer(Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(R.drawable.contribution_clothes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}