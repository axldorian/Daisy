package com.daisydev.daisy.ui.compose.blog

// Importamos de models el modelo de BlogEntry
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.daisydev.daisy.R
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.ui.feature.blog.BlogSharedViewModel
import com.daisydev.daisy.util.formatDate

/**
 * Pantalla de entrada de la vista de una publicación del blog
 */
@Composable
fun EntryBlogScreen(
    navController: NavController,
    viewModel: BlogSharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    // Datos de la entrada del blog
    val selected by viewModel.selected.observeAsState()

    AppBarTitle(
        navController = navController,
        viewModel = viewModel,
        infoEntry = selected!!,
        snackbarHostState = snackbarHostState
    )
}

/**
 * Barra de título de la pantalla
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarTitle(
    navController: NavController,
    infoEntry: BlogEntry,
    viewModel: BlogSharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val editImage by viewModel.editImage.observeAsState(false)
    val editContent by viewModel.editContent.observeAsState(false)

    Column() {
        androidx.compose.material3.TopAppBar(
            title = { Text(text = "Entrada del blog") },

            navigationIcon = {
                IconButton(onClick = {
                    if (editImage) {
                        viewModel.setEditImage(false)
                    } else if (editContent) {
                        viewModel.setEditContent(false)
                    } else {
                        viewModel.setIsSelfContent(false)
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        ContentEntryBlog(
            viewModel = viewModel,
            entry = infoEntry,
            snackbarHostState = snackbarHostState,
            editImage = editImage,
            editContent = editContent
        )
    }
}

/**
 * Contenido de la entrada del blog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEntryBlog(
    entry: BlogEntry,
    viewModel: BlogSharedViewModel,
    snackbarHostState: SnackbarHostState,
    editImage: Boolean,
    editContent: Boolean
) {
    val isSelfContent by viewModel.isSelfContent.observeAsState()

    val editImageSuccess by viewModel.editImageSuccess.observeAsState(false)
    val editImageError by viewModel.editImageError.observeAsState(false)

    val editContentSuccess by viewModel.editContentSuccess.observeAsState(false)
    val editContentError by viewModel.editContentError.observeAsState(false)

    // -- Para editar la imagen --
    if (editImageSuccess) {
        viewModel.showSnackbar(snackbarHostState, "Imagen cambiada correctamente")
        viewModel.setEditImageSuccess(false)
    }

    if (editImageError) {
        viewModel.showSnackbar(snackbarHostState, "Error al cambiar la imagen")
        viewModel.setEditImageError(false)
    }

    // -- Para editar el contenido --
    if (editContentSuccess) {
        viewModel.showSnackbar(snackbarHostState, "Contenido actualizado correctamente")
        viewModel.setEditContentSuccess(false)
    }

    if (editContentError) {
        viewModel.showSnackbar(snackbarHostState, "Error al actualizar el contenido")
        viewModel.setEditContentError(false)
    }

    if (editImage) {
        EditImage(viewModel = viewModel)
    } else if (editContent) {
        EditContent(viewModel = viewModel)
    } else {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = entry.entry_title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 20.dp)
                    )

                    if (isSelfContent != null && isSelfContent!!) {
                        TextButton(
                            modifier = Modifier
                                .padding(0.dp)
                                .align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, Color.Transparent),
                            onClick = { viewModel.setEditImage(true) }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit image",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = "Cambiar imagen"
                                )
                            }
                        }
                    }

                    if (entry.entry_image_id.isEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_daisy_bg),
                            contentDescription = "Imagen por defecto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(entry.entry_image_url),
                            contentDescription = "Imagen de la entrada del blog",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
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

                    if (isSelfContent != null && isSelfContent!!) {
                        TextButton(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, Color.Transparent),
                            onClick = { viewModel.setEditContent(true) }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Add post",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = "Editar contenido"
                                )
                            }
                        }
                    }

                    // Mostramos con badges los tags de la entrada del blog, campo plants,
                    // ordenados en un LazyVerticalGrid
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
        }
    }
}