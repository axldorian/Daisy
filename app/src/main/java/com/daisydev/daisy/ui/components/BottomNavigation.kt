package com.daisydev.daisy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daisydev.daisy.ui.navigation.NavRoute

@Composable
fun BottomNavigation(navController: NavController) {
    val items = NavRoute.getBottomNavRoutes()
    val fullScreenPaths = NavRoute.getFullScreenPaths()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var bottomAppBarVisible by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        bottomAppBarVisible = destination.route !in fullScreenPaths
    }

    AnimatedVisibility(visible = bottomAppBarVisible) {
        BottomAppBar(
            modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            containerColor = MaterialTheme.colorScheme.surface,
            actions = {
                items.forEach { item ->
                    IconButton(
                        onClick = {
                            if (currentRoute != item.path) {
                                navController.navigate(item.path)
                            }
                        }) {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            },
            floatingActionButton = { Fab(navController) }
        )
    }
}