package com.github.rafaabrito.projectgreenmind.data.model.navdDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val title: String,
    val icon: ImageVector
) {
    Settings(
        title = "Configurações",
        icon = Icons.Default.Settings
    ),
    About(
        title = "Sobre o App",
        icon = Icons.Default.Info
    ),
    Logout(
        title = "Log Out",
        icon = Icons.AutoMirrored.Filled.Logout
    )
}
