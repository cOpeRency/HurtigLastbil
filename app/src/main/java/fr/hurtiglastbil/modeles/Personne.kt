package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.enumerations.JsonEnum
import fr.hurtiglastbil.enumerations.Regles
import fr.hurtiglastbil.modeles.validateur.Validateur
import org.json.JSONObject


data class Personne(
    var nom: String? = null,
    var role: String? = null,
    var numeroDeTelephone: String
) : Validateur() {

    override var regles: Map<Regles, ArrayList<String>> = mapOf(
        Regles.NOM_PERSONNE to arrayListOf(Personne::nom.name),
        Regles.ROLE_PERSONNE to arrayListOf(Personne::role.name),
        Regles.TEL_FR to arrayListOf(Personne::numeroDeTelephone.name),
    )

    override fun getValeurChamp(nomChamp: String): String? {
        return when (nomChamp) {
            Personne::nom.name -> nom
            Personne::role.name -> role
            Personne::numeroDeTelephone.name -> numeroDeTelephone
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Personne) return false

        val normalizedThisNumber = normalizePhoneNumber(numeroDeTelephone)
        val normalizedOtherNumber = normalizePhoneNumber(other.numeroDeTelephone)

        return normalizedThisNumber == normalizedOtherNumber
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        return when {
            phoneNumber.startsWith("0") -> phoneNumber.substring(1)
            phoneNumber.startsWith("+33") -> phoneNumber.substring(3)
            else -> phoneNumber
        }
    }

    fun versJSON(): JSONObject {
        return JSONObject().put(JsonEnum.NOM_PERSONNE.cle, nom).put(JsonEnum.ROLE_PERSONNE.cle, role).put(JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle, numeroDeTelephone)
    }

    override fun hashCode(): Int {
        var result = nom?.hashCode() ?: 0
        result = 31 * result + (role?.hashCode() ?: 0)
        result = 31 * result + numeroDeTelephone.hashCode()
        return result
    }
}