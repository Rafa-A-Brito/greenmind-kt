package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,

        // Configuração de Teclado
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),

        // Lógica de Senha (Ocultar caracteres)
        visualTransformation = when {
            isPassword && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },

        label = {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color(0xFF475569),
            focusedPlaceholderColor = Color.Black,
            unfocusedContainerColor = LightBlueWhite,
            focusedContainerColor = LightBlueWhite,

            ),
        trailingIcon = {
            if (isPassword) {
                // Lógica para o ícone de visibilidade (Olho)
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Ocultar senha" else "Mostrar senha"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            } else if (keyboardType == KeyboardType.Email) {
                // Lógica para o ícone de Email (Caixa de Email)
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = Color(0xFF475569) // Cor de placeholder
                )
            }
        }
    )
}