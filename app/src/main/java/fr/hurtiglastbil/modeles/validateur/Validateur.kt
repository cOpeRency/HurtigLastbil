package fr.hurtiglastbil.modeles.validateur

import fr.hurtiglastbil.enumerations.Regles

abstract class Validateur {
    abstract var regles : Map<Regles,ArrayList<String>>

    abstract fun getValeurChamp(nomChamp: String): String?

    open fun valider() : Boolean {
        for ((regle, champs) in regles) {
            for (champ in champs) {
                val valeurChamp = getValeurChamp(champ)
                if (!regle.valider(valeurChamp)) {
                    return false
                }
            }
        }
        return true
    }
}