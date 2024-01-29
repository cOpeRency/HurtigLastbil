package fr.hurtiglastbil.modeles

import android.content.Context
import android.util.Log
import fr.hurtiglastbil.enumerations.JsonEnum
import fr.hurtiglastbil.gestionnaires.miseAJourGallerie
import fr.hurtiglastbil.enumerations.TagsModificationConfig
import fr.hurtiglastbil.interfaces.IConfiguration
import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.utilitaires.Journaliseur
import org.json.JSONObject
import java.io.File
import java.io.InputStream

private const val title = "hurtiglastbil"

class Configuration(private val contexte: Context) : IConfiguration {
    /**
     * Liste blanche des numéros de téléphone autorisés
     */
    var listeBlanche : ListeBlanche? = null

    /**
     * Temps de rafraichissment entre enregistrement des textos
     */
    var tempsDeRafraichissment : Int? = null

    /**
     * Ensemble des mots clés
     */
    var typesDeTextos : ListeDesTypesDeTextos? = null

    /**
     * Permet de savoir si l'application utilise le stockage interne ou externe
     */
    private var utiliseStockageInterne : Boolean = true

    override fun configurationDepuisObjetJSON(json: JSONObject): Configuration {
        listeBlanche = ListeBlanche().creerUneListeBlancheDepuisTableauDeJSon(json.getJSONArray(JsonEnum.LISTE_BLANCHE.cle))
        tempsDeRafraichissment = json.getInt(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle)
        typesDeTextos = ListeDesTypesDeTextos().creerDepuisJSONArray(json.getJSONArray(JsonEnum.TYPES_DE_TEXTO.cle))

        return this
    }

    override fun configurationVersObjetJSON(): JSONObject {
        val json = JSONObject()
        json.put(JsonEnum.LISTE_BLANCHE.cle, listeBlanche!!.listeBlancheVersTableauJSON())
        json.put(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle, tempsDeRafraichissment)
        json.put(JsonEnum.TYPES_DE_TEXTO.cle, typesDeTextos!!.listeDeMotsClesVersTableauJSON())
        return json
    }

    override fun configurationDepuisJSON(json: String): Configuration {
        val objetJson = JSONObject(json)
        return configurationDepuisObjetJSON(objetJson)
    }

    override fun configurationVersJSON(): String {
        val json = configurationVersObjetJSON()
        return json.toString(4)
    }

    override fun configurationDepuisAssets(cheminDuFichier: String): Configuration {
        val fluxDeConfiguration: InputStream = contexte.assets.open(cheminDuFichier)
        val chaineDeConfiguration: String = fluxDeConfiguration.reader().readText()
        return configurationDepuisJSON(chaineDeConfiguration)
    }

    override fun insererPersonne(personne: Personne) {
        this.listeBlanche!!.insererPersonne(personne)
    }

    override fun leFichierExisteDansLeStockageInterne(cheminDuFichier: String): Boolean {
        return File(contexte.applicationContext.filesDir, cheminDuFichier).exists()
    }

    fun modifierTempsDeRafraichissement(rafraichissement: Rafraichissement) {
        this.tempsDeRafraichissment = rafraichissement.temps
        Journaliseur.journaliserModificationDeLaConfiguration(
            TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.tag,
            "${TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.message}: ${rafraichissement.temps}"
        )
    }

    fun leFichierExisteDansLeStockageExterne(cheminDuFichier: String): Boolean {
        return File(contexte.getExternalFilesDir(title), cheminDuFichier).exists()
    }

    override fun sauvegarder(cheminDuFichier: CheminFichier) {
        val cheminComplet = if (cheminDuFichier.sousDossier != null) "${cheminDuFichier.sousDossier}/${cheminDuFichier.cheminDuFichier}" else cheminDuFichier.cheminDuFichier
        if (utiliseStockageInterne) {
            val fichier = File(contexte.applicationContext.filesDir, cheminComplet)
            fichier.writeText(configurationVersJSON())
        } else {
            val fichier = File(contexte.getExternalFilesDir(title), cheminComplet)
            Log.d("Tests", "chemin du fichier : ${fichier.toString()}")
            if (cheminDuFichier.sousDossier != null && !File(contexte.getExternalFilesDir(title), cheminDuFichier.sousDossier).exists()) {
                File(contexte.getExternalFilesDir(title), cheminDuFichier.sousDossier).mkdirs()
            }
            if (!fichier.exists()) {
                fichier.createNewFile()
            }
            fichier.writeText(configurationVersJSON())
            miseAJourGallerie(contexte, fichier, cheminDuFichier.sousDossier)
        }
    }

    fun reinitialiser(administarateur: Personne) {
        listeBlanche = ListeBlanche()
        insererPersonne(administarateur)
        tempsDeRafraichissment = 5
        typesDeTextos = ListeDesTypesDeTextos()
    }

    private fun chargerConfigurationDepuisStockage(cheminFichier: CheminFichier, estInterne: Boolean): Configuration {
        val repertoireDeFichier = if (estInterne) {
            contexte.applicationContext.filesDir
        } else {
            contexte.getExternalFilesDir(title)
        }

        val cheminComplet = if (cheminFichier.sousDossier != null) "${cheminFichier.sousDossier}/${cheminFichier.cheminDuFichier}" else cheminFichier.cheminDuFichier
        val fichier = File(repertoireDeFichier, cheminComplet)

        if (fichier.exists()) {
            val fluxDeConfiguration: InputStream = fichier.inputStream()
            val chaineDeConfiguration: String = fluxDeConfiguration.reader().readText()
            return configurationDepuisJSON(chaineDeConfiguration)
        }

        val config = configurationDepuisAssets(cheminFichier.cheminDuFichier)
        config.sauvegarder(cheminFichier)
        return this
    }

    override fun configurationDepuisFichierInterne(cheminDuFichier: CheminFichier): Configuration {
        return chargerConfigurationDepuisStockage(cheminDuFichier, true)
    }

    override fun configurationDepuisStockageExterne(cheminDuFichier: CheminFichier): Configuration {
        utiliseStockageInterne = false
        return chargerConfigurationDepuisStockage(cheminDuFichier, false)
    }

}