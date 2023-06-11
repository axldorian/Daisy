package com.daisydev.daisy.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.daisydev.daisy.ui.compose.blog.BlogScreen
import com.daisydev.daisy.ui.compose.reconocimiento.CamaraScreen
import com.daisydev.daisy.ui.compose.seguimiento.SeguimientoScreen
import com.daisydev.daisy.ui.compose.sesion.AccessScreen
import com.daisydev.daisy.ui.compose.sesion.LoginScreen
import com.daisydev.daisy.ui.compose.sesion.RegisterScreen
import com.daisydev.daisy.ui.compose.sesion.SesionScreen
import com.daisydev.daisy.ui.compose.sintomas.SintomasScreen
import com.daisydev.daisy.ui.compose.sintomas.PlantaScreen


/**
 * Función que contiene el grafo de navegación de la aplicación.
 */
@Composable
fun NavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    NavHost(navController, startDestination = NavRoute.Sintomas.path) {
        composable(NavRoute.Sintomas.path) {
            SintomasScreen(navController = navController)
        }

        composable(NavRoute.Seguimiento.path) {
            SeguimientoScreen(navController = navController)
        }

        composable(NavRoute.Blog.path) {
            BlogScreen(navController = navController)
        }

        composable(NavRoute.Sesion.path) {
            SesionScreen(navController = navController)
        }

        composable(NavRoute.Access.path) {
            AccessScreen(navController = navController)
        }

        composable(NavRoute.Login.path) {
            LoginScreen(navController = navController, snackbarHostState)
        }

        composable(NavRoute.Register.path) {
            RegisterScreen(navController = navController, snackbarHostState)
        }

        composable(NavRoute.Camera.path) {
            CamaraScreen(navController = navController, snackbarHostState)
        }
        composable(
            route = "plantaInfo/{name}/{nameC}/{body}/{uses}/{encodedUrl}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("nameC") { type = NavType.StringType },
                navArgument("body") { type = NavType.StringType },
                navArgument("uses") { type = NavType.StringType },
                navArgument("encodedUrl") { type = NavType.StringType }
            )
        )
        {
            val data_name = it.arguments?.getString("name")
            val data_nameC = it.arguments?.getString("nameC")
            val data_body = it.arguments?.getString("body")
            val data_uses = it.arguments?.getString("uses")
            val data_url = it.arguments?.getString("encodedUrl")
            requireNotNull(data_name)
            requireNotNull(data_nameC)
            requireNotNull(data_body)
            requireNotNull(data_uses)
            requireNotNull(data_url)
            PlantaScreen(
                navController = navController,
                name = data_name,
                nameC = data_nameC,
                body = data_body,
                uses = data_uses,
                url = data_url
            )
        }

    }
}