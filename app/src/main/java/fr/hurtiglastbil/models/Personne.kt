package fr.hurtiglastbil.models

import fr.hurtiglastbil.enumerations.JsonEnum
import org.json.JSONObject


data class Personne(
    var nom: String? = null,
    var role: String? = null,
    var numeroDeTelephone: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Personne

        if (numeroDeTelephone != other.numeroDeTelephone) return false

        return true
    }

    fun versJSON(): JSONObject {
        return JSONObject().put(JsonEnum.NOM_PERSONNE.cle, nom).put(JsonEnum.ROLE_PERSONNE.cle, role).put(JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle, numeroDeTelephone)
    }
}