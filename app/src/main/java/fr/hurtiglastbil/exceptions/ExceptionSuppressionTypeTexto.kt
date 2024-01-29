package fr.hurtiglastbil.exceptions

class ExceptionSuppressionTypeTexto : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_SUPPRESSION_TYPE_TEXTO.message
}