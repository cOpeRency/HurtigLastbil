package fr.hurtiglastbil.exceptions

class ExceptionNouveauTypeTexto : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.message
}