package com.daisydev.daisy.ui.feature.reconocimiento

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.models.DataPlant
import com.daisydev.daisy.repository.local.SessionDataStore
import com.daisydev.daisy.repository.remote.AppWriteRepository
import com.daisydev.daisy.util.convertImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de reconocimiento de plantas
 * @property appWriteRepository Repositorio para acceder a los datos de la aplicación
 */
@HiltViewModel
class ReconocimientoViewModel
@Inject constructor(
    private val appWriteRepository: AppWriteRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // -- Para sesion --

    private val _isUserLogged = MutableLiveData<Boolean>()
    val isUserLogged: LiveData<Boolean> = _isUserLogged

    private val _isSessionLoading = MutableLiveData<Boolean>()
    val isSessionLoading: LiveData<Boolean> = _isSessionLoading

    // Función para verificar si el usuario esta logueado
    fun isLogged() {
        viewModelScope.launch {
            val userData = sessionDataStore.getSession()

            // Si el id del usuario no esta vacio, entonces esta logueado
            _isUserLogged.value = userData.id.isNotEmpty()

            _isSessionLoading.value = false
        }
    }

    // -- Para reconocimiento de plantas --

    private val _response = MutableLiveData<List<DataPlant>>()
    val response: LiveData<List<DataPlant>> = _response

    private val _showResponse = MutableLiveData<Boolean>()
    val showResponse: LiveData<Boolean> = _showResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _imageConverted = MutableLiveData<java.io.File>()
    val imageConverted: LiveData<java.io.File> = _imageConverted

    // Función para analizar la imagen
    fun analyzeImage(context: Context, image: ImageProxy, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _imageConverted.value = convertImage(context, image)
                val uploaded = appWriteRepository.uploadImage(_imageConverted.value!!)
                _response.value = appWriteRepository.recognizeImage(uploaded.id)
                _showResponse.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbar(snackbarHostState, message = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para ocultar la respuesta
    fun hideResponse() {
        _imageConverted.value = java.io.File("")
        _showResponse.value = false
    }

    // Función para mostrar el snackbar de error
    fun showSnackbar(snackbarHostState: SnackbarHostState, message: String? = null) {
        val msg = message ?: "Error al reconocer la imagen, intente de nuevo"
        viewModelScope.launch {
            snackbarHostState
                .showSnackbar(message = msg)
        }
    }
}