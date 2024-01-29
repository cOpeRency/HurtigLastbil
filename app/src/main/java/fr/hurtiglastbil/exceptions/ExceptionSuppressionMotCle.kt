package fr.hurtiglastbil.exceptions

class ExceptionSuppressionMotCle: Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_SUPPRESSION_MOT_CLE.message
}