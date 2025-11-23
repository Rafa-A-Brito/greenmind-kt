package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.github.rafaabrito.projectgreenmind.data.model.navdDrawer.CustomDrawerState
import com.github.rafaabrito.projectgreenmind.data.model.navdDrawer.NavigationItem
import com.github.rafaabrito.projectgreenmind.data.model.navdDrawer.isOpened
import com.github.rafaabrito.projectgreenmind.data.model.navdDrawer.opposite
import kotlin.math.roundToInt

@Composable
fun DrawerContainer(
    showTopBar: Boolean = true,
    title: String = "",
    outerPadding: PaddingValues = PaddingValues(0.dp),
    onDrawerStateChange: (Boolean) -> Unit = {},
    userName: String? = null,
    userPhotoUrl: String? = null,
    isLoadingUserData: Boolean = false,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    LaunchedEffect(drawerState) {
        onDrawerStateChange(drawerState.isOpened())
    }

    // Animações iguais à sua implementação original
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density

    val screenWidth = remember {
        derivedStateOf { (configuration.screenWidthDp * density).roundToInt() }
    }

    val offsetValue by remember { derivedStateOf { (screenWidth.value / 4.5).dp } }

    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp,
        label = "drawer_offset"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f,
        label = "drawer_scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (drawerState.isOpened()) {
            CustomDrawer(
                selectedNavigationItem = NavigationItem.Logout,
                onNavigationItemClick = {
                    drawerState = CustomDrawerState.Closed
                },
                onCloseClick = {
                    drawerState = CustomDrawerState.Closed
                },
                userName = userName,
                userPhotoUrl = userPhotoUrl,
                isLoading = isLoadingUserData
            )
        }

        // Coluna que será animada (offset e scale)
        Column(
            modifier = Modifier
                .offset(x = animatedOffset)
                .scale(animatedScale)
                .fillMaxSize()
        ) {
            if (showTopBar) {
                TopBarComponent(
                    onMenuClick = { drawerState = drawerState.opposite() }
                )
            }

            val combinedPadding = PaddingValues(
                bottom = outerPadding.calculateBottomPadding(),
                start = outerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = outerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content(combinedPadding)
            }
        }
    }
}