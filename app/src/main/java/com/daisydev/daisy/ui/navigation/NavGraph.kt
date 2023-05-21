package com.daisydev.daisy.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daisydev.daisy.ui.compose.blog.BlogScreen
import com.daisydev.daisy.ui.compose.seguimiento.SeguimientoScreen
import com.daisydev.daisy.ui.compose.sesion.AccessScreen
import com.daisydev.daisy.ui.compose.sesion.LoginScreen
import com.daisydev.daisy.ui.compose.sesion.RegisterScreen
import com.daisydev.daisy.ui.compose.sesion.SesionScreen
import com.daisydev.daisy.ui.compose.sintomas.SintomasScreen

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
    }
}