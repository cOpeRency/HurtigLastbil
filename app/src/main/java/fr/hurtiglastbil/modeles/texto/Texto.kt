package fr.hurtiglastbil.modeles.texto

import fr.hurtiglastbil.utilitaires.SerialiseurDeDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModuleOf
import java.util.Date
abstract class Texto(
    val envoyeur: String,
    val receveur: String,
    val date: Date,
    val contenu: String)
{
    fun enJson(): String {
        val donneesSMS = DonneesTexto(envoyeur, receveur, date, contenu)
        val json = Json { serializersModule = serializersModuleOf(Date::class, SerialiseurDeDate); prettyPrint = true }
        return json.encodeToString(donneesSMS)
    }
}