package com.github.rafaabrito.projectgreenmind.ui.components

import android.net.http.SslCertificate.restoreState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import com.github.rafaabrito.projectgreenmind.ui.theme.ForestGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.GrotesqueGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray

@Composable
fun BottomBarComponent(navController: NavController) {
    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }

    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val selectedIndex = remember(currentRoute) {
        navigationBarItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

        AnimatedNavigationBar(
            modifier = Modifier.height(64.dp),
            selectedIndex = selectedIndex,
            cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
            ballAnimation = Parabolic(tween(300)),
            indentAnimation = Height(tween(300)),
            barColor = GrotesqueGreen,
            ballColor = ForestGreen
        ) {
            navigationBarItems.forEach { item ->
                val isSelected = currentRoute == item.route
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = item.icon,
                        contentDescription = "Bottom Bar Icons",
                        tint = if (isSelected) Color.Black else MinimumGray
                    )
                }
            }
        }
    }
enum class NavigationBarItems(val icon : ImageVector, val route: String){
    House(icon = Icons.Default.Home, route = "home"),
    Local(icon = Icons.Default.LocationOn, route = "eco"),
    Trophy(icon =  Icons.Outlined.Leaderboard, route = "eco_tasks"),
    Community(icon = Icons.Default.Groups, route = "community"),
    Person(icon = Icons.Default.Person, route = "profile")
}
