package com.daisydev.daisy.ui.feature.blog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.models.Session
import com.daisydev.daisy.repository.local.SessionDataStore
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _response = MutableLiveData<List<BlogEntry>>()
    val response: LiveData<List<BlogEntry>> = _response

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

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }

    fun setIsContentLoading() {
        _isContentLoading.value = true
    }

    fun setIsSelfLoading() {
        _isSelfLoading.value = true
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun getInitialBlogEntries() {
        viewModelScope.launch {
            _response.value = appWriteRepository.listDocuments()
            _isFirstLoading.value = false
        }
    }

    fun getSelfBlogEntries() {
        viewModelScope.launch {
            _response.value = appWriteRepository.listDocumentsOfUser(_userData.value!!.id)
            _isSelfLoading.value = false
        }
    }

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
}