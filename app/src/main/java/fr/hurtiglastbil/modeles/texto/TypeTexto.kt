package fr.hurtiglastbil.modeles.texto

import fr.hurtiglastbil.modeles.Regles
import fr.hurtiglastbil.exceptions.ExceptionNouveauMotCle
import fr.hurtiglastbil.exceptions.ExceptionSuppressionMotCle
import fr.hurtiglastbil.modeles.validateur.Validateur
import org.json.JSONArray
import org.json.JSONObject

data class TypeTexto(val cle: String, val listeDeMotsCles: MutableSet<String>) : Validateur(){
    fun ajouterMotCle(motCle: String) {
        if (listeDeMotsCles.contains(motCle)) throw ExceptionNouveauMotCle()
        listeDeMotsCles.add(motCle)
    }

    fun supprimerMotCle(motCle: String) {
        if (!listeDeMotsCles.contains(motCle)) throw ExceptionSuppressionMotCle()
        listeDeMotsCles.remove(motCle)
    }

    fun versJSON(): JSONObject {
        val json = JSONArray()
        listeDeMotsCles.forEach { json.put(it) }
        return JSONObject().put(cle, json)
    }

    override var regles: Map<Regles, ArrayList<String>> = mapOf(
        Regles.PAS_VIDE_OU_NULL to arrayListOf(TypeTexto::cle.name, TypeTexto::listeDeMotsCles.name),
    )

    override fun getValeurChamp(nomChamp: String): String? {
        return when (nomChamp) {
            TypeTexto::cle.name -> cle
            TypeTexto::listeDeMotsCles.name -> listeDeMotsCles.joinToString { it }
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypeTexto

        if (cle != other.cle) return false
        if (listeDeMotsCles != other.listeDeMotsCles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cle.hashCode()
        result = 31 * result + listeDeMotsCles.hashCode()
        return result
    }
}