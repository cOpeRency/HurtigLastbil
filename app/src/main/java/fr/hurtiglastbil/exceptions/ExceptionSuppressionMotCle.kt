package fr.hurtiglastbil.exceptions

import fr.hurtiglastbil.enumerations.TagsErreur

class ExceptionSuppressionMotCle: Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_SUPPRESSION_MOT_CLE.message
}