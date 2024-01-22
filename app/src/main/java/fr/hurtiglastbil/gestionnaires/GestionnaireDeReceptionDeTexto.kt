package fr.hurtiglastbil.gestionnaires

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import fr.hurtiglastbil.enumerations.TagsErreur
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.FabriqueATexto
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TextoIndefini
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.util.Date

fun traiterTexto(contexte: Context?, action: Intent?) {
    val config = Configuration(contexte ?: return)
    config.configurationDepuisStockageExterne("configuration.dev.json", "config")

    if (action?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
        // Log an error
        Log.e(TagsErreur.ERREUR_ECOUTE_PAS_TEXTO.tag, TagsErreur.ERREUR_ECOUTE_PAS_TEXTO.message)
        return
    }

    val message = Telephony.Sms.Intents.getMessagesFromIntent(action)
    if (message.isNullOrEmpty()) {
        // Log an error
        Log.e("TEXT_RECEIVER", "No SMS message found in the Intent")
        return
    }

    val expediteur = message[0].displayOriginatingAddress
    val corpsDuMessage = message[0].displayMessageBody
    val horodatage = System.currentTimeMillis()

    val personne = Personne(numeroDeTelephone = expediteur)
    val isInWhitelist = config.listeBlanche?.estDansLaListeBlanche(personne) == true
    if (isInWhitelist) {
        val personneInList = config.listeBlanche?.creerPersonneSiInseree(personne)
        if (personneInList?.role == "administrateur" && (corpsDuMessage.startsWith("config", ignoreCase = true) || corpsDuMessage.startsWith("configuration", ignoreCase = true))) {
            actionsConfiguration(corpsDuMessage, config)
        }

        Log.d("TEXT_RECEIVER", "Texto reçu de $expediteur  : $corpsDuMessage")
        Log.d("WHITELIST", "traiterTexto: $expediteur est dans la liste blanche")

        personneInList?.let {
            enregistrerLeFichier(contexte,
                it, horodatage, corpsDuMessage, config)
        }
    } else {
        Log.d("WHITELIST", "traiterTexto: $expediteur n'est pas dans la liste blanche")
    }
}


private fun enregistrerLeFichier(
    contexte: Context,
    expediteur: Personne,
    horodatage: Long,
    corpsDuMessage: String?,
    configuration: Configuration
) {
    val subDirBase = "textos"
    val typeDuTexto = configuration.typesDeTextos!!.recupererTypeTextoDepuisCorpsMessage(corpsDuMessage!!)
    Log.d("Tests", "enregistrerLeFichier: $typeDuTexto")
    val subDir: String = if (typeDuTexto != null) {
        "$subDirBase/${typeDuTexto.cle.split(" ").joinToString("/")}"
    } else {
        "$subDirBase/indéfini"
    }
    val fichier = File(contexte.getExternalFilesDir("hurtiglastbil"), "${expediteur.nom}_${expediteur.numeroDeTelephone}_${horodatage}.log.txt")
    if (!fichier.exists()) {
        try {
            fichier.createNewFile()
        } catch (e: IOException) {
            Log.e(TagsErreur.ERREUR_CREATION_FICHIER.tag, TagsErreur.ERREUR_CREATION_FICHIER.message + " de stockage de SMS.")
        }
    }

    val texto = FabriqueATexto().creerTexto(
        expediteur.numeroDeTelephone,
        "Hurtiglastbil",
        Date.from(Instant.ofEpochMilli(horodatage)),
        corpsDuMessage,
        configuration.typesDeTextos!!
    )
    val texteDansFichierDeLog: String = if (!(texto is TextoIndefini)) {
        texto.enJson() + "\n"
    } else {
        "SMS reçu de ${expediteur.nom} avec le numéro ${expediteur.numeroDeTelephone} : \n$corpsDuMessage"
    }

    // Contenu à écrire dans le fichier
    Log.d("Récepteur de Texto", contexte.getExternalFilesDir("hurtiglastbil").toString())
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
    updateGallery(contexte, fichier, subDir)
}

fun updateGallery(context: Context, file: File, subDir: String? = null) {
    val resolver: ContentResolver = context.contentResolver
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
    val cheminComplet = if (subDir != null) "/hurtiglastbil/$subDir" else "/hurtiglastbil"
    contentValues.put(
        MediaStore.Images.Media.RELATIVE_PATH,
        Environment.DIRECTORY_DOCUMENTS + cheminComplet
    )

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
    // Exécution de la requête
    val cursor = resolver.query(queryUri, projection, selection, selectionArgs, null)
    if (cursor != null && cursor.moveToFirst()) {
        // Si le fichier existe le premier élément du curseur vaut true
        try {
            // Le fichier existe déjà, supprimez-le
            val existingUri = ContentUris.withAppendedId(queryUri, cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)?:0))
            resolver.delete(existingUri, null, null)

            // Insérez le fichier mis à jour
            val uri = resolver.insert(queryUri, contentValues)

            resolver.openOutputStream(uri!!)?.use { outputStream ->
                FileInputStream(file).use { fileInputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }
        } catch (e: Exception) {
            // Gérer l'exception si la suppression ou l'insertion échoue
            e.printStackTrace()
        }
    } else {
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
    cursor?.close()
}
