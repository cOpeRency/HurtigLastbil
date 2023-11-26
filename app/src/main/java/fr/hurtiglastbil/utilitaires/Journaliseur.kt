package fr.hurtiglastbil.utilitaires

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import fr.hurtiglastbil.enumerations.TagsErreur
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Journaliseur {
    lateinit var contexte: Context

    fun journaliserErreur(tag: String, message: String){
        Log.e(tag, message)
        try {
            journaliser(message, "erreurs")
        } catch (e: Exception) {
            Log.e(TagsErreur.ERREUR_LOG.tag, TagsErreur.ERREUR_LOG.message)
        }
    }

    fun journaliserModificationDeLaConfiguration(tag: String, message: String){
        Log.i(tag, message)
        try {
            journaliser(message, "modifications de la config")
        } catch (e: Exception) {
            Log.e(TagsErreur.ERREUR_LOG.tag, TagsErreur.ERREUR_LOG.message)
        }
    }

    private fun journaliser(message: String, chemin: String) {
        val messageDeLog = "${obtenirDateEtHeure()} - $message\n"
        val fichier = File(contexte.getExternalFilesDir("hurtiglastbil/logs/" + chemin), "/logs.txt")
        if (!fichier.exists()) {
            try {
                fichier.createNewFile()
            } catch (e: IOException) {
                Log.e(TagsErreur.ERREUR_CREATION_FICHIER.tag, TagsErreur.ERREUR_CREATION_FICHIER.message + " de stockage de SMS.")
            }
        }
        try {
            val fluxDeFichierSortant = FileOutputStream(fichier,true)
            fluxDeFichierSortant.write(messageDeLog.toByteArray())
            fluxDeFichierSortant.close()
        } catch (e: IOException) {
            Log.e(TagsErreur.ERREUR_MODIFICATION_FICHIER.tag, TagsErreur.ERREUR_MODIFICATION_FICHIER.message + " de stockage de SMS." )
        }
        sauvegarderDansDocuments(contexte, fichier, chemin)
    }

    private fun obtenirDateEtHeure(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun sauvegarderDansDocuments(context: Context, file: File, chemin: String) {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "text/plain")
        contentValues.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_DOCUMENTS + "/hurtiglastbil/logs/" + chemin
        )
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
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
}