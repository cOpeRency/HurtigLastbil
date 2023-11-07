package fr.hurtiglastbil.modeles

enum class MotsCles(val listeDeMotsCles: Array<String>) {
    MOTS_CLE_RDV_LIVRAISON(arrayOf("livraison", "sodimat", "ecoval", "7h", "9h", "retour depot")),
    MOTS_CLE_RDV_REMORQUE_VIDE(arrayOf("gueret", "demain", "remorque vide")),
}