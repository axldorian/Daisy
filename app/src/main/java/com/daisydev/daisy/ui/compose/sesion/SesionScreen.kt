package com.daisydev.daisy.ui.compose.sesion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.sesion.SesionViewModel
import com.daisydev.daisy.ui.navigation.NavRoute
import io.appwrite.models.User

// Pantalla para ver los datos del usuario logueado
@Composable
fun SesionScreen(
    navController: NavController,
    viewModel: SesionViewModel = hiltViewModel()
) {

    val userData by viewModel.userData.observeAsState()
    val isUserLogged by viewModel.isUserLogged.observeAsState(true)
    val isLoading by viewModel.isLoading.observeAsState(true)

    // Si el usuario no esta logueado, lo enviamos a la pantalla de acceso
    if (!isUserLogged) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoute.Access.path) {
                popUpTo(NavRoute.Sesion.path) { inclusive = true }
            }
        }
    } else {
        ShowLoadingOrScreen(viewModel, isLoading, userData)
    }
}

// Mostramos la pantalla de carga o la pantalla de datos del usuario
@Composable
private fun ShowLoadingOrScreen(
    viewModel: SesionViewModel,
    isLoading: Boolean,
    userData: User<Map<String, Any>>?
) {
    var shouldShowLoading by remember { mutableStateOf(true) }

    LaunchedEffect(isLoading) {
        shouldShowLoading = isLoading
    }

    if (shouldShowLoading) {
        viewModel.isLogged()
        LoadingIndicator()
    } else {
        ScreenView(viewModel, userData)
    }
}

// Pantalla de datos del usuario
@Composable
private fun ScreenView(viewModel: SesionViewModel, userData: User<Map<String, Any>>?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Datos personales",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Avatar(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            userData?.name?.first().toString().uppercase()
        )
        Text(
            text = "Usuario: ${userData?.name}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Text(
            text = "Email: ${userData?.email}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            onClick = { viewModel.closeSession() }) {
            Text(text = "Cerrar Sesion")
        }
    }
}

// Genera avatar del usuario
@Composable
private fun Avatar(modifier: Modifier, letter: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = modifier.size(128.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 64.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}