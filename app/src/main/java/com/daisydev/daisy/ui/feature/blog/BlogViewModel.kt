package com.daisydev.daisy.ui.feature.blog

import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.models.BlogDocumentModel
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.models.Session
import com.daisydev.daisy.repository.local.SessionDataStore
import com.daisydev.daisy.repository.remote.AppWriteRepository
import com.daisydev.daisy.util.ExportConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appwrite.models.File
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Blog
 * @property appWriteRepository Repositorio para acceder a los datos de la aplicación
 */
@HiltViewModel
class BlogViewModel
@Inject constructor(
    private val appWriteRepository: AppWriteRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // -- Para sesion --

    private val _userData = MutableLiveData<Session>()

    private val _isUserLogged = MutableLiveData<Boolean>()
    val isUserLogged: LiveData<Boolean> = _isUserLogged

    private val _isSessionLoading = MutableLiveData<Boolean>()
    val isSessionLoading: LiveData<Boolean> = _isSessionLoading

    // Función para verificar si el usuario esta logueado
    fun isLogged() {
        viewModelScope.launch {
            val userData = sessionDataStore.getSession()

            // Si el id del usuario no esta vacio, entonces esta logueado
            if (userData.id.isNotEmpty()) {
                _userData.value = userData
                _isUserLogged.value = true
            } else {
                _isUserLogged.value = false
            }

            _isSessionLoading.value = false
        }
    }

    // -- Para el blog --

    private val _response = MutableLiveData<MutableList<BlogEntry>>()
    val response: LiveData<MutableList<BlogEntry>> = _response

    private val _isFirstLoading = MutableLiveData<Boolean>()
    val isFirstLoading: LiveData<Boolean> = _isFirstLoading

    private val _isContentLoading = MutableLiveData<Boolean>()
    val isContentLoading: LiveData<Boolean> = _isContentLoading

    private val _isSelfLoading = MutableLiveData<Boolean>()
    val isSelfLoading: LiveData<Boolean> = _isSelfLoading

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String> = _searchText

    private val _selectedTabIndex = MutableLiveData<Int>(0)
    val selectedTabIndex: LiveData<Int> = _selectedTabIndex

    // Para la creación de un nuevo blog
    // -- / Variables para la creación de un nuevo blog --
    private val _showNewBlogEntry = MutableLiveData<Boolean>()
    val showNewBlogEntry: LiveData<Boolean> = _showNewBlogEntry

    private val _isNewBlogEntryLoading = MutableLiveData<Boolean>()
    val isNewBlogEntryLoading: LiveData<Boolean> = _isNewBlogEntryLoading

    private val _isNewBlogEntrySuccess = MutableLiveData<Boolean>()
    val isNewBlogEntrySuccess: LiveData<Boolean> = _isNewBlogEntrySuccess

    private val _isNewBlogEntryError = MutableLiveData<Boolean>()
    val isNewBlogEntryError: LiveData<Boolean> = _isNewBlogEntryError

    private val _saveEnable = MutableLiveData<Boolean>()
    val saveEnable: LiveData<Boolean> = _saveEnable

    private val _entryTitle = MutableLiveData<String>()
    val entryTitle: LiveData<String> = _entryTitle

    private val _entryContent = MutableLiveData<String>()
    val entryContent: LiveData<String> = _entryContent

    private val _plants = MutableLiveData<String>()
    val plants: LiveData<String> = _plants

    private val _symptoms = MutableLiveData<String>()
    val symptoms: LiveData<String> = _symptoms

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    // To delete a blog entry
    private val _isDeleteLoading = MutableLiveData<Boolean>()
    val isDeleteLoading: LiveData<Boolean> = _isDeleteLoading

    private val _isDeleteSuccess = MutableLiveData<Boolean>()
    val isDeleteSuccess: LiveData<Boolean> = _isDeleteSuccess

    private val _isDeleteError = MutableLiveData<Boolean>()
    val isDeleteError: LiveData<Boolean> = _isDeleteError

    fun setIsDeleteSuccess(value: Boolean) {
        _isDeleteSuccess.value = value
    }

    fun setIsDeleteError(value: Boolean) {
        _isDeleteError.value = value
    }

    // Función que controla si se ve o no la pantalla de creación de un nuevo blog
    fun setShowNewBlogEntry(show: Boolean) {
        _showNewBlogEntry.value = show

        if (!show) {
            _isNewBlogEntrySuccess.value = false
        }
    }

    // Función para actualizar las variables de la creación de un nuevo blog
    fun onNewBlogEntryChanged(
        entryTitle: String,
        entryContent: String,
        plants: String,
        symptoms: String,
        imageUri: Uri?
    ) {
        _entryTitle.value = entryTitle
        _entryContent.value = entryContent
        _plants.value = plants
        _symptoms.value = symptoms
        _imageUri.value = imageUri
        _saveEnable.value = entryTitle.isNotEmpty() && entryContent.isNotEmpty()
    }

    // Función para guardar un nuevo blog en la base de datos
    fun onSaveNewBlogEntryModel() {
        _isNewBlogEntryLoading.value = true

        viewModelScope.launch {
            try {

                // Variables para la imagen
                val imageUri = _imageUri.value
                var image: File? = null
                var imageId: String? = null
                var imageUrl: String? = null

                // Si la imagen no es nula, entonces se sube a la base de datos
                // y se obtiene la url de la imagen
                if (imageUri != null) {
                    image = appWriteRepository.uploadBlogImage(imageUri)
                    imageId = image.id
                    imageUrl = "${ExportConstants.baseUrl}/storage/buckets/"
                    imageUrl += "${image.bucketId}/files/$imageId/view"
                    imageUrl += "?project=${ExportConstants.projectId}"
                }

                // Se obtienen las plantas y los sintomas
                val plants =
                    if (_plants.value!!.isEmpty())
                        listOf<String>("Ninguna")
                    else
                        _plants.value!!.split(",")
                            .map { it.trim() }
                val symptoms =
                    if (_symptoms.value!!.isEmpty())
                        listOf<String>("Ninguno")
                    else
                        _symptoms.value!!.split(",")
                            .map { it.trim() }

                // Se crea el nuevo blog
                val newBlogEntry = BlogDocumentModel(
                    id_user = _userData.value!!.id,
                    name_user = _userData.value!!.name,
                    entry_title = _entryTitle.value!!,
                    entry_content = _entryContent.value!!,
                    entry_image_id = imageId ?: "",
                    entry_image_url = imageUrl ?: "",
                    posted = true,
                    plants = plants,
                    symptoms = symptoms,
                )

                // Se guarda el nuevo blog en la base de datos y se indica que fue exitoso
                appWriteRepository.createDocument(newBlogEntry)
                _isNewBlogEntrySuccess.value = true
            } catch (e: Exception) {
                // Si hubo un error, se indica que hubo un error
                _isNewBlogEntryError.value = true
            } finally {
                _isNewBlogEntryLoading.value = false
            }
        }
    }

    // Función que establece el index del tab seleccionado
    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }

    // Función que establece la pantalla de carga de contenido de la comunidad
    fun setIsContentLoading() {
        _isContentLoading.value = true
    }

    // Función que establece la pantalla de carga de contenido propio
    fun setIsSelfLoading() {
        _isSelfLoading.value = true
    }

    // Función que establece el texto de búsqueda en la comunidad
    fun setSearchText(text: String) {
        _searchText.value = text
    }

    // Función que obtiene los blogs de la comunidad por primera vez
    fun getInitialBlogEntries() {
        viewModelScope.launch {
            _response.value = appWriteRepository.listDocuments()
            _isFirstLoading.value = false
        }
    }

    // Función que obtiene los blogs propios
    fun getSelfBlogEntries() {
        viewModelScope.launch {
            _response.value = appWriteRepository.listDocumentsOfUser(_userData.value!!.id)
            _isSelfLoading.value = false
        }
    }

    // Función que obtiene los blogs de la comunidad filtrados por el texto de búsqueda
    fun getFilteredBlogEntries() {
        viewModelScope.launch {
            _response.value =
                if (_searchText.value!!.isEmpty())
                    appWriteRepository.listDocuments()
                else
                    appWriteRepository.listDocumentsWithFilter(
                        _searchText.value!!
                    )
            _isContentLoading.value = false
        }
    }

    fun deleteBlogEntry(item: BlogEntry, index: Int) {
        viewModelScope.launch {
            try {
                appWriteRepository.deleteBlogImage(item.entry_image_id)
                appWriteRepository.deleteDocument(item.id)
                _response.value!!.removeAt(index)
                _isDeleteSuccess.value = true
            } catch (e: Exception) {
                Log.d("Error", "Error: ${e.message}")
                _isDeleteError.value = true
            } finally {
                _isDeleteLoading.value = false
            }
        }
    }

    // Función que muestra un mensaje en la pantalla a través de un snackbar
    fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
        viewModelScope.launch {
            snackbarHostState
                .showSnackbar(message = message)
        }
    }
}