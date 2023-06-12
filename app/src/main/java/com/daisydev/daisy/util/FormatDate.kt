package com.daisydev.daisy.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()

/**
 * Función que formatea una objeto Date a un String en formato local
 */
fun formatDate(date: Date): String {
    return dateFormat.format(date)
}