package com.daisydev.daisy.ui.compose.seguimiento

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private val Context.dataStore by preferencesDataStore("seguimiento_plantas_data_store")

class SeguimientoPlantasDataStore(context: Context) {
    private val dataStore = context.dataStore

    val plantas: Flow<List<Message>> = dataStore.data
        .catch { exception ->
            // Handle read/write exceptions.
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Get the list of plant messages from preferences
            val plantasJson = preferences[PreferencesKeys.PLANTAS] ?: "[]"
            val plantasArray = JSONArray(plantasJson)
            val plantMessages = mutableListOf<Message>()
            for (i in 0 until plantasArray.length()) {
                val plantObject = plantasArray.getJSONObject(i)
                val name = plantObject.getString("nombre")?: ""
                val healingProperties = plantObject.getString("clima")?: ""
                val url = plantObject.getString("url")?: ""
                val cuidados = plantObject.getString("cuidados")?: ""
                val nameC = plantObject.getString("nameC")?: ""
                val message = Message(name, healingProperties,url, nameC, cuidados)
                plantMessages.add(message)
            }
            plantMessages
        }

    suspend fun savePlantas(plantas: List<Message>) {
        val plantasJson = JSONArray().apply {
            for (plant in plantas) {
                val plantObject = JSONObject().apply {
                    put("nombre", plant.name)
                    put("clima", plant.body)
                    put("url", plant.url)
                    put("nameC", plant.nameC)
                    put("cuidados", plant.cuidados)
                }
                put(plantObject)
            }
        }.toString()

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLANTAS] = plantasJson
        }
    }

    private object PreferencesKeys {
        val PLANTAS = stringPreferencesKey("plantas")
    }
}
