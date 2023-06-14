package com.daisydev.daisy.ui.feature.sintomas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.ui.compose.sintomas.Message
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class MainViewModel : ViewModel() {
    //Variable para el listado de plantas
    private val _sampleData = MutableLiveData(emptyArray<Message>())
    val sampleData: LiveData<Array<Message>> get() = _sampleData

    // Texto ingresado por el usuario
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Estado para detectar si el usuario esta escribiendo en el buscador
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado de la lista de los sintomas
    private val _sintomas = MutableStateFlow(allSintomas)

    val sintomas = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_sintomas) { text, sintomas ->
            if (text.isBlank()) {
                sintomas
            } else {
                delay(1000L)
                sintomas.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }.onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            _sintomas.value
        )

    // Cambios de el texto de busqueda , se usa en la UI, cada vez que el usuario escriba algo
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun setSampleData(plantMessages: Array<Message>) {
        _sampleData.value = plantMessages
    }

    fun loadingFalse() {
        viewModelScope.launch {
            _isLoading.value = false
        }
    }

    fun loadingTrue() {
        viewModelScope.launch {
            _isLoading.value = true
        }
    }

}

// Clase para los sintomas
data class Sintoma(
    val sintoma: String
) {
    // Detecta alguna combinación de un sintoma
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$sintoma"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allSintomas = listOf(
    Sintoma(
        sintoma = "Fiebre"
    ),
    Sintoma(
        sintoma = "Tos"
    ),
    Sintoma(
        sintoma = "Ronquera"
    ),
    Sintoma(
        sintoma = "Fatiga"
    ),
    Sintoma(
        sintoma = "Náuseas"
    ),
    Sintoma(
        sintoma = "Vomitos"
    ),
    Sintoma(
        sintoma = "Problemas estomacales"
    ),
    Sintoma(
        sintoma = "Dolor abdominal"
    ),
    Sintoma(
        sintoma = "Manchas en la piel"
    ),
)