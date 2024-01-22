package fr.hurtiglastbil.gestionnaires

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.IOException


fun executerRequete(resolver: ContentResolver, file: File, cheminComplet: String): Cursor? {
    // Récupère l'uri du stockage externe
    val queryUri = MediaStore.Files.getContentUri("external")
    // Récupère les colonnes à retourner
    val projection = arrayOf(MediaStore.Images.Media._ID)
    // Condition sur la selection (WHERE)
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH}=? AND ${MediaStore.Images.Media.DISPLAY_NAME}=?"
    val selectionArgs = arrayOf(
        Environment.DIRECTORY_DOCUMENTS + cheminComplet + "/",
        file.name
    )

    return resolver.query(queryUri, projection, selection, selectionArgs, null)
}

fun sauvegarderFichier(resolver: ContentResolver, file: File, uri : Uri, contentValues : ContentValues){
    val uri = resolver.insert(uri, contentValues)

    try {
        resolver.openOutputStream(uri!!)?.use { outputStream ->
            FileInputStream(file).use { fileInputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}