package fr.hurtiglastbil.modeles

import android.content.Context

data class EnregistrementFichierParams (
    val contexte: Context,
    val expediteur: Personne,
    val horodatage: Long,
    val corpsDuMessage: String?,
    val configuration: Configuration
)