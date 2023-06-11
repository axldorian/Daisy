package com.daisydev.daisy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.daisydev.daisy.ui.components.Layout
import com.daisydev.daisy.ui.theme.DaisyTheme
import dagger.hilt.android.AndroidEntryPoint

// Actividad principal de la aplicación
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaisyTheme {
                // Un Surface es un contenedor que aplica el tema de Material Design
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Layout() // Layout principal de la aplicación
                }
            }
        }
    }
}