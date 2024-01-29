package fr.hurtiglastbil.modeles

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

    override fun recupereValeurChamp(nomChamp: String): String? {
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

        val normalizedThisNumber = normaliseNumeroTelephone(numeroDeTelephone)
        val normalizedOtherNumber = normaliseNumeroTelephone(other.numeroDeTelephone)

        return normalizedThisNumber == normalizedOtherNumber
    }

    private fun normaliseNumeroTelephone(numeroDeTelephone: String): String {
        return when {
            numeroDeTelephone.startsWith("0") -> numeroDeTelephone.substring(1)
            numeroDeTelephone.startsWith("+33") -> numeroDeTelephone.substring(3)
            else -> numeroDeTelephone
        }
    }

    fun versJSON(): JSONObject {
        return JSONObject().put(JsonEnum.NOM_PERSONNE.cle, nom).put(JsonEnum.ROLE_PERSONNE.cle, role).put(
            JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle, numeroDeTelephone)
    }

    override fun hashCode(): Int {
        var result = nom?.hashCode() ?: 0
        result = 31 * result + (role?.hashCode() ?: 0)
        result = 31 * result + numeroDeTelephone.hashCode()
        return result
    }
}