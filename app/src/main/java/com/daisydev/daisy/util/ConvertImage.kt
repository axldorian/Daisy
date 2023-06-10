package com.daisydev.daisy.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.close()

    return file
}