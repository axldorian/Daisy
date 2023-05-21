package com.daisydev.daisy.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daisydev.daisy.ui.navigation.NavRoute

@Composable
fun BottomNavigation(navController: NavController, snackbarHostState: SnackbarHostState) {
    val items = NavRoute.getBottomNavRoutes()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
        floatingActionButton = { Fab(navController, snackbarHostState) }
    )
}