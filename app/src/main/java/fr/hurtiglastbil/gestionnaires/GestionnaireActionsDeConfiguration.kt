package fr.hurtiglastbil.gestionnaires

import android.util.Log
import fr.hurtiglastbil.enumerations.Roles
import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto

data class Role(val value: String)
data class Nom(val value: String)
data class NumeroDeTelephone(val value: String)

data class RoleInfo(val role: Role, val nom: Nom, val numeroDeTelephone: NumeroDeTelephone)

data class TypeTextoInfo(val nomDuType: String, val motCles: List<String>)

data class ConfigurationLigne(val line: String, val configuration: Configuration)

fun actionsConfiguration(corpsDuMessage: String, configuration: Configuration) {
    corpsDuMessage.split("\n").filterNot { it.startsWith("CONFIG") || it.startsWith("=") }
        .forEach { ligneConfiguration(ConfigurationLigne(it, configuration)) }

    configuration.sauvegarder(CheminFichier("configuration.dev.json", "config"))
}

private fun ligneConfiguration(configurationLigne: ConfigurationLigne) {
    val words = configurationLigne.line.split(" ")
    when (words[0].lowercase()) {
        "ajouter" -> ajouter(configurationLigne)
        "modifier" -> modifier(configurationLigne)
        "supprimer" -> supprimer(configurationLigne)
        "réinitialiser" -> reinitialiser(configurationLigne)
    }
}

private fun reinitialiser(configurationLigne: ConfigurationLigne) {
    val mots = configurationLigne.line.split(" ").drop(1).joinToString(" ").split(",").map { it.trim() }
    configurationLigne.configuration.reinitialiser(
        Personne(
            Role(mots[0]).value,
            Roles.ADMINISTRATEUR.motCle,
            NumeroDeTelephone(mots[1].split(" ").joinToString("")).value
        )
    )
}

private fun ajouter(configurationLigne: ConfigurationLigne) {
    val mots = configurationLigne.line.split(" ")
    when {
        Roles.estUnRole(mots[1].lowercase()) -> {
            val roleInfo = parseRoleInfo(configurationLigne.line)
            ajouterPersonneALaListeBlanche(configurationLigne.configuration, roleInfo)
        }
        mots[1].lowercase() == "type" -> {
            val typeTextoInfo = parseTypeTextoInfo(configurationLigne.line)
            ajouterTypeDeTexto(configurationLigne.configuration, typeTextoInfo)
        }
        (mots[1].lowercase() + mots[2].lowercase()) == "motsclé" -> {
            val motsCleInfo = parseTypeTextoInfo(configurationLigne.line)
            ajouterMotsCle(configurationLigne.configuration, motsCleInfo)
        }
    }
}

private fun parseRoleInfo(ligne: String): RoleInfo {
    val role = Role(ligne.split(" ")[1].lowercase())
    val donneesPersonne = ligne.split(" : ")[1]
    val nom = Nom(donneesPersonne.split(",")[0])
    val numeroDeTelephone = NumeroDeTelephone(donneesPersonne.split(",")[1].split(" ").joinToString(""))
    return RoleInfo(role, nom, numeroDeTelephone)
}

private fun parseTypeTextoInfo(ligne: String): TypeTextoInfo {
    val extraitDeLigne = ligne.split(" : ")
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

private fun modifier(configurationLigne: ConfigurationLigne) {
    val mots = configurationLigne.line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val partiePersonne = configurationLigne.line.split(" : ")[1]
        val personne = Personne(
            partiePersonne.split(",")[0],
            role,
            partiePersonne.split(",")[1].split(" ").joinToString("")
        )
        supprimerPersonne(personne.role!!, personne.nom, personne.numeroDeTelephone, configurationLigne.configuration)
        ajouterPersonneALaListeBlanche(configurationLigne.configuration, RoleInfo(Role(role), Nom(personne.nom!!), NumeroDeTelephone(personne.numeroDeTelephone)))
    }
}

private fun supprimer(configurationLigne: ConfigurationLigne) {
    val mots = configurationLigne.line.split(" ")
    if (Roles.estUnRole(mots[1].lowercase())) {
        val role = Roles.obtenirRole(mots[1].lowercase())!!.motCle
        val donneesPersonne = configurationLigne.line.split(" : ")[1]
        var nom: String? = null
        val numeroDeTelephone: String
        if (donneesPersonne.split(",").size == 2) {
            nom = donneesPersonne.split(",")[0]
            numeroDeTelephone = donneesPersonne.split(",")[1].split(" ").joinToString("")
        } else {
            numeroDeTelephone = donneesPersonne.split(" ").joinToString("")
        }
        supprimerPersonne(role, nom, numeroDeTelephone, configurationLigne.configuration)
    } else if (mots[1].lowercase() == "type") {
        supprimerTypeTexto(configurationLigne)
    } else if (mots[1].lowercase() + mots[2].lowercase() == "motsclés") {
        val extraitDeLigne = configurationLigne.line.split(" : ")
        val motsCles = extraitDeLigne[2].split(",").onEach { it.trim() }
        for (motCle in motsCles) {
            configurationLigne.configuration.typesDeTextos!!.supprimerMotCle(extraitDeLigne[1], motCle)
        }
    }
}

private fun supprimerTypeTexto(configurationLigne: ConfigurationLigne) {
    val extraitDeLigne = configurationLigne.line.split(" : ")
    configurationLigne.configuration.typesDeTextos!!.supprimerTypeTexto(extraitDeLigne[1])
}

private fun supprimerPersonne(role: String, nom: String?, numeroDeTelephone: String, configuration: Configuration) {
    val personne = Personne(
        role = role,
        nom = nom,
        numeroDeTelephone = numeroDeTelephone
    )
    configuration.listeBlanche!!.supprimerPersonne(personne)
}
