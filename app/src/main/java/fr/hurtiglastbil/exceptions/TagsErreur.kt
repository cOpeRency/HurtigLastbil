package fr.hurtiglastbil.exceptions

enum class TagsErreur(val tag: String, val message: String) {
    ERREUR_CREATION_FICHIER("Exception creation fichier", "Erreur lors de la creation du fichier"),
    ERREUR_MODIFICATION_FICHIER("Exception modification fichier", "Erreur lors de la modification du fichier"),
    ERREUR_ECOUTE_PAS_TEXTO("Exception entrée écoute pas texto", "L'entrée écoutée n'est pas un texto reçu"),
    ERREUR_VALIDATION_PERSONNE("Exception validation personne", "Erreur lors de la validation de la personne"),
    ERREUR_AJOUT_TYPE_TEXTO("Exception ajout type texto", "Erreur lors de l'ajout du type texto"),
    ERREUR_SUPPRESSION_TYPE_TEXTO("Exception suppression type texto", "Erreur lors de la suppression du type texto"),
    ERREUR_AJOUT_MOT_CLE("Exception ajout mot cle", "Erreur lors de l'ajout du mot cle"),
    ERREUR_SUPPRESSION_MOT_CLE("Exception suppression mot cle", "Erreur lors de la suppression du mot cle"),
    ERREUR_LOG("Erreur écriture", "Impossible d'écrire dans le fichier de logs"),
}