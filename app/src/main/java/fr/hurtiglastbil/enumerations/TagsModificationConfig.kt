package fr.hurtiglastbil.enumerations

enum class TagsModificationConfig(val tag: String, val message: String) {
    PERSONNE_AJOUTE("Nouvelle personne", "Une nouvelle personne a été ajouté"),
    PERSONNE_MODIFIE("Personne modifié", "Une personne a été modifié"),
    PERSONNE_SUPPRIMER("Personne supprimé", "Une personne a été supprimé"),
    MODIFICATION_TEMPS_RAFRAICHISSEMENT("Rafraissement modifié", "Nouveau temps de rafraichissement"),

}