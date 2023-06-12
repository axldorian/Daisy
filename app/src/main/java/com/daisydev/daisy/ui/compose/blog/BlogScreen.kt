package com.daisydev.daisy.ui.compose.blog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.blog.BlogViewModel
import com.daisydev.daisy.ui.navigation.NavRoute


// Pantalla del blog
@Composable
fun BlogScreen(navController: NavController, viewModel: BlogViewModel) {

    // Para sesión
    val isUserLogged by viewModel.isUserLogged.observeAsState(true)
    val isSessionLoading by viewModel.isSessionLoading.observeAsState(true)

    // Si no hay sesión, se redirige a la pantalla de acceso
    if (!isUserLogged) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoute.Access.path) {
                popUpTo(NavRoute.Sesion.path) { inclusive = true }
            }
        }
    } else {
        ShowLoadingOrScreen(
            navController,
            viewModel,
            isSessionLoading
        )
    }


}

@Composable
private fun ShowLoadingOrScreen(
    navController: NavController,
    viewModel: BlogViewModel,
    isSessionLoading: Boolean
) {

    // Variable para mostrar el loading
    var shouldShowLoading by remember { mutableStateOf(true) }

    // Esta variable se actualiza cuando isSessionLoading cambia
    LaunchedEffect(isSessionLoading) {
        shouldShowLoading = isSessionLoading
    }

    // Mostrar loading o pantalla de reconocimiento
    if (shouldShowLoading) {
        viewModel.isLogged()
        LoadingIndicator()
    } else {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            InicioBlogScreen(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun InicioBlogScreen(navController: NavController, viewModel: BlogViewModel) {
    val tabs = listOf("Comunidad", "Mis entradas")
    val selectedTabIndex by remember { mutableStateOf(0) }

    val response by viewModel.response.observeAsState()
    val isFirstLoading by viewModel.isFirstLoading.observeAsState(true)


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
        BlogTabs(
            tabs = tabs, selectedTabIndex = selectedTabIndex,
            navController = navController,
            response = response,
            loading = isFirstLoading,
            viewModel = viewModel
        )
    }


}

// Función que se encarga de mostrar el título de la página
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
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
private fun BlogTabs(
    tabs: List<String> = listOf(),
    selectedTabIndex: Int = 0,
    navController: NavController,
    response: List<BlogEntry>?,
    loading: Boolean,
    viewModel: BlogViewModel
) {
    var tabIndex: Int by remember { mutableStateOf(selectedTabIndex) }
    // Tabs de la página
    MaterialTheme() {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(text = title) },
                    selected = tabIndex == index,
                    onClick = {
                        if (tabIndex != index) {
                            when (index) {
                                0 -> {
                                    viewModel.setSearchText("")
                                    viewModel.setIsContentLoading()
                                }

                                1 -> {
                                    viewModel.setIsSelfLoading()
                                }
                            }
                        }

                        tabIndex = index
                    })
            }
        }
        // Contenido de la página
        BlogContent(
            selectedTabIndex = tabIndex,
            navController = navController,
            response = response,
            loading = loading,
            viewModel = viewModel
        )
    }
}

// Función que se encarga de mostrar el contenido de la página en función de la pestaña seleccionada
@Composable
private fun BlogContent(
    selectedTabIndex: Int = 0,
    navController: NavController,
    response: List<BlogEntry>?,
    loading: Boolean,
    viewModel: BlogViewModel
) {
    when (selectedTabIndex) {
        0 -> BlogCommunity(
            navController = navController,
            response = response,
            firstLoading = loading,
            viewModel = viewModel
        )

        1 -> BlogMyPosts(viewModel = viewModel, response = response)
    }
}

// Funciones que se encargan de mostrar el contenido de cada pestaña
@Composable
private fun BlogCommunity(
    navController: NavController,
    response: List<BlogEntry>?,
    firstLoading: Boolean,
    viewModel: BlogViewModel,
) {
    val isContentLoading by viewModel.isContentLoading.observeAsState(false)

    // Información de las 10 entradas más recientes de la comunidad JSON
    Column {
        BlogSearch(viewModel = viewModel)
        Text(
            text = "Listado",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.padding(15.dp)
        )
        // Lista de entradas
        // Carga de los datos
        if (firstLoading || isContentLoading) {
            if (firstLoading) {
                viewModel.getInitialBlogEntries()
            } else {
                viewModel.getFilteredBlogEntries()
            }

            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                if (response!!.isNotEmpty()) {
                    response.map { blogEntry ->
                        item {
                            CardEntrada(
                                navController = navController,
                                blogEntry = blogEntry,
                                viewModel = viewModel
                            )
                        }
                    }
                } else {
                    item { Text(text = "Sin entradas del blog") }
                }
            }
        }
    }

}

@Composable
fun BlogMyPosts(viewModel: BlogViewModel, response: List<BlogEntry>?) {
    val isSelfLoading by viewModel.isSelfLoading.observeAsState()

    if (isSelfLoading!!) {
        viewModel.getSelfBlogEntries()
        LoadingIndicator()
    } else {
        Text(text = "Mis entradas")
    }

}

// Funcion que se encarga de manejar el buscador de la página
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogSearch(viewModel: BlogViewModel) {
    val textActual by viewModel.searchText.observeAsState("")
    val clickedSearch = remember { mutableStateOf(false) }
    val sugestionsPlants = listOf("Canela", "Menta", "Rosas")
    val sugestionsSymptoms = listOf("Dolor de cabeza", "Dolor de estómago", "Dolor de garganta")

    SearchBar(
        modifier = Modifier
            .padding(15.dp),
        leadingIcon = {
            IconButton(
                onClick = {
                    clickedSearch.value = false
                    viewModel.setIsContentLoading()
                },
                content = { Icon(Icons.Default.Search, contentDescription = null) },
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    clickedSearch.value = false
                    viewModel.setSearchText("")
                    viewModel.setIsContentLoading()
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
                                        /*textActual = sugestionsPlants[items]*/
                                        viewModel.setSearchText(sugestionsPlants[items])
                                        clickedSearch.value = false
                                        viewModel.setIsContentLoading()
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

                        Text(
                            text = "Síntomas",
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
                                        /*textActual = sugestionsSymptoms[items]*/
                                        viewModel.setSearchText(sugestionsSymptoms[items])
                                        clickedSearch.value = false
                                        viewModel.setIsContentLoading()
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
        onQueryChange = { viewModel.setSearchText(it) },
        query = textActual,
        onSearch = {
            clickedSearch.value = false
            viewModel.setIsContentLoading()
        },
        placeholder = { Text(text = "Buscar") },
    )

}

// Función que se encarga de mostrar el contenido de la página, tarjetas con información de las entradas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEntrada(navController: NavController, blogEntry: BlogEntry, viewModel: BlogViewModel) {

    val textContent =
        if (blogEntry.entry_content.length > 36)
            "${blogEntry.entry_content.subSequence(0..35)}..."
        else
            blogEntry.entry_content

    Column() {
        ListItem(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    enabled = true,
                    onClick = {
                        // Impresión de prueba
                        viewModel.setSelectBlogEntry(blogEntry)
                        navController.navigate(NavRoute.EntryBlog.path)
                    }
                ),
            overlineContent = { Text(blogEntry.name_user) },
            supportingContent = { Text(textContent) },
            headlineContent = { Text(blogEntry.entry_title) },
            leadingContent = {
                Avatar(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    blogEntry.name_user.first().toString().uppercase()
                )
            },

            )
    }
}


@Composable
private fun Avatar(modifier: Modifier, letter: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = modifier.size(40.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}