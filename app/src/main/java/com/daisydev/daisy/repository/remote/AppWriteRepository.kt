package com.daisydev.daisy.repository.remote

import com.daisydev.daisy.models.AltName
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.models.DataPlant
import com.daisydev.daisy.models.toBlogEntry
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.extensions.toJson
import io.appwrite.models.File
import io.appwrite.models.InputFile
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Functions
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para todas las operaciones relacionadas con AppWrite.
 */
@Singleton
class AppWriteRepository @Inject constructor(
    private val client: Client,
    private val dispatcher: CoroutineDispatcher
) {
    // -- Services --

    private val account = Account(client)
    private val databases = Databases(client)
    private val storage = Storage(client)
    private val functions = Functions(client)

    // -- Account --

    // Iniciar sesión con email y contraseña
    suspend fun login(email: String, password: String): Session {
        return withContext(dispatcher) {
            account.createEmailSession(email, password)
        }
    }

    // Registrar usuario con usuario, email y contraseña
    suspend fun register(
        email: String, password: String,
        name: String
    ): User<Map<String, Any>> {
        return withContext(dispatcher) {
            account.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = name
            )
        }
    }

    // Para cerrar sesión
    suspend fun logout() {
        return withContext(dispatcher) {
            account.deleteSession("current")
        }
    }

    // Para obtener la información del usuario
    suspend fun getAccount(): User<Map<String, Any>> {
        return withContext(dispatcher) {
            account.get()
        }
    }

    // Para obtener la sesión actual
    suspend fun isLoggedIn(): Session {
        return withContext(dispatcher) {
            account.getSession("current")
        }
    }

    // -- Storage --

    // Para subir una imagen a AppWrite
    suspend fun uploadImage(image: java.io.File): File {
        return withContext(dispatcher) {
            storage.createFile(
                bucketId = "6476c99ad8636acff4ad",
                fileId = ID.unique(),
                file = InputFile.fromFile(image),
            )
        }
    }

    // -- Functions --

    // Para reconocer una imagen por medio de Google Cloud Vision Y GPT-3.5
    suspend fun recognizeImage(imageId: String): List<DataPlant> {
        return withContext(Dispatchers.IO) {
            // Ejecutar la función
            val execution = functions.createExecution(
                functionId = "64791401c68219857417",
                data = mapOf(
                    "image" to imageId
                ).toJson(),
                async = true
            )

            // Esperar a que la función termine de ejecutarse
            var executionResult = functions.getExecution(
                functionId = "64791401c68219857417",
                executionId = execution.id
            )

            while (executionResult.status != "completed" && executionResult.status != "failed") {
                Thread.sleep(1000)
                executionResult = functions.getExecution(
                    functionId = "64791401c68219857417",
                    executionId = execution.id
                )
            }

            // Obtener el resultado de la función
            val jsonString = executionResult.response

            // Variable para guardar el resultado
            var result = listOf<DataPlant>()

            // Convertir el resultado a una lista de DataPlant
            val jsonArray = JSONArray(jsonString)

            // Si el resultado no está vacío
            if (jsonArray.length() > 0) {
                val arrRange = 0 until jsonArray.length()

                // Convertir el resultado a una lista de DataPlant
                result = arrRange.map { i ->
                    val jsonObject = jsonArray.getJSONObject(i)

                    val altNamesArr = jsonObject.getJSONArray("alt_names")
                    val altNamesRange = 0 until altNamesArr.length()

                    val altNames: List<AltName> = altNamesRange.map {
                        val alt_name = altNamesArr.getJSONObject(it)
                        AltName(
                            name = alt_name.getString("name")
                        )
                    }

                    DataPlant(
                        plant_name = jsonObject.getString("plant_name"),
                        probability = jsonObject.getDouble("probability"),
                        alt_names = altNames
                    )
                }
            }

            // Regresar el resultado
            result
        }
    }

    // -- Databases --

    // Para listar todos los documentos en la base de datos de AppWrite
    suspend fun listDocuments(): List<BlogEntry> {
        return withContext(dispatcher) {
            databases.listDocuments(
                "64668e42ab469f0dcf8d",
                "647a1828df7b78f0dfb1"
            ).documents.map {
                toBlogEntry(it)
            }
        }
    }
}