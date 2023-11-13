package fr.hurtiglastbil.exceptions

import fr.hurtiglastbil.enumerations.TagsErreur

class ExceptionSuppressionTypeTexto : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_SUPPRESSION_TYPE_TEXTO.message
}