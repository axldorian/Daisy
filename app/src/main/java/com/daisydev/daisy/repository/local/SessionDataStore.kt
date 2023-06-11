package com.daisydev.daisy.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.daisydev.daisy.models.Session
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Clase que se encarga de guardar y obtener los datos de sesión del usuario
 * en un DataStore.
 * @param context Contexto de la aplicación.
 */
class SessionDataStore @Inject constructor(context: Context) {
    private var dataStore: DataStore<Preferences> = context.sessionDataStore

    // Creamos un companion object para definir las claves de las preferencias
    companion object {
        private val Context.sessionDataStore: DataStore<Preferences>
                by preferencesDataStore("session_data_store")

        // keys
        val ID = stringPreferencesKey("id")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
    }

    // Función para obtener los datos de sesión del usuario
    suspend fun getSession(): Session {
        return try {
            val preferences = dataStore.data.first()
            val id = preferences[ID] ?: ""
            val name = preferences[NAME] ?: ""
            val email = preferences[EMAIL] ?: ""
            Session(id, name, email)
        } catch (e: Exception) {
            Session("", "", "")
        }
    }

    // Función para guardar los datos de sesión del usuario
    suspend fun saveSession(session: Session) {
        dataStore.edit { preferences ->
            preferences[ID] = session.id
            preferences[NAME] = session.name
            preferences[EMAIL] = session.email
        }
    }

    // Función para borrar los datos de sesión del usuario
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
