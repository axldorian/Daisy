package com.daisydev.daisy.ui.compose.blog

// Importamos de models el modelo de BlogEntry
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.ui.feature.blog.BlogViewModel
import com.daisydev.daisy.util.formatDate

@Composable
fun EntryBlogScreen(navController: NavController, viewModel: BlogViewModel) {
    // Datos de la entrada del blog
    val selected by viewModel.selected.observeAsState()

    AppBarTitle(navController = navController, infoEntry = selected!!)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarTitle(navController: NavController, infoEntry: BlogEntry) {
    /*    val infoEntry : BlogEntry = BlogEntry(
            "6482d1a97a4fdeb12aae",
            "647a1828df7b78f0dfb1",
            "64668e42ab469f0dcf8d",
            convertStringToDate("2021-10-10T00:00:00.000Z"),
            convertStringToDate("2022-10-10T00:00:00.000Z"),
            "646d976bb7b1bee78f6e",
            "Maggys",
            "Para la hora de dormir",
            "La manzanilla y otras plantas han sido utilizadas durante siglos debido a sus " +
                    "beneficios para promover un sueño saludable y reparador. Estas plantas contienen " +
                    "compuestos naturales que ayudan a relajar el cuerpo y la mente, lo que facilita " +
                    "conciliar el sueño y mejorar la calidad del descanso. A continuación, te presento " +
                    "algunos beneficios específicos de la manzanilla y otras plantas para la hora " +
                    "de dormir.",
            "null",
            true,
            listOf("Manzanilla", "Valeriana", "Lavanda"),
            listOf("Dolor de cabeza", "Dolor de estómago", "Dolor de garganta")
        )*/

    Column() {
        androidx.compose.material3.TopAppBar(
            title = { Text(text = infoEntry.entry_title) },

            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        LazyColumn {
            item { ContentEntryBlog(entry = infoEntry) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEntryBlog(entry: BlogEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (entry.entry_image_id.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_daisy_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            /*Image(
               painter = painterResource(id = R.drawable.ic_daisy_bg),
               contentDescription = null,
               modifier = Modifier
                   .fillMaxWidth()
                   .height(200.dp),
               contentScale = ContentScale.Crop
           )*/
        }

        Row() {
            Text(
                text = "Usuario: ",
                modifier = Modifier
                    .padding(top = 20.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = entry.name_user,
                modifier = Modifier
                    .padding(top = 20.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Row() {
            Text(
                text = "Publicado: ",
                modifier = Modifier
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatDate(entry.createdAt),
                modifier = Modifier
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }


        // Mostramos con chip los tags de la entrada del blog, campo plants, ordenados en un LazyVerticalGrid
        Text(
            text = "Plantas mencionadas:",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
        LazyRow(
            content = {
                items(entry.plants.size) { index ->
                    Badge(
                        modifier = Modifier.padding(2.dp),
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(text = entry.plants[index])
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Mostramos con chip los tags de la entrada del blog, campo symptoms
        Text(
            text = "Síntomas mencionados:",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
        LazyRow(
            content = {
                items(entry.symptoms.size) { index ->
                    Badge(
                        modifier = Modifier.padding(2.dp),
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(text = entry.symptoms[index])
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )


        // Se muestra el contenido de la entrada del blog, campo entry_content, cuando
        // se menciona una planta, se muestra en negrita

        Text(
            text = entry.entry_content, modifier = Modifier.padding(
                top = 16.dp, bottom = 16.dp
            ), textAlign = TextAlign.Justify
        )


    }
}