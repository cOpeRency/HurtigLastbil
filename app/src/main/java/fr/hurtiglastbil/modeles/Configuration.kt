package fr.hurtiglastbil.modeles

import android.content.Context
import android.util.Log
import fr.hurtiglastbil.enumerations.JsonEnum
import fr.hurtiglastbil.gestionnaires.updateGallery
import fr.hurtiglastbil.enumerations.TagsModificationConfig
import fr.hurtiglastbil.interfaces.IConfiguration
import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.utilitaires.Journaliseur
import org.json.JSONObject
import java.io.File
import java.io.InputStream

class Configuration(val context: Context) : IConfiguration {
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
    var utiliseStockageInterne : Boolean = true

    override fun configurationDepuisJSONObject(json: JSONObject): Configuration {
        listeBlanche = ListeBlanche().creerUneListeBlancheDepuisTableauDeJSon(json.getJSONArray(JsonEnum.LISTE_BLANCHE.cle))
        tempsDeRafraichissment = json.getInt(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle)
        typesDeTextos = ListeDesTypesDeTextos().creerDepuisJSONArray(json.getJSONArray(JsonEnum.TYPES_DE_TEXTO.cle))

        return this
    }

    override fun configurationVersJSONObject(): JSONObject {
        var json = JSONObject()
        json.put(JsonEnum.LISTE_BLANCHE.cle, listeBlanche!!.listeBlancheVersJSONArray())
        json.put(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle, tempsDeRafraichissment)
        json.put(JsonEnum.TYPES_DE_TEXTO.cle, typesDeTextos!!.listeDeMotsClesVersJSONArray())
        return json
    }

    override fun configurationDepuisJSON(json: String): Configuration {
        val jsonObject = JSONObject(json)
        return configurationDepuisJSONObject(jsonObject)
    }

    override fun configurationVersJSON(): String {
        var json = configurationVersJSONObject()
        return json.toString(4)
    }

    override fun configurationDepuisAssets(cheminDuFichier: String): Configuration {
        val configStream: InputStream = context.assets.open(cheminDuFichier)
        val configString: String = configStream.reader().readText()
        return configurationDepuisJSON(configString)
    }

    override fun configurationDepuisFichierInterne(cheminDuFichier: String): Configuration {
        if (leFichierExisteDansLeStockageInterne(cheminDuFichier)) {
            val configStream: InputStream = context.openFileInput(cheminDuFichier)
            val configString: String = configStream.reader().readText()
            return configurationDepuisJSON(configString)
        }
        val config = configurationDepuisAssets(cheminDuFichier)
        config.sauvegarder(cheminDuFichier)
        return this
    }

    override fun insererPersonne(personne: Personne) {
        this.listeBlanche!!.insererPersonne(personne)
    }

    override fun leFichierExisteDansLeStockageInterne(cheminDuFichier: String): Boolean {
        return File(context.applicationContext.filesDir, cheminDuFichier).exists()
    }

    fun modifierTempsDeRafraichissement(tempsDeRafraichissement: Int) {
        this.tempsDeRafraichissment = tempsDeRafraichissement
        Journaliseur.journaliserModificationDeLaConfiguration(
            TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.tag,
            "${TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.message}: $tempsDeRafraichissement"
        )
    }

    fun leFichierExisteDansLeStockageExterne(cheminDuFichier: String): Boolean {
        return File(context.getExternalFilesDir("hurtiglastbil"), cheminDuFichier).exists()
    }

    fun configurationDepuisStockageExterne(cheminDuFichier: String, subDir: String): Configuration {
        utiliseStockageInterne = false
        if (leFichierExisteDansLeStockageExterne("$subDir/$cheminDuFichier")) {
            val configStream: InputStream = File(context.getExternalFilesDir("hurtiglastbil"), subDir + "/" +cheminDuFichier).inputStream()
            val configString: String = configStream.reader().readText()
            return configurationDepuisJSON(configString)
        }
        val config = configurationDepuisAssets(cheminDuFichier)
        config.sauvegarder(cheminDuFichier, subDir)
        return this
    }

    override fun sauvegarder(cheminDuFichier: String, subDir: String?) {
        val cheminComplet = if (subDir != null) "$subDir/$cheminDuFichier" else cheminDuFichier
        if (utiliseStockageInterne) {
            val fichier = File(context.applicationContext.filesDir, cheminComplet)
            fichier.writeText(configurationVersJSON())
        } else {
            val fichier = File(context.getExternalFilesDir("hurtiglastbil"), cheminComplet)
            Log.d("Tests", "chemin du fichier : ${fichier.toString()}")
            if (subDir != null && !File(context.getExternalFilesDir("hurtiglastbil"), subDir).exists()) {
                File(context.getExternalFilesDir("hurtiglastbil"), subDir).mkdirs()
            }
            if (!fichier.exists()) {
                fichier.createNewFile()
            }
            fichier.writeText(configurationVersJSON())
            updateGallery(context, fichier, subDir)
        }
    }
}