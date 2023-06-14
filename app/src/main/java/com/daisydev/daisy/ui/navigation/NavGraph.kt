package com.daisydev.daisy.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.daisydev.daisy.ui.compose.blog.BlogScreen
import com.daisydev.daisy.ui.compose.blog.EntryBlogScreen
import com.daisydev.daisy.ui.compose.reconocimiento.CamaraScreen
import com.daisydev.daisy.ui.compose.seguimiento.SeguimientoScreen
import com.daisydev.daisy.ui.compose.sesion.AccessScreen
import com.daisydev.daisy.ui.compose.sesion.LoginScreen
import com.daisydev.daisy.ui.compose.sesion.RegisterScreen
import com.daisydev.daisy.ui.compose.sesion.SesionScreen
import com.daisydev.daisy.ui.compose.sintomas.SintomasScreen
import com.daisydev.daisy.ui.compose.sintomas.PlantaScreen
import com.daisydev.daisy.ui.feature.blog.BlogSharedViewModel
import com.daisydev.daisy.ui.compose.seguimiento.SeguimientoPlanta


/**
 * Función que contiene el grafo de navegación de la aplicación.
 */
@Composable
fun NavGraph(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val blogSharedViewModel: BlogSharedViewModel = hiltViewModel() // Para sección blog

    NavHost(navController, startDestination = NavRoute.Sintomas.path) {
        composable(NavRoute.Sintomas.path) {
            SintomasScreen(navController = navController)
        }

        composable(NavRoute.Seguimiento.path) {
            SeguimientoScreen(navController = navController)
        }

        composable(NavRoute.Blog.path) {
            BlogScreen(
                navController = navController,
                sharedViewModel = blogSharedViewModel,
                snackbarHostState = snackbarHostState
            )
        }

        composable(NavRoute.EntryBlog.path) {
            EntryBlogScreen(
                navController = navController,
                viewModel = blogSharedViewModel,
                snackbarHostState = snackbarHostState
            )
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
        composable(
            route = "plantaCuidados/{name}/{nameC}/{body}/{cuidados}/{encodedUrl}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("nameC") { type = NavType.StringType },
                navArgument("body") { type = NavType.StringType },
                navArgument("cuidados") { type = NavType.StringType },
                navArgument("encodedUrl") { type = NavType.StringType }
            )
        ) { it ->
            val data_name = it.arguments?.getString("name")
            val data_nameC = it.arguments?.getString("nameC")
            val data_body = it.arguments?.getString("body")
            val data_cuidados = it.arguments?.getString("cuidados")?.split("\n") // Convertir a List<String>
            val data_url = it.arguments?.getString("encodedUrl")
            requireNotNull(data_name)
            requireNotNull(data_nameC)
            requireNotNull(data_body)
            requireNotNull(data_cuidados)
            requireNotNull(data_url)
            SeguimientoPlanta(
                navController = navController,
                name = data_name,
                nameC = data_nameC,
                body = data_body,
                cuidados = data_cuidados,
                url = data_url
            )
        }


    }
}