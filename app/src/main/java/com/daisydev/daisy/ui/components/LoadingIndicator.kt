package com.daisydev.daisy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Un indicador de progreso circular que llena el espacio disponible.
 *
 * @param backgroudColor (opcional) El color de fondo del indicador de progreso.
 */
@Composable
fun LoadingIndicator(backgroudColor: Color? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        if (backgroudColor != null) {
            CircularProgressIndicator(
                color = backgroudColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}