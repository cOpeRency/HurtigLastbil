package fr.hurtiglastbil.modeles.texto

import android.util.Log
import fr.hurtiglastbil.enumerations.TagsErreur
import fr.hurtiglastbil.exceptions.ExceptionNouveauMotCle
import fr.hurtiglastbil.exceptions.ExceptionNouveauTypeTexto
import fr.hurtiglastbil.exceptions.ExceptionSuppressionMotCle
import fr.hurtiglastbil.exceptions.ExceptionSuppressionTypeTexto
import org.json.JSONArray

class ListeDesTypesDeTextos(var typeTextos: MutableSet<TypeTexto>? = null) {
    fun creerDepuisJSONArray(jsonArray: JSONArray) : ListeDesTypesDeTextos {
        typeTextos = mutableSetOf()
        for (i in 0 until jsonArray.length()) {
            val motCle = jsonArray.getJSONObject(i)
            val cles = motCle.keys()
            for (cle in cles) {
                val motsClesArray = motCle.getJSONArray(cle)
                val listeDeMotsCles = mutableSetOf<String>()
                for (j in 0 until motsClesArray.length()) {
                    listeDeMotsCles.add(motsClesArray.getString(j))
                }
                typeTextos!!.add(TypeTexto(cle, listeDeMotsCles))
            }
        }
        return this
    }

    fun ajouterTypeTexto(typeTexto: TypeTexto) {
        if (typeTexto.valider()) {
            this.typeTextos?.add(typeTexto)
        } else {
            Log.e(TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.tag, TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.message + typeTexto)
            throw ExceptionNouveauTypeTexto()
        }
    }

    fun supprimerTypeTexto(cle: String) {
        val typeTexto = recupererTypeTexto(cle)
        if (typeTexto!!.valider()) {
            this.typeTextos?.remove(typeTexto)
        } else {
            Log.e(TagsErreur.ERREUR_SUPPRESSION_TYPE_TEXTO.tag, TagsErreur.ERREUR_SUPPRESSION_TYPE_TEXTO.message + typeTexto)
            throw ExceptionSuppressionTypeTexto()
        }
    }

    fun listeDeMotsClesVersJSONArray(): JSONArray {
        if (typeTextos == null) {
            return JSONArray()
        }
        val jsonArray = JSONArray()
        typeTextos!!.forEach { jsonArray.put(it.versJSON()) }
        return jsonArray
    }

    fun recupererTypeTextoDepuisCorpsMessage(corpsMessage: String): TypeTexto? {
        val motsMessage = corpsMessage.split(" ").map { it.lowercase() }
        return typeTextos?.find { typeTexto -> typeTexto.listeDeMotsCles.any { motsMessage.contains(it) } }
    }

    fun recupererTypeTexto(cle: String): TypeTexto? {
        return typeTextos?.find { it.cle == cle }
    }

    fun recupererLaListeDesMotsCles(cle: String): MutableSet<String>? {
        return recupererTypeTexto(cle)?.listeDeMotsCles
    }

    fun ajouterMotCle(cle: String, motCle: String) {
        try  {
            recupererTypeTexto(cle)?.ajouterMotCle(motCle)
        } catch (e: ExceptionNouveauMotCle) {
            Log.e(TagsErreur.ERREUR_AJOUT_MOT_CLE.tag, e.message + " pour le type suivant : "+ cle)
        }
    }

    fun supprimerMotCle(cle: String, motCle: String) {
        try {
            recupererTypeTexto(cle)?.supprimerMotCle(motCle)
        } catch (e: ExceptionSuppressionMotCle) {
            Log.e(TagsErreur.ERREUR_SUPPRESSION_MOT_CLE.tag, e.message + " pour le type suivant : "+ cle)
        }
    }

    override fun toString(): String {
        return "ListeDesTypesDeTextos(Types de textos = $typeTextos)"
    }
}