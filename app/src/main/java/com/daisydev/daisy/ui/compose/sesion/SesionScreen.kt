package com.daisydev.daisy.ui.compose.sesion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.sesion.SesionViewModel
import com.daisydev.daisy.ui.navigation.NavRoute

@Composable
fun SesionScreen(
    navController: NavController,
    viewModel: SesionViewModel = hiltViewModel()
) {

    val isUserLogged by viewModel.isUserLogged.observeAsState(true)
    val isLoading by viewModel.isLoading.observeAsState(true)

    // Si el usuario no esta logueado, lo enviamos a la pantalla de acceso
    if (!isUserLogged) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoute.Access.path) {
                popUpTo(NavRoute.Sesion.path) { inclusive = true }
            }
        }
    }

    if (isLoading) {
        viewModel.isLogged()
        LoadingIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopCenter)
        ) {
            Text(
                text = "Sesion Screen",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Button(onClick = { viewModel.closeSession() }) {
                Text(text = "Cerrar Sesion")
            }
        }
    }
}