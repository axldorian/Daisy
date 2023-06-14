package com.daisydev.daisy.ui.compose.blog

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.blog.BlogSharedViewModel

@Composable
fun EditImage(viewModel: BlogSharedViewModel) {
    val newImageUri by viewModel.newImageUri.observeAsState(null)
    val saveImageEnabled by viewModel.saveImageEnabled.observeAsState(false)
    val isEditImageLoading by viewModel.isEditImageLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.setNewImageUri(null)
    }

    // Lanzador de la galería para obtener la imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uriList ->
        uriList?.let {
            viewModel.setNewImageUri(it)
        }
    }

    if (isEditImageLoading) {
        Column(Modifier.fillMaxSize()) {
            LoadingIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn {
                item {
                    ImageField(imageUri = newImageUri, galleryLauncher = galleryLauncher) {
                        viewModel.setNewImageUri(null)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CancelButton(viewModel = viewModel)
                SaveButton(saveEnable = saveImageEnabled) {
                    viewModel.onSetEditedImage()
                }
            }
        }
    }
}

// Composable para el campo de la imagen
@Composable
private fun ImageField(
    imageUri: Uri?,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    onImageDeleted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { // Lanza la galería para seleccionar una imagen
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = "Seleccionar imagen")
                }
                Spacer(modifier = Modifier.padding(3.dp))

                // Muestra el botón de eliminar imagen si hay una imagen seleccionada
                if (imageUri != null) {
                    IconButton(onClick = {
                        onImageDeleted()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))

            // Muestra la imagen seleccionada, si hay una
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "No hay imagen seleccionada",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

// Composable para el botón de cambiar imagen
@Composable
private fun SaveButton(saveEnable: Boolean, onSaveClicked: () -> Unit) {
    Button(
        modifier = Modifier.width(150.dp),
        onClick = { onSaveClicked() },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        enabled = saveEnable
    ) {
        Text(text = "Cambiar")
    }
}

@Composable
private fun CancelButton(viewModel: BlogSharedViewModel) {
    Button(
        modifier = Modifier.width(150.dp),
        onClick = { viewModel.setEditImage(false) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
        ),
        enabled = true
    ) {
        Text(text = "Cancelar")
    }
}