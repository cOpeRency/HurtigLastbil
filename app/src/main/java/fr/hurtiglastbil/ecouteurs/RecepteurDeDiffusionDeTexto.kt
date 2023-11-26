package fr.hurtiglastbil.ecouteurs

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.hurtiglastbil.gestionnaires.traiterTexto
import fr.hurtiglastbil.utilitaires.Journaliseur

class RecepteurDeDiffusionDeTexto : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(contexte: Context?, action: Intent?) {
        if (contexte != null) {
            Journaliseur.contexte = contexte
        }
        traiterTexto(contexte, action)
    }
}