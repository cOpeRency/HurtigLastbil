package fr.hurtiglastbil.exceptions

class ExceptionNouveauMotCle : Exception() {
    override val message: String
        get() = TagsErreur.ERREUR_AJOUT_MOT_CLE.message

}