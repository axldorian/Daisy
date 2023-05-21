package com.daisydev.daisy.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.daisydev.daisy.R
import kotlinx.coroutines.launch

@Composable
fun Fab(navController: NavController, snackbarHostState: SnackbarHostState) {
    // Solo necesaria para mostrar el snackbar, no es necesaria para el reconocimiento
    val scope = rememberCoroutineScope()

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onClick = {
        // TODO: Implementar screen de reconocimiento

        // muestra un snack bar con el mensaje "Reconocimiento"
        scope.launch {
            snackbarHostState.showSnackbar(
                message =
                "La funcionalidad de reconocimiento estará disponible en futuras versiones",
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }) {
        Icon(
            painterResource(id = R.drawable.ic_search),
            contentDescription = "Reconocimiento",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}