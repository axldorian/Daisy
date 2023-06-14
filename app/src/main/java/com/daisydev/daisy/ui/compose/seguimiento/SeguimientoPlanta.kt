package com.daisydev.daisy.ui.compose.seguimiento

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.daisydev.daisy.ui.navigation.NavRoute
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
//import androidx.compose.material3.ButtonDefaults.TextButtonColors
//import androidx.compose.material3.ButtonDefaults.TextButtonContentColors
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DatePicker
import java.time.LocalDate



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoPlanta(
    navController: NavController,
    name: String,
    nameC: String,
    body: String,
    cuidados: List<String>,
    url: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Información",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Al pulsar el botón se dirige a la pantalla de síntomas
                        navController.navigate(NavRoute.Sintomas.path)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = {
            Card(
                onClick = { /* Do something */ },
                modifier = Modifier
                    .size(width = 400.dp, height = 740.dp)
                    .padding(start = 15.dp, top = 75.dp),
            ) {
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(Modifier.padding(8.dp)) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                val showDialog = remember { mutableStateOf(false) }
                                val titleText = remember { mutableStateOf("Título del recordatorio") }
                                val showDatePicker = remember { mutableStateOf(false) }
                                val selectedDate = remember { mutableStateOf(LocalDate.now()) }

                                Text(text = "Nombre común: $name", modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { showDialog.value = true }
                                ) {
                                    Text(text = "Recordatorio")
                                }

                                if (showDialog.value) {
                                    AlertDialog(
                                        onDismissRequest = { showDialog.value = false },
                                        properties = DialogProperties(
                                            dismissOnClickOutside = false // Opcional: para evitar cerrar el diálogo al hacer clic fuera de él
                                        ),
                                        title = {
                                            Column {
                                                TextField(
                                                    value = titleText.value,
                                                    onValueChange = { titleText.value = it },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = { showDialog.value = false },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.primary
                                                    //backgroundColor = MaterialTheme.colorScheme.background
                                                )
                                            ) {
                                                Text(text = "Guardar")
                                            }
                                        },
                                        dismissButton = {
                                            Button(
                                                onClick = { showDialog.value = false },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.primary
                                                    //backgroundColor = MaterialTheme.colorScheme.background
                                                )
                                            ) {
                                                Text(text = "Cancelar")
                                            }
                                        }
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nombre científico: $nameC",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = nameC,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Clima:",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = body, textAlign = TextAlign.Justify)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Cuidados:",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }

                        cuidados.forEach { cuidado ->
                            item {
                                Text(text = cuidado, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    )
}
