package com.github.rafaabrito.projectgreenmind.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.rafaabrito.projectgreenmind.ui.theme.Black
import com.github.rafaabrito.projectgreenmind.ui.theme.DarkGrayBlue
import com.github.rafaabrito.projectgreenmind.ui.theme.Inter
import com.github.rafaabrito.projectgreenmind.ui.viewModel.SettingsViewModel

private val SeaGreen = Color(0xFF3CB371)
private val Green = Color(0xFF006400)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val userState by viewModel.userSettingsState.collectAsStateWithLifecycle()
    var showPersonalDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    // Observar sucesso no salvamento
    LaunchedEffect(userState.saveSuccess) {
        if (userState.saveSuccess) {
            viewModel.clearSaveSuccess()
        }
    }

    if (userState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SeaGreen)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botão Voltar + Título
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Configurações",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter,
                        color = DarkGrayBlue,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Voltar",
                            tint = DarkGrayBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Card com informações do usuário
            item {
                UserInfoCard(
                    name = userState.name,
                    email = userState.email,
                    hasPassword = userState.hasPassword,
                    isFirebaseAuth = userState.isFirebaseAuth
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Seção Pessoal
            item {
                SettingsItem(
                    title = "Editar Dados Pessoais",
                    icon = Icons.Default.AccountCircle,
                    onClick = { showPersonalDialog = true }
                )
            }

            item {
                HorizontalDivider(color = DarkGrayBlue.copy(alpha = 0.3f))
            }
        }

        if (showPersonalDialog) {
            PersonalDataEditDialog(
                name = userState.name,
                email = userState.email,
                hasPassword = userState.hasPassword,
                isSaving = userState.isSaving,
                onDismiss = { showPersonalDialog = false },
                onSave = { newName, newEmail, newPassword, currentPassword ->
                    viewModel.updateUserData(newName, newEmail, newPassword, currentPassword)
                    showPersonalDialog = false
                }
            )
        }

        // Dialog de Sucesso
        if (userState.saveSuccess) {
            SuccessDialog(
                onDismiss = { viewModel.clearSaveSuccess() }
            )
        }

        // Dialog de Erro
        if (userState.error != null && !userState.isLoading) {
            showErrorDialog = true
        }

        if (showErrorDialog) {
            ErrorDialog(
                error = userState.error ?: "Erro desconhecido",
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}

@Composable
private fun UserInfoCard(
    name: String,
    email: String,
    hasPassword: Boolean,
    isFirebaseAuth: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Nome",
                    tint = SeaGreen,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Nome",
                        fontSize = 12.sp,
                        fontFamily = Inter,
                        color = Color.Gray
                    )
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        color = Black
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = SeaGreen,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Email",
                        fontSize = 12.sp,
                        fontFamily = Inter,
                        color = Color.Gray
                    )
                    Text(
                        text = email,
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        color = Black
                    )
                }
            }

            // Indicador de tipo de autenticação
            if (isFirebaseAuth || hasPassword) {
                HorizontalDivider(color = Color.LightGray)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isFirebaseAuth) Color(0xFFFFB74D) else SeaGreen)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isFirebaseAuth) "Firebase Auth" else "Auth Local",
                            fontSize = 11.sp,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (hasPassword) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Green.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "✓ Com senha",
                                fontSize = 11.sp,
                                fontFamily = Inter,
                                color = Green
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SeaGreen,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = Inter,
            color = Black
        )
    }
}

@Composable
private fun PersonalDataEditDialog(
    name: String,
    email: String,
    hasPassword: Boolean,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String?, String?) -> Unit
) {
    var editedName by remember { mutableStateOf(name) }
    var editedEmail by remember { mutableStateOf(email) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordFields by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Validação em tempo real
    fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "Nome não pode estar vazio"
            value.length < 3 -> "Nome deve ter pelo menos 3 caracteres"
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> "Email não pode estar vazio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Email inválido"
            else -> null
        }
    }

    fun validateNewPassword(value: String): String? {
        if (!showPasswordFields || value.isEmpty()) return null
        return when {
            value.length < 6 -> "Senha deve ter pelo menos 6 caracteres"
            else -> null
        }
    }

    fun validateConfirmPassword(value: String): String? {
        if (!showPasswordFields || value.isEmpty()) return null
        return when {
            value != newPassword -> "Senhas não coincidem"
            else -> null
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Text(
                "Editar Dados Pessoais",
                fontWeight = FontWeight.Bold,
                fontFamily = Inter,
                color = DarkGrayBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo Nome
                OutlinedTextField(
                    value = editedName,
                    onValueChange = {
                        editedName = it
                        nameError = validateName(it)
                    },
                    label = { Text("Nome Completo", fontFamily = Inter) },
                    singleLine = true,
                    enabled = !isSaving,
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SeaGreen,
                        unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
                        focusedLabelColor = SeaGreen,
                        errorBorderColor = Color.Red
                    ),
                    supportingText = nameError?.let { error ->
                        { Text(text = error, color = Color.Red, fontSize = 12.sp, fontFamily = Inter) }
                    }
                )

                // Campo Email
                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = {
                        editedEmail = it
                        emailError = validateEmail(it)
                    },
                    label = { Text("Email", fontFamily = Inter) },
                    singleLine = true,
                    enabled = !isSaving,
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SeaGreen,
                        unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
                        focusedLabelColor = SeaGreen,
                        errorBorderColor = Color.Red
                    ),
                    supportingText = emailError?.let { error ->
                        { Text(text = error, color = Color.Red, fontSize = 12.sp, fontFamily = Inter) }
                    }
                )

                // Toggle para mostrar campos de senha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPasswordFields = !showPasswordFields }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hasPassword) "Alterar senha" else "Criar senha",
                        fontFamily = Inter,
                        fontSize = 15.sp,
                        color = SeaGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = showPasswordFields,
                        onCheckedChange = { showPasswordFields = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SeaGreen,
                            checkedThumbColor = Color.White
                        )
                    )
                }

                // Campos de senha (mostrados condicionalmente)
                if (showPasswordFields) {
                    if (hasPassword) {
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                currentPasswordError = if (it.isEmpty()) "Senha atual obrigatória" else null
                            },
                            label = { Text("Senha Atual", fontFamily = Inter) },
                            singleLine = true,
                            enabled = !isSaving,
                            isError = currentPasswordError != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SeaGreen,
                                unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red
                            ),
                            supportingText = currentPasswordError?.let { error ->
                                { Text(text = error, color = Color.Red, fontSize = 12.sp, fontFamily = Inter) }
                            }
                        )
                    }

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = validateNewPassword(it)
                        },
                        label = { Text("Nova Senha", fontFamily = Inter) },
                        singleLine = true,
                        enabled = !isSaving,
                        isError = newPasswordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeaGreen,
                            unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red
                        ),
                        supportingText = newPasswordError?.let { error ->
                            { Text(text = error, color = Color.Red, fontSize = 12.sp, fontFamily = Inter) }
                        }
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = validateConfirmPassword(it)
                        },
                        label = { Text("Confirmar Nova Senha", fontFamily = Inter) },
                        singleLine = true,
                        enabled = !isSaving,
                        isError = confirmPasswordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeaGreen,
                            unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red
                        ),
                        supportingText = confirmPasswordError?.let { error ->
                            { Text(text = error, color = Color.Red, fontSize = 12.sp, fontFamily = Inter) }
                        }
                    )
                }

                // Mensagem de info
                if (!isSaving) {
                    Text(
                        text = "✓ As alterações serão salvas imediatamente",
                        fontSize = 13.sp,
                        fontFamily = Inter,
                        color = SeaGreen,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalNameError = validateName(editedName)
                    val finalEmailError = validateEmail(editedEmail)

                    if (finalNameError == null && finalEmailError == null) {
                        val passwordToSave = if (showPasswordFields && newPassword.isNotEmpty()) newPassword else null
                        val currentPwd = if (showPasswordFields && hasPassword) currentPassword else null
                        onSave(editedName, editedEmail, passwordToSave, currentPwd)
                    } else {
                        nameError = finalNameError
                        emailError = finalEmailError
                    }
                },
                enabled = !isSaving && nameError == null && emailError == null &&
                        (!showPasswordFields || (newPasswordError == null && confirmPasswordError == null)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SeaGreen,
                    disabledContainerColor = Color.Gray
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvando...", fontFamily = Inter)
                } else {
                    Icon(Icons.Default.Done, contentDescription = "Salvar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salvar", fontFamily = Inter)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = DarkGrayBlue)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cancelar", color = DarkGrayBlue, fontFamily = Inter)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = Inter) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SeaGreen,
            unfocusedBorderColor = DarkGrayBlue.copy(alpha = 0.5f),
            focusedLabelColor = SeaGreen
        )
    )
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Done,
                contentDescription = "Sucesso",
                tint = Green,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "✓ Atualizado com Sucesso!",
                color = Green,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "Seus dados foram salvos e atualizados com sucesso.",
                color = DarkGrayBlue,
                fontFamily = Inter,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SeaGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("OK", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Erro",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Erro ao Atualizar",
                color = Color.Red,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = error,
                color = DarkGrayBlue,
                fontFamily = Inter,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fechar", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    SettingsScreen()
}