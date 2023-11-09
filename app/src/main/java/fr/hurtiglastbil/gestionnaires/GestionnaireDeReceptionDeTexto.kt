package fr.hurtiglastbil.gestionnaires

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import java.io.File
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

                EnregistrerLeFichier(contexte, expediteur, horodatage, corpsDuMessage)
            } else {
                Log.d("WHITELIST", "traiterTexto: $expediteur n'est pas dans la liste blanche")
            }
        } else {
            //log erreur
            Log.e("ERREUR", "traiterTexto: pas d'action")

        }
    }
}

private fun EnregistrerLeFichier(
    contexte: Context?,
    expediteur: String?,
    horodatage: Long,
    corpsDuMessage: String?
) {
    val fichier = File(contexte?.filesDir, "${expediteur}_${horodatage}_log.txt")
    if (!fichier.exists()) {
        try {
            fichier.createNewFile()
        } catch (e: IOException) {
            Log.e("Exception creation", "Erreur dans la création du fichier de stockage de SMS.")
        }
    }
    // Contenu à écrire dans le fichier
    val texteDansFichierDeLog = "SMS reçu de $expediteur : $corpsDuMessage\n"
    Log.d("Récepteur de Texto", contexte?.filesDir.toString())
    Log.d("Récepteur de Texto", fichier.readText())
    try {
        val fluxDeFichierSortant = FileOutputStream(
            fichier,
            true
        ) // true pour ajouter au fichier existant

        fluxDeFichierSortant.write(texteDansFichierDeLog.toByteArray())
        fluxDeFichierSortant.close()
    } catch (e: IOException) {
        Log.e("Exception", "Echec d'écriture de fichier: $e")
    }
}