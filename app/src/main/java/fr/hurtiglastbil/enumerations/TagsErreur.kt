package fr.hurtiglastbil.enumerations

enum class TagsErreur(val tag: String, val message: String) {
    ERREUR_CREATION_FICHIER("Exception creation fichier", "Erreur lors de la creation du fichier"),
    ERREUR_MODIFICATION_FICHIER("Exception modification fichier", "Erreur lors de la modification du fichier"),
    ERREUR_LECTURE_FICHIER("Exception lecture fichier", "Erreur lors de la lecture du fichier"),
    ERREUR_ECOUTE_PAS_TEXTO("Exception entrée écoute pas texto", "L'entrée écoutée n'est pas un texto reçu"),
    ERREUR_VALIDATION_PERSONNE("Exception validation personne", "Erreur lors de la validation de la personne"),
}