package fr.hurtiglastbil.interfaces

import fr.hurtiglastbil.modeles.Personne
import org.json.JSONArray

interface IListeBlanche {
    /**
     * Défini la liste blanche depuis un objet JSONArray
     *
     * @param[listeBlanche] La liste blanche
     */
    fun creerUneListeBlancheDepuisTableauDeJSon(listeBlanche: JSONArray) : IListeBlanche

    /**
     * Vérfie l'existance d'une personne dans la liste
     *
     * @param[personne] La personne à vérifier
     */
    fun estDansLaListeBlanche(personne: Personne): Boolean

    /**
     * Défini une personne si elle est présente dans la liste
     *
     * @param[personne] La personne à définir
     */
    fun creerPersonneSiInseree(personne: Personne): Personne?

    /**
     * Insère une nouvelle personne dans la liste blanche
     *
     * @param[personne] La personne à insérer
     */
    fun insererPersonne(personne: Personne)

    /**
     * Donne la liste blanche au format JSONArray
     *
     * @return La liste blanche
     */
    fun listeBlancheVersTableauJSON(): JSONArray
}