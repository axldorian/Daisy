package com.daisydev.daisy.ui.compose.sesion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.sesion.LoginViewModel
import com.daisydev.daisy.ui.navigation.NavRoute

// Pantalla de inicio de sesión
@Composable
fun LoginScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: LoginViewModel = hiltViewModel()
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), snackbarHostState, navController, viewModel)
    }
}

// Contenido de la pantalla de inicio de sesión
@Composable
private fun Login(
    modifier: Modifier, snackbarHostState: SnackbarHostState,
    navController: NavController, viewModel: LoginViewModel
) {
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val loginEnable by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loginSuccess by viewModel.loginSuccess.observeAsState(initial = false)
    val showError by viewModel.showError.observeAsState(initial = false)

    if (showError) {
        LaunchedEffect(Unit) {
            viewModel.showSnackbar(snackbarHostState)
        }
    }

    if (loginSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoute.Sesion.path)
        }
    } else if (isLoading) {
        LoadingIndicator()
    } else {
        Column(modifier = modifier) {
            LoginHeader(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(24.dp))
            EmailField(email) { viewModel.onLoginChanged(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(email, it) }
            Spacer(modifier = Modifier.padding(16.dp))
            LoginButton(loginEnable) {
                viewModel.onLoginSelected()
            }
        }
    }
}

// Botón de inicio de sesión
@Composable
private fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        enabled = loginEnable
    ) {
        Text(text = "Iniciar sesión")
    }
}

// Entrada de texto para la contraseña
@Composable
private fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Contraseña",
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            val icon =
                if (passwordVisible)
                    painterResource(id = R.drawable.ic_visibility)
                else painterResource(id = R.drawable.ic_visibility_off)
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = icon,
                    contentDescription = "Toggle password visibility",
                )
            }
        }
    )
}

// Entrada de texto para el correo
@Composable
private fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Correo"
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = "Contraseña"
            )
        }
    )
}

// Encabezado de la pantalla de inicio de sesión
@Composable
private fun LoginHeader(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_daisy_no_bg),
            contentDescription = "Header Image",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Daisy",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
