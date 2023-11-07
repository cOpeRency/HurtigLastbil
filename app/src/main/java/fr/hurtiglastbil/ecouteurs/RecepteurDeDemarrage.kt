package fr.hurtiglastbil.ecouteurs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.hurtiglastbil.activites.ActivitePrincipale

class RecepteurDeDemarrage : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Lancer votre activité principale ici
            val launchIntent = Intent(context, ActivitePrincipale::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(launchIntent)
        }
    }
}