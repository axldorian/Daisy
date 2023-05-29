package com.daisydev.daisy.ui.compose.sintomas

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.Image
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daisydev.daisy.R

data class Message(val name: String, val body: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SintomasScreen(navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val sintomas by viewModel.sintomas.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardOpen by keyboardAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Sección del buscador
        Box(
            modifier = Modifier
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                .width(350.dp)
                .wrapContentSize(Alignment.TopCenter)
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.outlineVariant, CircleShape),
        ) {
            TextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                textStyle = TextStyle(fontSize = 17.sp),
                leadingIcon = {
                    IconButton(
                        onClick = { busqueda(searchText);keyboardController?.hide()}
                    ) {
                        Icon(Icons.Filled.Search, null, tint = Color.Gray)
                    }
                },
                modifier = Modifier
                    .padding(11.dp)
                    .background(Color.White, CircleShape)
                    .fillMaxWidth(),
                placeholder = { Text(text = "Buscar sintomas") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search, keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    busqueda(searchText)
                }),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.outlineVariant
                ),
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Cuando se realiza la busqueda muestra la lista de las plantas
        if (isKeyboardOpen == Keyboard.Closed) {
            // Sección de sintomas más comunes
            Text(
                text = "Plantas más comunes",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            //lista de elementos favoritos
            val SampleData: Array<Message> = arrayOf(
                Message(
                    "Nombre",
                    "Descripción "
                ),
                Message("Nombre", "Descripción"),
                Message("Nombre", "Descripción"),
                Message("Nombre", "Descripción"),
                Message("Nombre", "Descripción"),
                Message("Nombre", "Descripción"),
                Message("Nombre7", "Descripción"),
                Message("Nombre8", "Descripción")
            )
            Conversation(messages = SampleData)
            Spacer(modifier = Modifier.height(-16.dp))
        }
        if (isSearching) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Muestra la lista de los sintomas
                items(sintomas) { sintoma ->
                    Text(
                        text = "${sintoma.sintoma}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
    return keyboardState
}

fun busqueda(value: String) {
    println("busqueda $value")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageCard(msg: Message) {
    // Add padding around our message
    Row(
        modifier = Modifier
            .padding(all = 10.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20))
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Row() {
                Image(
                    painter = painterResource(R.drawable.ic_daisy_bg),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier
                        // Set image size to 40 dp
                        .size(50.dp)
                        // Clip image to be shaped as a circle
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        .align(Alignment.CenterVertically),
                )
                // Add a horizontal space between the image and the column
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = msg.name,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    // Add a vertical space between the author and message texts
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun Conversation(messages: Array<Message>) {
    LazyColumn(
    ) {
        items(messages) { message ->
            MessageCard(message)
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}