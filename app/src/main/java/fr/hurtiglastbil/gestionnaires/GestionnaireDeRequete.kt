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


fun executerRequete(resolveur: ContentResolver, fichier: File, cheminComplet: String): Cursor? {
    val requeteUri = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH}=? AND ${MediaStore.Images.Media.DISPLAY_NAME}=?"
    val selectionArgs = arrayOf(
        Environment.DIRECTORY_DOCUMENTS + cheminComplet + "/",
        fichier.name
    )

    return resolveur.query(requeteUri, projection, selection, selectionArgs, null)
}

fun sauvegarderFichier(resolveur: ContentResolver, fichier: File, uri : Uri, valeursDeContenue : ContentValues){
    val uri = resolveur.insert(uri, valeursDeContenue)

    try {
        resolveur.openOutputStream(uri!!)?.use { outputStream ->
            FileInputStream(fichier).use { fileInputStream ->
                val tampon = ByteArray(1024)
                var lectureOctets: Int
                while (fileInputStream.read(tampon).also { lectureOctets = it } != -1) {
                    outputStream.write(tampon, 0, lectureOctets)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}