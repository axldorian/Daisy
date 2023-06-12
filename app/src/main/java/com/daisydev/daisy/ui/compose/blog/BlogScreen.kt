package com.daisydev.daisy.ui.compose.blog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.blog.BlogSharedViewModel
import com.daisydev.daisy.ui.feature.blog.BlogViewModel
import com.daisydev.daisy.ui.navigation.NavRoute


/**
 * Pantalla principal de la sección de blog
 * @param navController controlador de navegación
 * @param sharedViewModel ViewModel compartido entre las pantallas de blog
 * @param viewModel ViewModel de la pantalla
 */
@Composable
fun BlogScreen(
    navController: NavController,
    sharedViewModel: BlogSharedViewModel,
    viewModel: BlogViewModel = hiltViewModel()
) {

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
            sharedViewModel,
            viewModel,
            isSessionLoading
        )
    }
}

/**
 * Función que muestra el loading o la pantalla del blog
 * @param navController controlador de navegación
 * @param sharedViewModel ViewModel compartido entre las pantallas de blog
 * @param viewModel ViewModel de la pantalla
 * @param isSessionLoading variable que indica si se está cargando la sesión
 */
@Composable
private fun ShowLoadingOrScreen(
    navController: NavController,
    sharedViewModel: BlogSharedViewModel,
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
            InicioBlogScreen(
                navController = navController,
                viewModel = viewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
}

/**
 * Pantalla principal de la sección de blog
 * @param navController controlador de navegación
 * @param sharedViewModel ViewModel compartido entre las pantallas de blog
 * @param viewModel ViewModel de la pantalla
 */
@Composable
fun InicioBlogScreen(
    navController: NavController,
    viewModel: BlogViewModel,
    sharedViewModel: BlogSharedViewModel
) {
    val tabs = listOf("Comunidad", "Mis entradas")
    val selectedTabIndex by viewModel.selectedTabIndex.observeAsState()

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
            tabs = tabs, selectedTabIndex = selectedTabIndex!!,
            navController = navController,
            response = response,
            loading = isFirstLoading,
            viewModel = viewModel,
            sharedViewModel = sharedViewModel
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
    selectedTabIndex: Int,
    navController: NavController,
    response: List<BlogEntry>?,
    loading: Boolean,
    viewModel: BlogViewModel,
    sharedViewModel: BlogSharedViewModel
) {
    // Tabs de la página
    MaterialTheme() {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(text = title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        if (selectedTabIndex != index) {
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

                        viewModel.setSelectedTabIndex(index)
                    })
            }
        }
        // Contenido de la página
        BlogContent(
            selectedTabIndex = selectedTabIndex,
            navController = navController,
            response = response,
            loading = loading,
            viewModel = viewModel,
            sharedViewModel = sharedViewModel
        )
    }
}

// Función que se encarga de mostrar el contenido de la página
// en función de la pestaña seleccionada
@Composable
private fun BlogContent(
    selectedTabIndex: Int,
    navController: NavController,
    response: List<BlogEntry>?,
    loading: Boolean,
    viewModel: BlogViewModel,
    sharedViewModel: BlogSharedViewModel
) {
    when (selectedTabIndex) {
        0 -> BlogCommunity(
            navController = navController,
            response = response,
            firstLoading = loading,
            viewModel = viewModel,
            sharedViewModel = sharedViewModel
        )

        1 -> BlogMyPosts(
            navController = navController,
            viewModel = viewModel,
            response = response,
            sharedViewModel = sharedViewModel
        )
    }
}