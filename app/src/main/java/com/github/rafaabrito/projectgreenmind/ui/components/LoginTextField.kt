package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.github.rafaabrito.projectgreenmind.ui.theme.LightBlueWhite

@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    label: String,
    trailing: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true
){

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,

        // Configuração de Teclado
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),

        // Lógica de Senha (Ocultar caracteres)
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,

        label = {
            Text(text = label, style= MaterialTheme.typography.labelMedium)
        },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color(0xFF475569),
            focusedPlaceholderColor = Color.Black,
            unfocusedContainerColor = LightBlueWhite,
            focusedContainerColor = LightBlueWhite,

            ),
        trailingIcon = {
            // Condição para mostrar o botão "Esqueceu a senha?"
            if (trailing.isNotEmpty()) {
                TextButton(
                    onClick = { /* TODO: Ação de recuperação de senha */ },
                    enabled = enabled
                ) {
                    Text(
                        text = trailing,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    )
}