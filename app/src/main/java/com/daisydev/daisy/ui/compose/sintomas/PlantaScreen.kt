package com.daisydev.daisy.ui.compose.sintomas

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.daisydev.daisy.ui.navigation.NavRoute

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantaScreen(
    navController: NavController,
    name: String,
    nameC: String,
    body: String,
    uses: String,
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
                        // Al pulsar el botón se dirije a la pantalla de sintomas
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
                Box(Modifier
                    .fillMaxSize()
                    //.padding(start = 15.dp)
                ) {
                    Column() {
                        Text(
                            text = "Nombre cientifico: ${nameC}",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nombre común: ${name}",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleMedium
                            )
                        // Imagen de internet
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = nameC,
                            modifier = Modifier
                                // Set image size
                                .size(400.dp)
                                .fillMaxHeight(1f),
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            "Propiedades curativas:",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "${uses}",
                            textAlign = TextAlign.Justify
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Usos:",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "${body}",
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        },

        )

}


