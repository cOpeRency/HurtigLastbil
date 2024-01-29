package fr.hurtiglastbil.gestionnaires

import android.util.Log
import fr.hurtiglastbil.modeles.Roles
import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto

data class Role(val value: String)
data class Nom(val value: String)
data class NumeroDeTelephone(val value: String)

data class RoleInfo(val role: Role, val nom: Nom, val numeroDeTelephone: NumeroDeTelephone)

data class TypeTextoInfo(val nomDuType: String, val motCles: List<String>)

data class ConfigurationLine(val line: String, val configuration: Configuration)

fun actionsConfiguration(corpsDuMessage: String, configuration: Configuration) {
    corpsDuMessage.split("\n").filterNot { it.startsWith("CONFIG") || it.startsWith("=") }
        .forEach { ligneConfiguration(ConfigurationLine(it, configuration)) }

    configuration.sauvegarder(CheminFichier("configuration.dev.json", "config"))
}

private fun ligneConfiguration(configurationLine: ConfigurationLine) {
    val words = configurationLine.line.split(" ")
    when (words[0].lowercase()) {
        "ajouter" -> ajouter(configurationLine)
        "modifier" -> modifier(configurationLine)
        "supprimer" -> supprimer(configurationLine)
        "réinitialiser" -> reinitialiser(configurationLine)
    }
}

private fun reinitialiser(configurationLine: ConfigurationLine) {
    val mots = configurationLine.line.split(" ").drop(1).joinToString(" ").split(",").map { it.trim() }
    configurationLine.configuration.reinitialiser(
        Personne(
            Role(mots[0]).value,
            Roles.ADMINISTRATEUR.motCle,
            NumeroDeTelephone(mots[1].split(" ").joinToString("")).value
        )
    )
}

private fun ajouter(configurationLine: ConfigurationLine) {
    val mots = configurationLine.line.split(" ")
    when {
        Roles.estUnRole(mots[1].lowercase()) -> {
            val roleInfo = parseRoleInfo(configurationLine.line)
            ajouterPersonneALaListeBlanche(configurationLine.configuration, roleInfo)
        }
        mots[1].lowercase() == "type" -> {
            val typeTextoInfo = parseTypeTextoInfo(configurationLine.line)
            ajouterTypeDeTexto(configurationLine.configuration, typeTextoInfo)
        }
        (mots[1].lowercase() + mots[2].lowercase()) == "motsclé" -> {
            val motsCleInfo = parseTypeTextoInfo(configurationLine.line)
            ajouterMotsCle(configurationLine.configuration, motsCleInfo)
        }
    }
}

private fun parseRoleInfo(line: String): RoleInfo {
    val role = Role(line.split(" ")[1].lowercase())
    val donneesPersonne = line.split(" : ")[1]
    val nom = Nom(donneesPersonne.split(",")[0])
    val numeroDeTelephone = NumeroDeTelephone(donneesPersonne.split(",")[1].split(" ").joinToString(""))
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
            role = roleInfo.role.value,
            nom = roleInfo.nom.value,
            numeroDeTelephone = roleInfo.numeroDeTelephone.value
        )
    )
}

fun ajouterTypeDeTexto(configuration: Configuration, typeTextoInfo: TypeTextoInfo) {
    configuration.typesDeTextos!!.ajouterTypeTexto(TypeTexto(typeTextoInfo.nomDuType, typeTextoInfo.motCles.toMutableSet()))
}

private fun modifier(configurationLine: ConfigurationLine) {
    val mots = configurationLine.line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val partiePersonne = configurationLine.line.split(" : ")[1]
        val personne = Personne(
            partiePersonne.split(",")[0],
            role,
            partiePersonne.split(",")[1].split(" ").joinToString("")
        )
        supprimerPersonne(personne.role!!, personne.nom, personne.numeroDeTelephone, configurationLine.configuration)
        ajouterPersonneALaListeBlanche(configurationLine.configuration, RoleInfo(Role(role), Nom(personne.nom!!), NumeroDeTelephone(personne.numeroDeTelephone)))
    }
}

private fun supprimer(configurationLine: ConfigurationLine) {
    val mots = configurationLine.line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val donneesPersonne = configurationLine.line.split(" : ")[1]
        var nom: String? = null
        val numeroDeTelephone: String
        if (donneesPersonne.split(",").size == 2) {
            nom = donneesPersonne.split(",")[0]
            numeroDeTelephone = donneesPersonne.split(",")[1].split(" ").joinToString("")
        } else {
            numeroDeTelephone = donneesPersonne.split(" ").joinToString("")
        }
        supprimerPersonne(role, nom, numeroDeTelephone, configurationLine.configuration)
    } else if (mots[1].lowercase() == "type") {
        supprimerTypeTexto(configurationLine)
    } else if (mots[1].lowercase() + mots[2].lowercase() == "motsclés") {
        val extraitDeLigne = configurationLine.line.split(" : ")
        val motsCles = extraitDeLigne[2].split(",").onEach { it.trim() }
        for (motCle in motsCles) {
            configurationLine.configuration.typesDeTextos!!.supprimerMotCle(extraitDeLigne[1], motCle)
        }
    }
}

private fun supprimerTypeTexto(configurationLine: ConfigurationLine) {
    val extraitDeLigne = configurationLine.line.split(" : ")
    configurationLine.configuration.typesDeTextos!!.supprimerTypeTexto(extraitDeLigne[1])
}

private fun supprimerPersonne(role: String, nom: String?, numeroDeTelephone: String, configuration: Configuration) {
    val personne = Personne(
        role = role,
        nom = nom,
        numeroDeTelephone = numeroDeTelephone
    )
    configuration.listeBlanche!!.supprimerPersonne(personne)
}
