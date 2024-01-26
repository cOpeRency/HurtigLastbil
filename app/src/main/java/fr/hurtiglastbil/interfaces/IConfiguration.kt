package fr.hurtiglastbil.interfaces

import fr.hurtiglastbil.modeles.CheminFichier
import fr.hurtiglastbil.modeles.Personne
import org.json.JSONObject

interface IConfiguration {
    /**
     * Défini le fichier de configuration depuis un objet JSONObject
     *
     * @param[json] Le fichier de configuration
     */
    fun configurationDepuisJSONObject(json: JSONObject): IConfiguration

    /**
     * Donne le fichier de configuration au format JSONObject
     *
     * @return Le fichier de configuration
     */
    fun configurationVersJSONObject(): JSONObject

    /**
     * Défini le fichier de configuration depuis une chaîne de caractères (écrit au format json)
     *
     * @param[json] Le fichier de configuration
     */
    fun configurationDepuisJSON(json: String): IConfiguration

    /**
     * Donne le fichier de configuration  au format chaîne de caractères (écrit au format json)
     *
     * @return Le fichier de configuration
     */
    fun configurationVersJSON(): String


    /**
     * Défini le fichier de configuration depuis un fichier mis dans les assets
     *
     * @param[cheminDuFichier] Le chemin vers le fichier
     */
    fun configurationDepuisAssets(cheminDuFichier: String): IConfiguration

    /**
     * Sauvegarde le fichier de configuration
     *
     * @param[cheminDuFichier] Le chemin vers le fichier
     */
    fun sauvegarder(cheminDuFichier: CheminFichier)

    /**
     * Vérifie l'existance du fichier de configuration
     *
     * @param[cheminDuFichier] Le chemin vers le fichier
     * @return true si le fichier existe, false sinon
     */
    fun leFichierExisteDansLeStockageInterne(cheminDuFichier: String): Boolean

    /**
     * Défini le fichier de configuration depuis un fichier interne
     *
     * @param[cheminDuFichier] Le chemin vers le fichier
     */
    fun configurationDepuisFichierInterne(cheminDuFichier: CheminFichier): IConfiguration

    /**
     * Défini le fichier de configuration depuis un fichier externe
     *
     * @param[cheminDuFichier] Le chemin vers le fichier
     */
    fun configurationDepuisStockageExterne(cheminDuFichier: CheminFichier): IConfiguration



    /**
     * Insère une nouvelle personne dans la liste blanche
     *
     * @param[personne] La personne à insérer
     */
    fun insererPersonne(personne: Personne)
}