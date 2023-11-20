package fr.hurtiglastbil.gestionnaires

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import fr.hurtiglastbil.enumerations.TagsErreur
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun traiterTexto(contexte: Context?, action: Intent?) {
    val config = Configuration(contexte!!)
    config.configurationDepuisFichierInterne("configuration.dev.json")
    if (action != null) {
        if (action.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val message = Telephony.Sms.Intents.getMessagesFromIntent(action)
            val expediteur = message.get(0).displayOriginatingAddress
            val corpsDuMessage = message.get(0).displayMessageBody
            val horodatage = System.currentTimeMillis()
            if (config.listeBlanche!!.estDansLaListeBlanche(Personne(numeroDeTelephone = expediteur))) {
                Log.d("Récepteur de Texto", "Texto reçu de $expediteur  : $corpsDuMessage")
                Log.d("WHITELIST", "traiterTexto: $expediteur est dans la liste blanche")

                enregistrerLeFichier(contexte, expediteur, horodatage, corpsDuMessage)
            } else {
                Log.d("WHITELIST", "traiterTexto: $expediteur n'est pas dans la liste blanche")
            }
        } else {
            //log erreur
            Log.e(TagsErreur.ERREUR_ECOUTE_PAS_TEXTO.tag, TagsErreur.ERREUR_ECOUTE_PAS_TEXTO.message)
        }
    }
}

private fun enregistrerLeFichier(
    contexte: Context,
    expediteur: String?,
    horodatage: Long,
    corpsDuMessage: String?
) {
    val fichier = File(contexte.getExternalFilesDir("hurtiglastbil"), "${expediteur}_${horodatage}_log.txt")
    if (!fichier.exists()) {
        try {
            fichier.createNewFile()
        } catch (e: IOException) {
            Log.e(TagsErreur.ERREUR_CREATION_FICHIER.tag, TagsErreur.ERREUR_CREATION_FICHIER.message + " de stockage de SMS.")
        }
    }
    // Contenu à écrire dans le fichier
    val texteDansFichierDeLog = "SMS reçu de $expediteur : $corpsDuMessage\n"
    Log.d("Récepteur de Texto", contexte.filesDir.toString())
    Log.d("Récepteur de Texto", fichier.readText())
    try {
        val fluxDeFichierSortant = FileOutputStream(
            fichier,
            true
        ) // true pour ajouter au fichier existant

        fluxDeFichierSortant.write(texteDansFichierDeLog.toByteArray())
        fluxDeFichierSortant.close()
    } catch (e: IOException) {
        Log.e(TagsErreur.ERREUR_MODIFICATION_FICHIER.tag, TagsErreur.ERREUR_MODIFICATION_FICHIER.message + " de stockage de SMS." )
    }
    updateGallery(contexte, fichier)
}

private fun updateGallery(context: Context, file: File) {
    val resolver: ContentResolver = context.contentResolver
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "text/plain")
    contentValues.put(
        MediaStore.Images.Media.RELATIVE_PATH,
        Environment.DIRECTORY_DOCUMENTS + "/hurtiglastbil"
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
            } // Le flux fileInputStream est fermé automatiquement à la fin du bloc 'use'
        } // Le flux outputStream est fermé automatiquement à la fin du bloc 'use'
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
