package com.daisydev.daisy.ui.compose.blog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.ui.components.LoadingIndicator
import com.daisydev.daisy.ui.feature.blog.BlogSharedViewModel
import com.daisydev.daisy.ui.feature.blog.BlogViewModel
import com.daisydev.daisy.ui.navigation.NavRoute

/**
 * Pantalla de entrada de la vista las publicaciones propias en el blog
 */
@Composable
fun BlogMyPosts(
    viewModel: BlogViewModel,
    response: List<BlogEntry>?,
    navController: NavController,
    sharedViewModel: BlogSharedViewModel
) {
    val isSelfLoading by viewModel.isSelfLoading.observeAsState()

    if (isSelfLoading!!) {
        viewModel.getSelfBlogEntries()
        LoadingIndicator()
    } else {
        Column {
            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 20.dp, end = 35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { /*TODO*/ }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_post_add),
                        contentDescription = "Add post",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp), text = "Crear"
                    )
                }
            }
            Text(
                text = "Listado",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(15.dp)
            )
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
                                viewModel = viewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                } else {
                    item { Text(text = "Sin entradas propias") }
                }
            }
        }
    }
}

// Función que se encarga de mostrar el contenido de la página,
// tarjetas con información de las entradas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardEntrada(
    navController: NavController,
    blogEntry: BlogEntry,
    viewModel: BlogViewModel,
    sharedViewModel: BlogSharedViewModel
) {

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
                        sharedViewModel.setSelectBlogEntry(blogEntry)
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