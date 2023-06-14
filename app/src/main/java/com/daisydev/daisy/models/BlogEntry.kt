package com.daisydev.daisy.models

import com.daisydev.daisy.util.convertStringToDate
import io.appwrite.models.Document
import java.util.Date

// Clase base que representa el modelo de un documento de la colección "blog"
open class BlogDocumentModel(
    val id_user: String,
    val name_user: String,
    var entry_title: String,
    var entry_content: String,
    var entry_image_id: String,
    var entry_image_url: String,
    var posted: Boolean,
    var plants: List<String>,
    var symptoms: List<String>
)

// Clase que representa el modelo de un documento de la colección "blog"
// con los campos adicionales de la base de datos
class BlogEntry(
    val id: String,
    val collectionId: String,
    val databaseId: String,
    val createdAt: Date,
    val updatedAt: Date,
    id_user: String,
    name_user: String,
    entry_title: String,
    entry_content: String,
    entry_image_id: String,
    entry_image_url: String,
    posted: Boolean,
    plants: List<String>,
    symptoms: List<String>
) : BlogDocumentModel(
    id_user = id_user,
    name_user = name_user,
    entry_title = entry_title,
    entry_content = entry_content,
    entry_image_id = entry_image_id,
    entry_image_url = entry_image_url,
    posted = posted,
    plants = plants,
    symptoms = symptoms
)

/**
 * Convierte un documento de la colección "blog" a un objeto BlogEntry
 * @param document Documento de la colección "blog"
 * @return Objeto BlogEntry
 */
@Suppress("UNCHECKED_CAST")
fun toBlogEntry(document: Document<Map<String, Any>>): BlogEntry {
    return BlogEntry(
        id = document.id,
        collectionId = document.collectionId,
        databaseId = document.databaseId,
        createdAt = convertStringToDate(document.createdAt),
        updatedAt = convertStringToDate(document.updatedAt),
        id_user = document.data["id_user"].toString(),
        name_user = document.data["name_user"].toString(),
        entry_title = document.data["entry_title"].toString(),
        entry_content = document.data["entry_content"].toString(),
        entry_image_id = document.data["entry_image_id"] as? String ?: "",
        entry_image_url = document.data["entry_image_url"] as? String ?: "",
        posted = document.data["posted"] as Boolean,
        plants = document.data["plants"] as? List<String> ?: listOf(),
        symptoms = document.data["symptoms"] as? List<String> ?: listOf()
    )
}