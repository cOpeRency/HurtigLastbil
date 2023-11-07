package fr.hurtiglastbil.ecouteurs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecepteurDeDiffusionDeTexto : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val bundle = intent.extras // Réellement utile au final ?
                if (bundle != null) {
                    val message = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    val sender = message.get(0).displayOriginatingAddress
                    val messageBody = message.get(0).displayMessageBody
                    Toast.makeText(
                        context,
                        "SMS reçu de $sender  : $messageBody",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("Récepteur de Texto", "mdr Texto reçu de $sender  : $messageBody")

                    // Emplacement où on enregistre le fichier (exemple)
                    val timestamp = System.currentTimeMillis()
                    val filePath = File(context?.filesDir, "${sender}_${timestamp}_log.txt")
                    if (!filePath.exists()) {
                        try {
                            filePath.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }                    // Contenu à écrire dans le fichier
                    val logText = "SMS reçu de $sender : $messageBody\n"
                    Log.d("Récepteur de Texto", context?.filesDir.toString())
                    Log.d("Récepteur de Texto", filePath.readText())
                    try {
                        val fileOutputStream = FileOutputStream(
                            filePath,
                            true
                        ) // true pour ajouter au fichier existant
                        fileOutputStream.write(logText.toByteArray())
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Erreur",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}