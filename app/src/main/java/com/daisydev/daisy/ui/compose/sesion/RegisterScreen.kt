package com.daisydev.daisy.ui.compose.sesion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.sesion.RegisterViewModel
import com.daisydev.daisy.ui.navigation.NavRoute

// Pantalla de registro
@Composable
fun RegisterScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Register(Modifier.align(Alignment.Center), snackbarHostState, navController, viewModel)
    }
}

// Contenedor de la pantalla de registro
@Composable
private fun Register(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    viewModel: RegisterViewModel
) {

    val user by viewModel.user.observeAsState(initial = "")
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val conditionsChecked by viewModel.conditionsChecked.observeAsState(initial = false)
    val registerEnable by viewModel.registerEnable.observeAsState(initial = false)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val registerSuccess by viewModel.registerSuccess.observeAsState(initial = false)
    val showError by viewModel.showError.observeAsState(initial = false)

    if (showError) {
        LaunchedEffect(Unit) {
            viewModel.showSnackbar(snackbarHostState)
        }
    }

    if (registerSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoute.Sesion.path)
        }
    } else if (isLoading) {
        LoadingIndicator()
    } else {
        Column(modifier = modifier) {
            RegisterHeader(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
            UserField(user) { viewModel.onRegisterChanged(it, email, password, conditionsChecked) }
            Spacer(modifier = Modifier.padding(4.dp))
            EmailField(email) { viewModel.onRegisterChanged(user, it, password, conditionsChecked) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) {
                viewModel.onRegisterChanged(
                    user,
                    email,
                    it,
                    conditionsChecked
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            ConditionsCheckbox(Modifier.align(Alignment.CenterHorizontally), conditionsChecked) {
                viewModel.onRegisterChanged(
                    user,
                    email,
                    password,
                    it
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            RegisterButton(registerEnable) { viewModel.onRegisterSelected() }
        }
    }
}

// Botón de registro
@Composable
private fun RegisterButton(registerEnable: Boolean, onRegisterSelected: () -> Unit) {
    Button(
        onClick = { onRegisterSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        enabled = registerEnable
    ) {
        Text(text = "Registrarse")
    }
}

// Checkbox de las condiciones de uso
@Composable
private fun ConditionsCheckbox(
    modifier: Modifier,
    conditionsChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    val annotatedString = buildAnnotatedString {
        append("Acepto las ")
        pushStringAnnotation(
            tag = "LINK",
            annotation = "Condiciones de uso"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("condiciones de uso")
        }
        pop()
        append(" de la aplicación")
    }

    Row(modifier = modifier.padding(10.dp)) {
        Checkbox(checked = conditionsChecked, onCheckedChange = { onCheckedChange(it) })
        ClickableText(
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyMedium,
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "LINK",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    showDialog.value = true
                }
            }
        )
    }

    if (showDialog.value) {
        // Composable que representa el modal
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .fillMaxHeight(0.95f),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Box {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(0.95f)
                            .align(Alignment.TopStart)
                    ) {
                        item {
                            Text(
                                text = "Condiciones de uso",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                text = stringResource(id = R.string.conditions),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            showDialog.value = false
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

// Campo de texto para la contraseña
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

// Campo de texto para el correo
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

// Campo de texto para el usuario
@Composable
private fun UserField(user: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = user,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Usuario"
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                painter = painterResource(id = R.drawable.ic_sesion),
                contentDescription = "Contraseña"
            )
        }
    )
}

// Cabecera de la pantalla de registro
@Composable
private fun RegisterHeader(modifier: Modifier) {
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
            text = "Registrate",
            style = MaterialTheme.typography.titleLarge
        )
    }
}