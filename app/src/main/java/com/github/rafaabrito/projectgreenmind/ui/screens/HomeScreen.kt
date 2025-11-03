package com.github.rafaabrito.projectgreenmind.ui.screens


import android.media.Image
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.github.rafaabrito.projectgreenmind.ui.components.TopBarComponent
import com.github.rafaabrito.projectgreenmind.ui.theme.GrotesqueGreen
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import com.exyte.animatednavbar.utils.noRippleClickable
import com.github.rafaabrito.projectgreenmind.ui.theme.ForestGreen
import com.github.rafaabrito.projectgreenmind.ui.theme.MinimumGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
    var selectedIndex by remember { mutableIntStateOf(0) }
    Surface(
        modifier = Modifier.fillMaxSize(),

    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .padding(15.dp),
            topBar = {
                TopBarComponent()
            },
            bottomBar = {
                AnimatedNavigationBar(
                    modifier = Modifier.height(64.dp),
                    selectedIndex = selectedIndex,
                    cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                    ballAnimation = Parabolic(tween(300)),
                    indentAnimation =  Height(tween(300)),
                    barColor = GrotesqueGreen,
                    ballColor = ForestGreen
                    ){
                    navigationBarItems.forEach { items ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .noRippleClickable { selectedIndex = items.ordinal },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = items.icon,
                                contentDescription = "Bottom Bar Icons",
                                tint = if(selectedIndex == items.ordinal) Color.Black
                                else MinimumGray
                                )
                        }
                    }
                }
            }
        )
        {
            paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {

            }
        }
    }
}

enum class NavigationBarItems(val icon : ImageVector){
    House(icon = Icons.Default.Home),
    Local(icon = Icons.Default.LocationOn),
    Trophy(icon =  Icons.Outlined.Leaderboard),
    Community(icon = Icons.Default.Groups),
    Person(icon = Icons.Default.Person)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }){ onClick()
        }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}