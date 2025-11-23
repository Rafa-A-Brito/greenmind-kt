package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.theme.*
import com.github.rafaabrito.projectgreenmind.ui.viewModel.CommunityMessage
import com.github.rafaabrito.projectgreenmind.ui.viewModel.CommunityViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val messageText by viewModel.messageText.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val currentUserPhoto by viewModel.currentUserPhoto.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }

    val filteredMessages = remember(messages, searchText) {
        if (searchText.isBlank()) {
            messages
        } else {
            messages.filter {
                it.userName.contains(searchText, ignoreCase = true) ||
                        it.message.contains(searchText, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        CommunityTopSection()

        Spacer(modifier = Modifier.height(16.dp))

        CommunitySearchBar(
            searchText = searchText,
            onSearchChange = { searchText = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchText.isNotBlank())
                                "Nenhuma mensagem encontrada"
                            else
                                "Nenhuma mensagem ainda. Seja o primeiro a comentar!",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { message ->
                    // ✅ Detectar se é mensagem do usuário atual
                    val isCurrentUser = message.userName == currentUserName

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                    ) {
                        CommunityMessageCard(
                            userName = message.userName,
                            userPhotoUrl = message.userPhotoUrl,
                            message = message.message,
                            messageId = message.id,
                            isCurrentUser = isCurrentUser,
                            onDeleteMessage = { viewModel.deleteMessage(it) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CommunityUserKeyboard(
            userName = currentUserName,
            userPhotoUrl = currentUserPhoto,
            messageText = messageText,
            isLoading = isLoading,
            onMessageChange = { viewModel.updateMessageText(it) },
            onSendMessage = { viewModel.sendMessage() },
            onTakePhoto = { viewModel.takePhoto() },
            onAttachImage = { viewModel.attachImage() }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun CommunityTopSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGrayViolet, RoundedCornerShape(12.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(35.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.house),
                    contentDescription = "Casa",
                    modifier = Modifier.size(45.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Feed da comunidade",
                fontSize = 18.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CommunitySearchBar(
    searchText: String,
    onSearchChange: (String) -> Unit
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
                        text = "Pesquise comentários...",
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
                    contentDescription = "Limpar pesquisa",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ✅ MUDANÇA 1: Card com cores diferentes para mensagem do usuário
@Composable
fun CommunityMessageCard(
    userName: String,
    userPhotoUrl: String? = null,
    message: String,
    messageId: String,
    isCurrentUser: Boolean = false,
    onDeleteMessage: (String) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val cardBackgroundColor = if (isCurrentUser) Color.DarkGray else SeaGreen
    val contentBackgroundColor = if (isCurrentUser) Color.LightGray else LightWhite

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBackgroundColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D9A3)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userPhotoUrl != null && userPhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "Foto do usuário",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (isCurrentUser) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar mensagem",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(contentBackgroundColor, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Deletar mensagem",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Tem certeza que deseja deletar esta mensagem?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        onDeleteMessage(messageId)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Deletar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CommunityUserKeyboard(
    userName: String?,
    userPhotoUrl: String?,
    messageText: String,
    isLoading: Boolean,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onTakePhoto: () -> Unit,
    onAttachImage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar do usuário
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            if (userPhotoUrl != null && userPhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Campo de texto
        Box(
            modifier = Modifier
                .weight(1f)
                .background(DarkGrayViolet, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de texto
                BasicTextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (messageText.isEmpty()) {
                            Text(
                                text = "Escreva sua mensagem",
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.width(6.dp))

                // ✅ MUDANÇA 2: Botões menores (28dp ao invés de 32dp)
                Row(
                    modifier = Modifier
                        .background(OuterSpace, RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Botão Câmera
                    IconButton(
                        onClick = onTakePhoto,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Câmera",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Botão Anexar
                    IconButton(
                        onClick = onAttachImage,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Anexar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Botão Enviar
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && !isLoading) {
                                onSendMessage()
                            }
                        },
                        modifier = Modifier.size(28.dp),
                        enabled = messageText.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = if (messageText.isNotBlank()) Color(0xFF00D9A3) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    CommunityScreen()
}