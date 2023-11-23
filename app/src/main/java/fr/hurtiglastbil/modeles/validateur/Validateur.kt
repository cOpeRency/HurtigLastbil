package fr.hurtiglastbil.modeles.validateur

import android.util.Log
import fr.hurtiglastbil.enumerations.Regles

abstract class Validateur {
    /**
     * Les règles de validation
     */
    abstract var regles : Map<Regles,ArrayList<String>>

    /**
     * Retourne la valeur du champ demandé
     *
     * @param nomChamp Le nom du champ dont on veut la valeur
     * @return La valeur du champ nommé nomChamp
     */
    abstract fun getValeurChamp(nomChamp: String): String?

    /**
     * Valide les champs de l'objet
     *
     * @return true si toutes les règles sont validés, false sinon
     */
    open fun valider() : Boolean {
        for ((regle, champs) in regles) {
            for (champ in champs) {
                val valeurChamp = getValeurChamp(champ)
                if (!regle.valider(valeurChamp)) {
                    Log.d("Tests", "Valeur du champ $champ: $valeurChamp ")
                    return false
                }
            }
        }
        return true
    }
}