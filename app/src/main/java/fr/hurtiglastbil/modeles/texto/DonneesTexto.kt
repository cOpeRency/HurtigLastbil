package fr.hurtiglastbil.modeles.texto

import fr.hurtiglastbil.utilitaires.SerialiseurDeDate
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DonneesTexto(
    val envoyeur: String,
    val receveur: String,
    @Serializable(SerialiseurDeDate::class)
    val date: Date,
    val contenu: String)
