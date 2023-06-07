package com.daisydev.daisy.ui.compose.sintomas

import android.graphics.Rect
import android.util.Log
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
//OpenAI
import androidx.compose.runtime.rememberCoroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import okhttp3.Headers.Companion.toHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.concurrent.TimeUnit
import java.util.Properties
import android.content.Context
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext

data class Message(val name: String, val body: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SintomasScreen(navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val sintomas by viewModel.sintomas.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val sampleData by viewModel.sampleData.observeAsState(emptyArray())
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardOpen by keyboardAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                busqueda(searchText, context) { plantMessages ->
                                    coroutineScope.launch(Dispatchers.Main) {
                                        viewModel.setSampleData(plantMessages)
                                    }
                                }
                            }
                            keyboardController?.hide()
                        }
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
                    coroutineScope.launch(Dispatchers.IO) {
                        busqueda(searchText, context) { plantMessages ->
                            coroutineScope.launch(Dispatchers.Main) {
                                viewModel.setSampleData(plantMessages)
                            }
                        }
                    }
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
            Conversation(messages = sampleData)
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

suspend fun busqueda(value: String, context: Context, onComplete: (Array<Message>) -> Unit) {
    try {
        Log.d("TAG", "Entro a la función con $value") // Imprime el mensaje en el Logcat
        val properties = Properties()
        val inputStream = context.assets.open("api.properties")
        properties.load(inputStream)
        val apiKey = properties.getProperty("API_KEY")
        val endpoint = "https://api.openai.com"
        val prompt = "Solo como ejemplo necesito plantas curativas para $value, dame la respuesta en JSON siguiendo la siguiente idea de formato que contendrá las plantas:" +
                "{\"plantas\": [{\"nombre\": \"nombre de la planta\", \"nombre_cientifico\": \"nombre cientifico de la planta\", " +
                "\"usos\": \"usos medicinales de la planta\", \"propiedades_curativas\": \"propiedades curativas de la planta\"," +
                "\"url_imagen\": \"una url de una imagen de la planta\"}, {\"aqui lo mismo para la siguiente planta y así sucesivamente\"}]}:"

        val maxTokens = 800

        val url = "$endpoint/v1/chat/completions"
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer $apiKey"
        )
        val requestBody = JSONObject()
            .put(
                "messages",
                JSONArray().put(JSONObject().put("role", "system").put("content", prompt))
            )
            .put("max_tokens", maxTokens)
            .put("model", "gpt-3.5-turbo")
            .toString()
        val mediaType = "application/json".toMediaType()
        val request = Request.Builder()
            .url(url)
            .headers(headers.toHeaders())
            .post(requestBody.toRequestBody(mediaType))
            .build()

        withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS) // Establece el tiempo de espera de conexión a 30 segundos
                .readTimeout(40, TimeUnit.SECONDS) // Establece el tiempo de espera de lectura a 30 segundos
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("TAG", "Respuesta: $responseData")
                    // Procesar y manejar la lista de plantas curativas
                    val responseJson = JSONObject(responseData)
                    val choicesArray = responseJson.getJSONArray("choices")

                    if (choicesArray.length() > 0) {
                        val messageObject = choicesArray.getJSONObject(0).getJSONObject("message")
                        val content = messageObject.getString("content")

                        // Extraer la parte del JSON que contiene las plantas
                        val startIndex = content.indexOf('[')
                        val endIndex = content.lastIndexOf(']')
                        val plantasJson = content.substring(startIndex, endIndex + 1)

                        val plantasArray = JSONArray(plantasJson)

                        val plantMessages = mutableListOf<Message>()

                        for (i in 0 until plantasArray.length()) {
                            val plantObject = plantasArray.getJSONObject(i)
                            val name = plantObject.getString("nombre")
                            val scientificName = plantObject.getString("nombre_cientifico")
                            val uses = plantObject.getString("usos")
                            val healingProperties = plantObject.getString("propiedades_curativas")
                            val url = plantObject.getString("url_imagen")

                            val message = Message(
                                name,
                                "$healingProperties"
                            )
                            plantMessages.add(message)
                        }

                        // Llamar a la función onComplete y pasar la lista de plantMessages
                        onComplete(plantMessages.toTypedArray())
                    } else {
                        // Manejar el caso en el que no haya opciones disponibles
                        Log.e("TAG", "No se encontraron opciones en la respuesta")
                    }
                } else {
                    // Manejar el caso en el que la respuesta no sea exitosa
                    val statusCode = response.code
                    val errorBody = response.body?.string()
                    Log.e("TAG", "Código de estado: $statusCode")
                    Log.e("TAG", "Cuerpo del error: $errorBody")
                }
            }
        }
    } catch (e: Exception) {
        // Manejar cualquier excepción que ocurra durante la búsqueda
        Log.e("TAG", "Error en la búsqueda: ${e.message}", e)
    }
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