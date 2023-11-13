package fr.hurtiglastbil.exceptions

import fr.hurtiglastbil.enumerations.TagsErreur

class ExceptionNouveauMotCle : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_AJOUT_MOT_CLE.message

}