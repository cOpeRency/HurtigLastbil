package fr.hurtiglastbil.model.texto

import fr.hurtiglastbil.util.SerialiseurDeDate
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
        val smsData = DonneesTexto(envoyeur, receveur, date, contenu)
        val json = Json { serializersModule = serializersModuleOf(Date::class, SerialiseurDeDate) }
        return Json.encodeToString(smsData)
    }
}