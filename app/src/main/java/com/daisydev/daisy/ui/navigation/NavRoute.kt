package com.daisydev.daisy.ui.navigation

import com.daisydev.daisy.R

/**
 * Clase que representa una ruta de navegación
 * @param title Título de la ruta
 * @param path Dirección a la ruta
 * @param icon Icono de la ruta (opcional)
 */
sealed class NavRoute(val title: String, val path: String, val icon: Int = -1) {

    // Rutas de navegación para el BottomNavigation
    object Sintomas:
        NavRoute(title = "Sintomas", path = "sintomas", icon = R.drawable.ic_sintomas)
    object Seguimiento:
        NavRoute(title = "Seguimiento", path = "seguimiento", icon = R.drawable.ic_seguimiento)
    object Blog:
        NavRoute(title = "Blog", path = "blog", icon = R.drawable.ic_blog)
    object Sesion:
        NavRoute(title = "Sesión", path = "sesion", icon = R.drawable.ic_sesion)

    // Otras rutas de navegación
    object Access:
        NavRoute(title = "Access", path = "access")
    object Login:
        NavRoute(title = "Login", path = "login")
    object Register:
        NavRoute(title = "Register", path = "register")

    object Camera:
        NavRoute(title = "Camera", path = "camera")

    object PlantaInfo:
        NavRoute(title = "PlantaInfo", path = "plantaInfo")

    companion object {
        // Función que devuelve una lista de NavRoute para el BottomNavigation
        fun getBottomNavRoutes(): List<NavRoute> {
            return listOf(
                Sintomas,
                Seguimiento,
                Blog,
                Sesion
            )
        }

        fun getFullScreenPaths(): List<String> {
            return listOf(
                Camera.path
            )
        }
    }
}