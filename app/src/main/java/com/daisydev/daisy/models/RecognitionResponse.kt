package com.daisydev.daisy.models

data class AltName(
    val name: String
)

// Clase que representa la respuesta del servidor al enviar una imagen para reconocimiento
data class DataPlant(
    val plant_name: String,
    val probability: Double,
    val alt_names: List<AltName>,
)