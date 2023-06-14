package com.daisydev.daisy.ui.feature.seguimiento


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.ui.compose.seguimiento.Message
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


@OptIn(FlowPreview::class)
class SeguimientoViewModel:  ViewModel()  {
    //Variable para el listado de plantas
    private val _sampleData = MutableLiveData(emptyArray<Message>())
    val sampleData: LiveData<Array<Message>> get() = _sampleData

    // Texto ingresado por el usuario
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Estado para detectar si el usuario esta escribiendo en el buscador
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // Estado de la lista de las plantas
    private val _plantas = MutableStateFlow(allPlantasSeg)

    val cuidados = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_plantas) { text, cuidados ->
            if (text.isBlank()) {
                cuidados
            } else {
                delay(2000L)
                cuidados.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }.onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _plantas.value
        )

    // Cambios de el texto de busqueda , se usa en la UI, cada vez que el usuario escriba algo
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun setSampleData(plantMessages: Array<Message>) {
        _sampleData.value = plantMessages
    }

}

// Clase para las plantas
data class Cuidado(
    val planta: String
) {
    // Detecta alguna combinación de un sintoma
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$planta"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPlantasSeg = listOf(
    Cuidado(
        planta = "Orquídea"
    ),
    Cuidado(
        planta = "Rosa"
    ),
    Cuidado(
        planta = "Margarita"
    ),
    Cuidado(
        planta = "Gardenia"
    ),
    Cuidado(
        planta = "Canela"
    ),
    Cuidado(
        planta = "Manzano"
    ),
    Cuidado(
        planta = "Hortensia"
    ),
    Cuidado(
        planta = "Girasol"
    ),
    Cuidado(
        planta = "Mora azul"
    ),
)