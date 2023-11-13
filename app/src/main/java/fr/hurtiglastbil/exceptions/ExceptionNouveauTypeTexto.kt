package fr.hurtiglastbil.exceptions

import fr.hurtiglastbil.enumerations.TagsErreur

class ExceptionNouveauTypeTexto : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.message
}