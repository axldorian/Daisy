package com.daisydev.daisy.ui.compose.blog

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
/*
import androidx.navigation.NavController
*/


@Composable
fun BlogScreen(/*navController: NavController*/) {

    val tabs = listOf("Comunidad", "Mis entradas")
    val selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        // Titulo de la página centrado en la parte superior
        Card( colors = CardDefaults.cardColors(),
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

@Composable
fun BlogTabs(tabs : List<String> = listOf(), selectedTabIndex : Int = 0){
    var tabIndex: Int by remember { mutableStateOf(selectedTabIndex) }
    // Tabs de la página
    MaterialTheme() {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed{index, title ->
                Tab(text = { Text(text = title)},
                    selected = tabIndex == index,
                    onClick = { tabIndex = index })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(){
    CenterAlignedTopAppBar(
        title = { Text("Blog", textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    )
}

/*
/// Preview
@Preview
@Composable

fun BlogScreenPreview() {
    BlogScreen(navController = NavController(LocalContext.current))
}*/
