package com.daisydev.daisy.util

// Constantes de la aplicación
data class Constants(
    val baseUrl: String = "https://daisy.axlserial.me/v1",
    val projectId: String = "64668ccea09eba923ca3"
)

// Constantes de la aplicación (para usar en otros archivos)
object ExportConstants {
    const val baseUrl: String = "https://daisy.axlserial.me/v1"
    const val projectId: String = "64668ccea09eba923ca3"
}