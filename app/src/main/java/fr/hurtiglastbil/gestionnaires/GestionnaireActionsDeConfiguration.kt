package fr.hurtiglastbil.gestionnaires

import android.util.Log
import fr.hurtiglastbil.enumerations.Roles
import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto

fun actionsConfiguration(corpsDuMessage: String, configuration: Configuration) {
    corpsDuMessage.split("\n").filterNot { it.startsWith("CONFIG") || it.startsWith("=") }
        .forEach { ligneConfiguration(it, configuration) }

    configuration.sauvegarder(CheminFichier("configuration.dev.json", "config"))
}

private fun ligneConfiguration(line: String, configuration: Configuration) {
    val words = line.split(" ")
    when (words[0].lowercase()) {
        "ajouter" -> ajouter(configuration, line)
        "modifier" -> modifier(configuration, line)
        "supprimer" -> supprimer(configuration, line)
        "réinitialiser" -> reinitialiser(configuration, line)
    }
}

private fun reinitialiser(configuration: Configuration, line: String) {
    val mots = line.split(" ").drop(1).joinToString(" ").split(",").map { it.trim() }
    configuration.reinitialiser(Personne(mots[0], Roles.ADMINISTRATEUR.motCle, mots[1].split(" ").joinToString("")))
}

private fun ajouter(configuration: Configuration, line: String) {
    val mots = line.split(" ")
    when {
        Roles.estUnRole(mots[1].lowercase()) -> {
            val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
            val donneesPersonne = line.split(" : ")[1]
            ajouterPersonneALaListeBlanche(
                configuration,
                role,
                donneesPersonne.split(",")[0],
                donneesPersonne.split(",")[1].split(" ").joinToString("")
            )
        }

        mots[1].lowercase() == "type" -> {
            val extraitDeLigne = line.split(" : ")
            ajouterTypeDeTexto(
                configuration,
                extraitDeLigne[1],
                extraitDeLigne[2].split(",").onEach { it.trim() })
        }

        (mots[1].lowercase() + mots[2].lowercase()) == "motsclé" -> {
            val extraitDeLigne = line.split(" : ")
            ajouterMotsCle(
                configuration,
                extraitDeLigne[1],
                extraitDeLigne[2].split(",").onEach { it.trim() })
        }
    }
}

fun ajouterMotsCle(configuration: Configuration, typeTexto: String, motCles: List<String>) {
    Log.d("Tests", "Ajouter : $typeTexto, $motCles")
    for (motCle in motCles) {
        configuration.typesDeTextos!!.ajouterMotCle(typeTexto, motCle)
    }
}

fun ajouterPersonneALaListeBlanche(configuration: Configuration, role: String, nom: String, numeroDeTelephone: String) {
    configuration.insererPersonne(Personne(role = role.trim(), nom = nom.trim(), numeroDeTelephone = numeroDeTelephone.trim()))
}

fun ajouterTypeDeTexto(configuration: Configuration, nomDuType: String, motCles: List<String>) {
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