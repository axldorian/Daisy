package com.daisydev.daisy.util

import java.text.Normalizer

/**
 * Función que elimina los acentos de un texto
 */
fun removeAccents(input: String): String {
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD))
    return regex.replace(temp, "")
}