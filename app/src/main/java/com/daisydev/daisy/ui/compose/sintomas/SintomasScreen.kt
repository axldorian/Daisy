package com.daisydev.daisy.ui.compose.sintomas

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.daisydev.daisy.ui.feature.sintomas.MainViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class Message(
    val name: String,
    val body: String,
    val url: String,
    val nameC: String,
    val uses: String
)


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
    val plantasDataStore = PlantasDataStore(context)
    //sección de busqueda
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Search section
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
                            //Asynchronously call the search function
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
        // Display the list of plants while searching
        if (isKeyboardOpen == Keyboard.Closed) {
            LaunchedEffect(Unit) {
                // Load plantas from DataStore
                val plantMessages = plantasDataStore.plantas.first()
                if (plantMessages.isEmpty()) {
                    // If plantas not available in DataStore, fetch from API and save to DataStore
                    coroutineScope.launch(Dispatchers.IO) {
                        getPlantasComunes(context) { plantMessages ->
                            coroutineScope.launch(Dispatchers.Main) {
                                viewModel.setSampleData(plantMessages)
                                // Save plantas to DataStore
                                plantasDataStore.savePlantas(plantMessages.toList())
                            }
                        }
                    }
                } else {
                    // If plantas available in DataStore, update UI
                    viewModel.setSampleData(plantMessages.toTypedArray())
                }
            }
            // Section of most common symptoms
            Text(
                text = "Plantas más comunes",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            //List of common items
            Conversation(messages = sampleData, navController)
            Spacer(modifier = Modifier.height(-16.dp))
        }
        // Cuando se realiza la busqueda muestra la lista de las plantas
        if (isKeyboardOpen == Keyboard.Opened) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Display the list of symptoms
                items(sintomas) { sintoma ->
                    Text(
                        // Al presionar una opcion se realiza la busqueda y oculta el teclado
                        text = "${sintoma.sintoma}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                keyboardController?.hide()
                                coroutineScope.launch(Dispatchers.IO) {
                                    busqueda("${sintoma.sintoma}", context) { plantMessages ->
                                        coroutineScope.launch(Dispatchers.Main) {
                                            viewModel.setSampleData(plantMessages)
                                        }
                                    }
                                }
                            },
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
        val properties = Properties()
        val inputStream = context.assets.open("api.properties")
        properties.load(inputStream)
        val apiKey = properties.getProperty("API_KEY") //Retrieve the API KEY from api.properties
        val endpoint = "https://api.openai.com"
        val prompt =
            "Solo como ejemplo necesito plantas curativas para $value, dame la respuesta en JSON siguiendo la siguiente idea de formato que contendrá las plantas:" +
                    "{\"plantas\": [{\"nombre\": \"nombre de la planta\", \"nombre_cientifico\": \"nombre cientifico de la planta\", " +
                    "\"usos\": \"usos medicinales de la planta\", \"propiedades_curativas\": \"propiedades curativas de la planta\"," +
                    "\"url_imagen\": \"una url de una imagen de la planta\"}, {\"aqui lo mismo para la siguiente planta y así sucesivamente\"}]}:"

        val maxTokens = 800 //Maximum allowed characters

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
                .connectTimeout(40, TimeUnit.SECONDS) //Set the connection timeout to 30 seconds
                .readTimeout(40, TimeUnit.SECONDS) // Set the read timeout to 30 seconds
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    // Process and handle the list of healing plants
                    val responseJson = JSONObject(responseData)
                    val choicesArray = responseJson.getJSONArray("choices")

                    if (choicesArray.length() > 0) {
                        val messageObject = choicesArray.getJSONObject(0).getJSONObject("message")
                        val content = messageObject.getString("content")

                        // Extract the part of the JSON that contains the plants
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
                            val url = get_URL(context, scientificName)
                            val message = Message(
                                name, "$healingProperties", "$url", "$scientificName", "$uses"
                            )
                            plantMessages.add(message)
                        }

                        // Call the onComplete function and pass the list of plantMessages
                        onComplete(plantMessages.toTypedArray())
                    } else {
                        // Handle the case when no options are available
                        Log.e("TAG", "No se encontraron opciones en la respuesta")
                    }
                } else {
                    // Handle the case when the response is not successful
                    val statusCode = response.code
                    val errorBody = response.body?.string()
                    Log.e("TAG", "Código de estado: $statusCode")
                    Log.e("TAG", "Cuerpo del error: $errorBody")
                }
            }
        }
    } catch (e: Exception) {
        // Handle any exceptions that occur during the search
        Log.e("TAG", "Error en la búsqueda: ${e.message}", e)
    }
}

suspend fun getPlantasComunes(context: Context, onComplete: (Array<Message>) -> Unit) {
    try {
        val properties = Properties()
        val inputStream = context.assets.open("api.properties")
        properties.load(inputStream)
        val apiKey = properties.getProperty("API_KEY")
        val endpoint = "https://api.openai.com"
        val prompt =
            "Solo como ejemplo necesito plantas curativas comunes, dame la respuesta en JSON siguiendo la siguiente idea de formato que contendrá las plantas:" +
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
                .connectTimeout(40, TimeUnit.SECONDS) // Set the connection timeout to 30 seconds
                .readTimeout(40, TimeUnit.SECONDS) // Set the read timeout to 30 seconds
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("TAG", "Respuesta: $responseData")
                    // Process and handle the list of healing plants
                    val responseJson = JSONObject(responseData)
                    val choicesArray = responseJson.getJSONArray("choices")

                    if (choicesArray.length() > 0) {
                        val messageObject = choicesArray.getJSONObject(0).getJSONObject("message")
                        val content = messageObject.getString("content")

                        // Extract the part of the JSON that contains the plants
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
                            val url = get_URL(context, scientificName)

                            val message = Message(
                                name, "$healingProperties", "$url", "$scientificName", "$uses"
                            )
                            plantMessages.add(message)
                        }

                        // Call the onComplete function and pass the list of plantMessages
                        onComplete(plantMessages.toTypedArray())
                    } else {
                        // Handle the case when no options are available
                        Log.e("TAG", "No se encontraron opciones en la respuesta")
                    }
                } else {
                    // Handle the case when the response is not successful
                    val statusCode = response.code
                    val errorBody = response.body?.string()
                    Log.e("TAG", "Código de estado: $statusCode")
                    Log.e("TAG", "Cuerpo del error: $errorBody")
                }
            }
        }
    } catch (e: Exception) {
        // Handle any exceptions that occur during the search
        Log.e("TAG", "Error en la búsqueda plantas comunes: ${e.message}", e)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageCard(msg: Message, navController: NavController) {
    // Add padding around our message
    Row(
        modifier = Modifier
            .padding(all = 10.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20))
            .fillMaxWidth()
            .clickable {
                // se dirije a la pantalla de plantas
                val encodedUrl = URLEncoder.encode(msg.url, StandardCharsets.UTF_8.toString())
                try {
                    navController.navigate(
                        "plantaInfo/${msg.name}/${msg.nameC}/${msg.body}/${msg.uses}/${encodedUrl}"
                    )
                } catch (e: Exception) {
                    println("Error" + e)
                }
            },
    ) {
        Box(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Row() {
                // Imagen de internet
                Image(
                    painter = rememberAsyncImagePainter(msg.url),
                    contentDescription = msg.name,
                    modifier = Modifier
                        // Set image size
                        .size(90.dp)
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
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}

@Composable
fun Conversation(messages: Array<Message>, navController: NavController) {
    LazyColumn(
    ) {
        items(messages) { message ->
            MessageCard(message, navController)
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}

suspend fun get_URL(context: Context, name: String): String? {
    return try {
        val baseUrl = "https://serpapi.com"
        val searchEndpoint = "google_images"
        val query = "Planta medicinal $name"
        val ijn = "0"
        val properties = Properties()
        val inputStream = context.assets.open("api.properties")
        properties.load(inputStream)
        val apiKey = properties.getProperty("S_API_KEY")

        val url = "$baseUrl/search.json?engine=$searchEndpoint&q=$query&ijn=$ijn&api_key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder().build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(responseData, JsonObject::class.java)

                    val imagesResultsArray = jsonObject.getAsJsonArray("images_results")
                    if (imagesResultsArray.size() > 0) {
                        val firstImageResult = imagesResultsArray[0].asJsonObject
                        val thumbnail = firstImageResult.get("thumbnail").asString
                        return@withContext thumbnail // Return the value of the thumbnail
                    } else {
                        Log.d("TAG", "Error en thumbnail")
                        return@withContext null // Return null in case of an error
                    }
                } else {
                    // Handle the case when the response is not successful
                    val statusCode = response.code
                    val errorBody = response.body?.string()
                    Log.e("TAG", "Código de estado URL: $statusCode")
                    Log.e("TAG", "Cuerpo del error URL: $errorBody")
                    return@withContext null // Return null in case of an error
                }
            }
        }
    } catch (e: Exception) {
        // Handle any exceptions that occur during the search
        Log.e("TAG", "Error en la obtención de url: ${e.message}", e)
        null // Return null in case of an error
    }
}


