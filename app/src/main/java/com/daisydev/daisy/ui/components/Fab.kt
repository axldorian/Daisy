package com.daisydev.daisy.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.ui.navigation.NavRoute

@Composable
fun Fab(navController: NavController) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onClick = {
        navController.navigate(NavRoute.Camera.path)
    }) {
        Icon(
            painterResource(id = R.drawable.ic_search),
            contentDescription = "Reconocimiento",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}