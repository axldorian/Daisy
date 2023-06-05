package com.daisydev.daisy.ui.compose.blog

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daisydev.daisy.ui.theme.md_theme_light_primary
import com.daisydev.daisy.ui.theme.md_theme_light_secondaryContainer

// Pantalla del blog
@Composable
fun BlogScreen(navController: NavController) {

    val tabs = listOf("Comunidad", "Mis entradas")
    val selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        // Titulo de la página centrado en la parte superior
        Card(
            colors = CardDefaults.cardColors(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .wrapContentSize(Alignment.TopCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(0)
        ) {
            TopAppBar()
        }
        BlogTabs(tabs = tabs, selectedTabIndex = selectedTabIndex)
    }
}

// Función que se encarga de mostrar el título de la página
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Blog",
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

// Control de Tabs de la página
@Composable
fun BlogTabs(tabs: List<String> = listOf(), selectedTabIndex: Int = 0) {
    var tabIndex: Int by remember { mutableStateOf(selectedTabIndex) }
    // Tabs de la página
    MaterialTheme() {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(text = title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index })
            }
        }
        // Contenido de la página
        BlogContent(selectedTabIndex = tabIndex)
    }
}

// Función que se encarga de mostrar el contenido de la página en función de la pestaña seleccionada
@Composable
fun BlogContent(selectedTabIndex: Int = 0) {
    when (selectedTabIndex) {
        0 -> BlogCommunity()
        1 -> BlogMyPosts()
    }
}

// Funciones que se encargan de mostrar el contenido de cada pestaña
@Composable
fun BlogCommunity() {
    BlogSearch()
    Text(text = "Comunidad")
}

@Composable
fun BlogMyPosts() {
    Text(text = "Mis entradas")
}

// Funcion que se encarga de manejar el buscador de la página
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogSearch() {
    var textActual by remember { mutableStateOf("") }
    val clickedSearch = remember { mutableStateOf(false) }
    val sugestionsPlants = listOf("Canela", "Menta", "Rosas")
    val sugestionsSymptoms = listOf("Dolor de cabeza", "Dolor de estómago", "Dolor de garganta")

    SearchBar(
        modifier = Modifier
            .padding(15.dp),
        leadingIcon = {
            IconButton(
                onClick = { clickedSearch.value = true },
                content = { Icon(Icons.Default.Search, contentDescription = null) },
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    clickedSearch.value = false
                    textActual = ""
                },
                content = { Icon(Icons.Default.Clear, contentDescription = null) },
                // De acuerdo al estado de la viable oculta el icono
                modifier = if (clickedSearch.value) Modifier else Modifier.width(0.dp),
            )
        },
        colors = SearchBarDefaults.colors(),
        shape = SearchBarDefaults.dockedShape,
        active = clickedSearch.value,
        content = {

            Box(modifier = Modifier.padding(16.dp)) {

                // Agregamos sugerencias de búsqueda
                if (textActual.isEmpty()) {

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Mostramos las sugerencias de búsqueda de la lista de sugerencias de plantas
                        LazyColumn {
                            items(sugestionsPlants.size) { items ->
                                Card(
                                    onClick = {
                                        textActual = sugestionsPlants[items]
                                        clickedSearch.value = false
                                    },
                                    content = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start

                                        ) {
                                            Icon(Icons.Default.Search, contentDescription = null)
                                            Text(
                                                text = sugestionsPlants[items],
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Transparent
                                    ),
                                )
                            }
                        }

                        // Agrega un divisor entre las sugerencias de plantas y las sugerencias de sintomas,
                        // para separarlas junto con un texto que indica que son sugerencias de sintomas

                        Text(
                            text = "Sintomas",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 15.dp, bottom = 8.dp),
                            color = Color.Gray
                        )

                        // Mostramos las sugerencias de búsqueda de la lista de sugerencias de sintomas
                        // mediante un LazyVerticalGrid y AssistChip

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            items(sugestionsSymptoms.size) { items ->
                                AssistChip(
                                    label = { Text(text = sugestionsSymptoms[items]) },
                                    onClick = {
                                        textActual = sugestionsSymptoms[items]
                                        clickedSearch.value = false
                                    },
                                   // Utilizamos los colores predeterminados de AssistChip
                                    /*colors = AssistChipDefaults.elevatedAssistChipColors(
                                        containerColor = md_theme_light_secondaryContainer
                                    ),*/
                                    interactionSource = remember { MutableInteractionSource() },

                                )

                            }
                        }

                    }
                }
            }
        },
        onActiveChange = {
            clickedSearch.value = it
            // S
        },
        onQueryChange = { textActual = it },
        query = textActual,
        onSearch = { /* Handle search action */ },
        placeholder = { Text(text = "Buscar") },
    )

}


/*
/// Preview
@Preview
@Composable

fun BlogScreenPreview() {
    BlogScreen(navController = NavController(LocalContext.current))
}*/
