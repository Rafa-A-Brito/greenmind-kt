package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit // <--- Adicionado
) {
    // Aqui você deve usar o 'onSignOut' no botão de logout da tela.
    Column {
        Text("Tela de Perfil")
        // Exemplo:
        Button(onClick = onSignOut) {
            Text("Sair (Sign Out)")
        }
    }
}