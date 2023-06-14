package com.daisydev.daisy.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream

/**
 * Convierte un [ImageProxy] en un [File].
 * @param context Contexto de la aplicación.
 * @param image Imagen a convertir.
 * @return [File] con la imagen convertida.
 */
fun convertImage(context: Context, image: ImageProxy): File {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val file = File(context.cacheDir, "image.jpg")
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
    }

    return file
}

/**
 * Obtiene el filename de un [Uri]
 * @param context Contexto de la aplicación.
 * @param uri Uri a convertir.
 * @return [String] con el filename.
 */
fun getFilenameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(
        uri, null, null, null, null
    )

    val filename = if (cursor != null && cursor.moveToFirst()) {
        val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (columnIndex != -1) {
            cursor.getString(columnIndex)
        } else {
            null
        }
    } else {
        null
    }
    cursor?.close()

    return filename
}