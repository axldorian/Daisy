package com.daisydev.daisy.ui.feature.blog

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.models.BlogDocumentModel
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.repository.remote.AppWriteRepository
import com.daisydev.daisy.util.ExportConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel compartido para la pantalla de Blog
 * @property selected BlogEntry el cual se comparte entre BlogScreen y EntryBlog
 */
@HiltViewModel
class BlogSharedViewModel @Inject constructor(
    private val appWriteRepository: AppWriteRepository
) : ViewModel() {
    // El que se comparte entre BlogScreen y EntryBlog
    private val _selected = MutableLiveData<BlogEntry>()
    val selected: LiveData<BlogEntry> = _selected

    // Función para establecer un blogEntry
    fun setSelectBlogEntry(blogEntry: BlogEntry) {
        _selected.value = blogEntry
    }

    // Para saber si el contenido es propio
    private val _isSelfContent = MutableLiveData<Boolean>()
    val isSelfContent: LiveData<Boolean> = _isSelfContent

    // Función para establecer si el contenido es propio
    fun setIsSelfContent(isSelfContent: Boolean) {
        _isSelfContent.value = isSelfContent
    }

    // -- Para editar la imagen --
    private val _editImage = MutableLiveData<Boolean>()
    val editImage: LiveData<Boolean> = _editImage

    private val _newImageUri = MutableLiveData<Uri?>()
    val newImageUri: LiveData<Uri?> = _newImageUri

    private val _saveImageEnabled = MutableLiveData<Boolean>()
    val saveImageEnabled: LiveData<Boolean> = _saveImageEnabled

    private val _isEditImageLoading = MutableLiveData<Boolean>()
    val isEditImageLoading: LiveData<Boolean> = _isEditImageLoading

    private val _editImageSuccess = MutableLiveData<Boolean>()
    val editImageSuccess: LiveData<Boolean> = _editImageSuccess

    private val _editImageError = MutableLiveData<Boolean>()
    val editImageError: LiveData<Boolean> = _editImageError

    // Función para establecer si se muestra la pantalla de editar imagen
    fun setEditImage(editImage: Boolean) {
        _editImage.value = editImage
    }

    // Función para establecer la nueva imagen
    fun setNewImageUri(uri: Uri?) {
        _newImageUri.value = uri
        _saveImageEnabled.value = uri != null
    }

    // Función para establecer el valor que muestra
    // si se pudo guardar la imagen
    fun setEditImageSuccess(value: Boolean) {
        _editImageSuccess.value = value
    }

    // Función para establecer el valor que muestra
    // si hubo un error al guardar la imagen
    fun setEditImageError(value: Boolean) {
        _editImageError.value = value
    }

    // Función para actualizar la imagen
    fun onSetEditedImage() {
        _isEditImageLoading.value = true

        viewModelScope.launch {
            try {
                val docId = _selected.value!!.id

                // Actualizamos la imagen
                val uploaded = appWriteRepository.uploadBlogImage(_newImageUri.value!!)

                var imageUrl = "${ExportConstants.baseUrl}/storage/buckets/"
                imageUrl += "${uploaded.bucketId}/files/${uploaded.id}/view"
                imageUrl += "?project=${ExportConstants.projectId}"

                // Si se subio la imagen, entonces actualizamos el documento
                val newBlogEntry = BlogDocumentModel(
                    id_user = _selected.value!!.id_user,
                    name_user = _selected.value!!.name_user,
                    entry_title = _selected.value!!.entry_title,
                    entry_content = _selected.value!!.entry_content,
                    entry_image_id = uploaded.id,
                    entry_image_url = imageUrl,
                    posted = _selected.value!!.posted,
                    plants = _selected.value!!.plants,
                    symptoms = _selected.value!!.symptoms
                )
                appWriteRepository.updateDocument(docId, newBlogEntry)

                // Borramos la imagen anterior (si es que existe)
                val oldImageId = _selected.value!!.entry_image_id

                if (oldImageId.isNotEmpty())
                    appWriteRepository.deleteBlogImage(oldImageId)

                // Actualizamos el blogEntry seleccionado
                _selected.value!!.entry_image_id = uploaded.id
                _selected.value!!.entry_image_url = imageUrl

                _editImageSuccess.value = true
                _editImage.value = false
                _newImageUri.value = null
            } catch (e: Exception) {
                _editImageError.value = true
            } finally {
                _isEditImageLoading.value = false
            }
        }
    }

    // -- Para editar el contenido --
    private val _editContent = MutableLiveData<Boolean>()
    val editContent: LiveData<Boolean> = _editContent

    private val _saveContentEnabled = MutableLiveData<Boolean>()
    val saveContentEnabled: LiveData<Boolean> = _saveContentEnabled

    private val _isEditContentLoading = MutableLiveData<Boolean>()
    val isEditContentLoading: LiveData<Boolean> = _isEditContentLoading

    private val _editContentSuccess = MutableLiveData<Boolean>()
    val editContentSuccess: LiveData<Boolean> = _editContentSuccess

    private val _editContentError = MutableLiveData<Boolean>()
    val editContentError: LiveData<Boolean> = _editContentError

    // Variables para editar el contenido
    private val _newTitle = MutableLiveData<String>()
    val newTitle: LiveData<String> = _newTitle

    private val _newContent = MutableLiveData<String>()
    val newContent: LiveData<String> = _newContent

    private val _newPlants = MutableLiveData<String>()
    val newPlants: LiveData<String> = _newPlants

    private val _newSymptoms = MutableLiveData<String>()
    val newSymptoms: LiveData<String> = _newSymptoms

    fun setEditContent(editContent: Boolean) {
        _editContent.value = editContent
    }

    fun setEditContentSuccess(value: Boolean) {
        _editContentSuccess.value = value
    }

    fun setEditContentError(value: Boolean) {
        _editContentError.value = value
    }

    fun onEditBlogEntryChanged(title: String, content: String, plants: String, symptoms: String) {
        _newTitle.value = title
        _newContent.value = content
        _newPlants.value = plants
        _newSymptoms.value = symptoms

        _saveContentEnabled.value = title.isNotEmpty() && content.isNotEmpty()
    }

    fun onSetEditedContent() {
        _isEditContentLoading.value = true

        viewModelScope.launch {
            try {
                val docId = _selected.value!!.id

                // Se obtienen las plantas y los sintomas
                val plants =
                    if (_newPlants.value!!.isEmpty())
                        listOf<String>("Ninguna")
                    else
                        _newPlants.value!!.split(",")
                            .map { it.trim() }
                val symptoms =
                    if (_newSymptoms.value!!.isEmpty())
                        listOf<String>("Ninguno")
                    else
                        _newSymptoms.value!!.split(",")
                            .map { it.trim() }

                // Actualizamos el contenido
                val newBlogEntry = BlogDocumentModel(
                    id_user = _selected.value!!.id_user,
                    name_user = _selected.value!!.name_user,
                    entry_title = _newTitle.value!!,
                    entry_content = _newContent.value!!,
                    entry_image_id = _selected.value!!.entry_image_id,
                    entry_image_url = _selected.value!!.entry_image_url,
                    posted = _selected.value!!.posted,
                    plants = plants,
                    symptoms = symptoms
                )
                appWriteRepository.updateDocument(docId, newBlogEntry)

                // Actualizamos el blogEntry seleccionado
                _selected.value!!.entry_title = _newTitle.value!!
                _selected.value!!.entry_content = _newContent.value!!
                _selected.value!!.plants = plants
                _selected.value!!.symptoms = symptoms

                _editContentSuccess.value = true
                _editContent.value = false
            } catch (e: Exception) {
                _editContentError.value = true
            } finally {
                _isEditContentLoading.value = false
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