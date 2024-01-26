package fr.hurtiglastbil.gestionnaires

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import fr.hurtiglastbil.enumerations.TagsErreur
import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.EnregistrementFichierParams
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
    config.configurationDepuisStockageExterne(CheminFichier("configuration.dev.json", "config"))

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
        val isAdmin = personneInList?.role == "administrateur"
        val startsWithConfig = corpsDuMessage.startsWith("config", ignoreCase = true) || corpsDuMessage.startsWith("configuration", ignoreCase = true)

        if (isAdmin && startsWithConfig) {
            actionsConfiguration(corpsDuMessage, config)
        }

        Log.d("TEXT_RECEIVER", "Texto reçu de $expediteur  : $corpsDuMessage")
        Log.d("WHITELIST", "traiterTexto: $expediteur est dans la liste blanche")

        personneInList?.let {
            val params = EnregistrementFichierParams(contexte, it, horodatage, corpsDuMessage, config)
            enregistrerLeFichier(params)
        }
    } else {
        Log.d("WHITELIST", "traiterTexto: $expediteur n'est pas dans la liste blanche")
    }
}


private fun enregistrerLeFichier(params: EnregistrementFichierParams) {
    val subDirBase = "textos"
    val typeDuTexto = params.configuration.typesDeTextos!!.recupererTypeTextoDepuisCorpsMessage(params.corpsDuMessage!!)
    Log.d("Tests", "enregistrerLeFichier: $typeDuTexto")
    val subDir: String = if (typeDuTexto != null) {
        "$subDirBase/${typeDuTexto.cle.split(" ").joinToString("/")}"
    } else {
        "$subDirBase/indéfini"
    }
    val fichier = File(params.contexte.getExternalFilesDir("hurtiglastbil"), "${params.expediteur.nom}_${params.expediteur.numeroDeTelephone}_${params.horodatage}.log.txt")
    if (!fichier.exists()) {
        try {
            fichier.createNewFile()
        } catch (e: IOException) {
            Log.e(TagsErreur.ERREUR_CREATION_FICHIER.tag, TagsErreur.ERREUR_CREATION_FICHIER.message + " de stockage de SMS.")
        }
    }

    val texto = FabriqueATexto().creerTexto(
        params.expediteur.numeroDeTelephone,
        "Hurtiglastbil",
        Date.from(Instant.ofEpochMilli(params.horodatage)),
        params.corpsDuMessage,
        params.configuration.typesDeTextos!!
    )
    val texteDansFichierDeLog: String = if (!(texto is TextoIndefini)) {
        texto.enJson() + "\n"
    } else {
        "SMS reçu de ${params.expediteur.nom} avec le numéro ${params.expediteur.numeroDeTelephone} : \n${params.corpsDuMessage}"
    }

    // Contenu à écrire dans le fichier
    Log.d("Récepteur de Texto", params.contexte.getExternalFilesDir("hurtiglastbil").toString())
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
    updateGallery(params.contexte, fichier, subDir)
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

    // Exécution de la requête
    val cursor = executerRequete(resolver,file,cheminComplet)

    var uri: Uri = MediaStore.Files.getContentUri("external")
    if (cursor != null && cursor.moveToFirst()) { //Si fichier existe déjà, on le suppr
        val existingUri = ContentUris.withAppendedId(uri, cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)?:0))
        resolver.delete(existingUri, null, null)
    }
    sauvegarderFichier(resolver,file,uri,contentValues)
    cursor?.close()
}
