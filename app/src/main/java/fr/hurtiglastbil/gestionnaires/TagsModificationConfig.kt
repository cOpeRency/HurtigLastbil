package fr.hurtiglastbil.gestionnaires

enum class TagsModificationConfig(val tag: String, val message: String) {
    PERSONNE_AJOUTE("Nouvelle personne", "Une nouvelle personne a été ajouté"),
    PERSONNE_MODIFIE("Personne modifié", "Une personne a été modifié"),
    MODIFICATION_TEMPS_RAFRAICHISSEMENT("Rafraissement modifié", "Nouveau temps de rafraichissement"),
}
