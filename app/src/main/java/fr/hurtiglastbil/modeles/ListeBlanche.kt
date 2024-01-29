package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.exceptions.TagsErreur
import fr.hurtiglastbil.gestionnaires.TagsModificationConfig
import fr.hurtiglastbil.interfaces.IListeBlanche
import fr.hurtiglastbil.utilitaires.Journaliseur
import org.json.JSONArray

class ListeBlanche : IListeBlanche {
    /**
     * La liste blanche au format d'un array
     */
    var listeBlanche: MutableSet<Personne> = mutableSetOf()
    override fun creerUneListeBlancheDepuisTableauDeJSon(listeBlanche: JSONArray) : ListeBlanche
    {
        for (i in 0 until listeBlanche.length()) {
            val nom = listeBlanche.getJSONObject(i).getString(JsonEnum.NOM_PERSONNE.cle)
            val role = listeBlanche.getJSONObject(i).getString(JsonEnum.ROLE_PERSONNE.cle)
            val numeroDeTelephone = listeBlanche.getJSONObject(i).getString(JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle)

            val personne = Personne(nom, role, numeroDeTelephone)

            this.listeBlanche.add(personne)
        }

        return this
    }

    override fun creerListeBlancheDepuisUneChaineDeCharacteres(listeBlanche: String) : ListeBlanche
    {
        val tableauJson = JSONArray(listeBlanche)
        return creerUneListeBlancheDepuisTableauDeJSon(tableauJson)
    }

    override fun estDansLaListeBlanche(personne: Personne): Boolean {
        for (personneDansLaListe in listeBlanche) {
            if (personneDansLaListe == personne) {
                return true
            }
        }
        return false
    }

    override fun creerPersonneSiInseree(personne: Personne): Personne {
        return if (estDansLaListeBlanche(personne)) {
            listeBlanche.find { it == personne }!!
        } else {
            personne
        }
    }

    fun supprimerPersonne(personne: Personne) {
        if (estDansLaListeBlanche(personne)) {
            Journaliseur.journaliserModificationDeLaConfiguration("Suppression personne", "supprimerPersonne: ${personne.numeroDeTelephone}")
            listeBlanche.remove(creerPersonneSiInseree(personne))
        } else {
            Journaliseur.journaliserErreur("Personne n'existe pas", "La personne avec le numéro suivant n'existe pas dans la liste blanche: ${personne.numeroDeTelephone}")
        }
    }

    override fun insererPersonne(personne: Personne) {
        if (!estDansLaListeBlanche(personne)) {
            if (personne.valider()) {
                listeBlanche.add(personne)
                Journaliseur.journaliserModificationDeLaConfiguration(
                    TagsModificationConfig.PERSONNE_AJOUTE.tag,
                    "Nouvelle personne: ${personne.nom}")
            } else {
                Journaliseur.journaliserErreur(TagsErreur.ERREUR_VALIDATION_PERSONNE.tag, TagsErreur.ERREUR_VALIDATION_PERSONNE.message + " donc elle n'est pas inséré.")
            }
        } else {
            Journaliseur.journaliserErreur("Personne existe", "La personne avec le numéro suivant existe déjà dans la liste blanche: ${personne.numeroDeTelephone}")
        }
    }

    override fun listeBlancheVersTableauJSON(): JSONArray {
        val jsonArray = JSONArray()
        listeBlanche.forEach {
            jsonArray.put(it.versJSON())
        }
        return jsonArray
    }
}