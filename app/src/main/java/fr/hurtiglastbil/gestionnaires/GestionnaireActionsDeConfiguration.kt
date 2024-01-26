package fr.hurtiglastbil.gestionnaires

import android.util.Log
import fr.hurtiglastbil.enumerations.Roles
import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto

data class RoleInfo(val role: String, val nom: String, val numeroDeTelephone: String)

data class TypeTextoInfo(val nomDuType: String, val motCles: List<String>)

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
    configuration.reinitialiser(
        Personne(
            mots[0],
            Roles.ADMINISTRATEUR.motCle,
            mots[1].split(" ").joinToString("")
        )
    )
}

private fun ajouter(configuration: Configuration, line: String) {
    val mots = line.split(" ")
    when {
        Roles.estUnRole(mots[1].lowercase()) -> {
            val roleInfo = parseRoleInfo(line)
            ajouterPersonneALaListeBlanche(configuration, roleInfo)
        }
        mots[1].lowercase() == "type" -> {
            val typeTextoInfo = parseTypeTextoInfo(line)
            ajouterTypeDeTexto(configuration, typeTextoInfo)
        }
        (mots[1].lowercase() + mots[2].lowercase()) == "motsclé" -> {
            val motsCleInfo = parseTypeTextoInfo(line)
            ajouterMotsCle(configuration, motsCleInfo)
        }
    }
}

private fun parseRoleInfo(line: String): RoleInfo {
    val role = line.split(" ")[1].lowercase()
    val donneesPersonne = line.split(" : ")[1]
    val nom = donneesPersonne.split(",")[0]
    val numeroDeTelephone = donneesPersonne.split(",")[1].split(" ").joinToString("")
    return RoleInfo(role, nom, numeroDeTelephone)
}

private fun parseTypeTextoInfo(line: String): TypeTextoInfo {
    val extraitDeLigne = line.split(" : ")
    val nomDuType = extraitDeLigne[1]
    val motCles = extraitDeLigne[2].split(",").onEach { it.trim() }
    return TypeTextoInfo(nomDuType, motCles)
}

fun ajouterMotsCle(configuration: Configuration, motsCleInfo: TypeTextoInfo) {
    Log.d("Tests", "Ajouter : ${motsCleInfo.nomDuType}, ${motsCleInfo.motCles}")
    for (motCle in motsCleInfo.motCles) {
        configuration.typesDeTextos!!.ajouterMotCle(motsCleInfo.nomDuType, motCle)
    }
}

fun ajouterPersonneALaListeBlanche(configuration: Configuration, roleInfo: RoleInfo) {
    configuration.insererPersonne(
        Personne(
            role = roleInfo.role.trim(),
            nom = roleInfo.nom.trim(),
            numeroDeTelephone = roleInfo.numeroDeTelephone.trim()
        )
    )
}

fun ajouterTypeDeTexto(configuration: Configuration, typeTextoInfo: TypeTextoInfo) {
    configuration.typesDeTextos!!.ajouterTypeTexto(TypeTexto(typeTextoInfo.nomDuType, typeTextoInfo.motCles.toMutableSet()))
}

private fun modifier(configuration: Configuration, line: String) {
    val mots = line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val partiePersonne = line.split(" : ")[1]
        val personne = Personne(
            partiePersonne.split(",")[0],
            role,
            partiePersonne.split(",")[1].split(" ").joinToString("")
        )
        supprimerPersonne(personne.role!!, personne.nom, personne.numeroDeTelephone, configuration)
        ajouterPersonneALaListeBlanche(configuration, RoleInfo(role, personne.nom!!, personne.numeroDeTelephone))
    }
}

private fun supprimer(configuration: Configuration, line: String) {
    val mots = line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val donneesPersonne = line.split(" : ")[1]
        var nom: String? = null
        val numeroDeTelephone: String
        if (donneesPersonne.split(",").size == 2) {
            nom = donneesPersonne.split(",")[0]
            numeroDeTelephone = donneesPersonne.split(",")[1].split(" ").joinToString("")
        } else {
            numeroDeTelephone = donneesPersonne.split(" ").joinToString("")
        }
        supprimerPersonne(role, nom, numeroDeTelephone, configuration)
    } else if (mots[1].lowercase() == "type") {
        supprimerTypeTexto(line, configuration)
    } else if (mots[1].lowercase() + mots[2].lowercase() == "motsclés") {
        val extraitDeLigne = line.split(" : ")
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
