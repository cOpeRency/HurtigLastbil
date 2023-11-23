package fr.hurtiglastbil.gestionnaires

import android.util.Log
import fr.hurtiglastbil.enumerations.Roles
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto

fun actionsConfiguration(corpsDuMessage: String, configuration: Configuration) {
    for (ligne in corpsDuMessage.split("\n")) {
        Log.d("Tests", "ligne: $ligne")
        if (ligne.startsWith("CONFIG")) continue
        if (ligne.startsWith("=")) continue
        val mots = ligne.split(" ")
        if (mots[0].lowercase() == "ajouter") {
            Log.d("test", "Ajouter ")
            ajouter(configuration, ligne)
        } else if (mots[0].lowercase() == "modifier") {
            modifier(configuration, ligne)
        } else if (mots[0].lowercase() == "supprimer") {
            supprimer(configuration, ligne)
        }
    }
    Log.d("Tests", "traiterTexto: $configuration")
    configuration.sauvegarder("configuration.dev.json")
}

fun ajouter(configuration: Configuration, ligne: String) {
    val mots = ligne.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        // AJOUTER administrateUr : Michel, 0617878456
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val donneesPersonne = ligne.split(" : ")[1]
        ajouterPersonneALaListeBlanche(configuration, role, donneesPersonne.split(",")[0], donneesPersonne.split(",")[1].split(" ").joinToString(""))
    } else if(mots[1].lowercase() == "type") {
        // Ajouter type : nouveau type de texto : mot1, mot2, mot3
        Log.d("Tests", "ajouter: $ligne")
        val extraitDeLigne = ligne.split(" : ")
        ajouterTypeDeTexto(configuration, extraitDeLigne[1], extraitDeLigne[2].split(",").onEach { it.trim() })
    } else if(mots[1].lowercase() + mots[2].lowercase() == "motsclé") {
        // Ajouter mots clé : nouveau type de texto : mot1, mot2, mot3
        Log.d("Tests", "ajouter: $ligne")
        val extraitDeLigne = ligne.split(" : ")
        ajouterMotsCle(configuration, extraitDeLigne[1], extraitDeLigne[2].split(",").onEach { it.trim() })
    }
}

fun ajouterMotsCle(configuration: Configuration, typeTexto: String, motCles: List<String>) {
    Log.d("Tests", "Ajouter : $typeTexto, $motCles")
    for (motCle in motCles) {
        configuration.typesDeTextos!!.ajouterMotCle(typeTexto, motCle)
    }
}

fun ajouterPersonneALaListeBlanche(configuration: Configuration, role: String, nom: String, numeroDeTelephone: String) {
    configuration.insererPersonne(Personne(role = role.trim(), nom = nom.trim(), numeroDeTelephone = numeroDeTelephone.trim()), "configuration.dev.json")
}

fun ajouterTypeDeTexto(configuration: Configuration, nomDuType: String, motCles: List<String>) {
    Log.d("Tests", "Ajouter : $nomDuType, $motCles")
    configuration.typesDeTextos!!.ajouterTypeTexto(TypeTexto(nomDuType, motCles.toMutableSet()))
}

fun modifier(configuration: Configuration, ligne: String) {
    val mots = ligne.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        // MODIFIER administrateur : Michel 0617878456
        // MODIFIER camionneur : Michel 0617878456
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val partiePersonne = ligne.split(" : ")[1]
        val personne = Personne(partiePersonne.split(",")[0], role, partiePersonne.split(",")[1].split(" ").joinToString(""))
        supprimerPersonne(personne.role!!,personne.nom,personne.numeroDeTelephone, configuration)
        ajouterPersonneALaListeBlanche(configuration, personne.role!!, personne.nom!!, personne.numeroDeTelephone)
    }
}

fun supprimer(configuration: Configuration, ligne: String) {
    val mots = ligne.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        // SUPPRIMER administrateur : Michel, 0617878456
        // SUPPRIMER camionneur : 0617878456
        // SUPPRIMER camionneur : Michel, 0617878456
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val donneesPersonne = ligne.split(" : ")[1]
        var nom : String? = null
        val numeroDeTelephone : String
        if (donneesPersonne.split(",").size == 2) {
            nom = donneesPersonne.split(",")[0]
            numeroDeTelephone = donneesPersonne.split(",")[1].split(" ").joinToString("")
        } else {
            numeroDeTelephone = donneesPersonne.split(" ").joinToString("")
        }
        supprimerPersonne(role, nom, numeroDeTelephone, configuration)
    } else if(mots[1].lowercase() == "type") {
        // SUPPRIMER type : nouveau type de texto
        supprimerTypeTexto(ligne, configuration)
    } else if(mots[1].lowercase() + mots[2].lowercase() == "motsclés") {
        // SUPPRIMER mots clés : nouveau type de texto : mot1, mot2, mot3
        val extraitDeLigne = ligne.split(" : ")
        val motsCles = extraitDeLigne[2].split(",").onEach { it.trim() }
        for (motCle in motsCles) {
            configuration.typesDeTextos!!.supprimerMotCle(extraitDeLigne[1], motCle)
        }
    }
}

private fun supprimerTypeTexto(ligne: String, configuration: Configuration) {
    val extraitDeLigne = ligne.split(" : ")
    configuration.typesDeTextos!!.supprimerTypeTexto(extraitDeLigne[1])
}

private fun supprimerPersonne(role: String, nom: String?, numeroDeTelephone: String, configuration: Configuration) {
    val personne = Personne(
        role = role,
        nom = nom,
        numeroDeTelephone = numeroDeTelephone
    )
    configuration.listeBlanche!!.supprimerPersonne(personne)
}