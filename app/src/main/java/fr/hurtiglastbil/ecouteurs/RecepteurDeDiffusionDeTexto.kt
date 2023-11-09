package fr.hurtiglastbil.ecouteurs

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.hurtiglastbil.gestionnaires.traiterTexto

class RecepteurDeDiffusionDeTexto : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(contexte: Context?, action: Intent?) {

        traiterTexto(contexte, action)
    }
}