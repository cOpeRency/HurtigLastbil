package fr.hurtiglastbil.modeles

import android.content.Context
import android.util.Log
import fr.hurtiglastbil.gestionnaires.updateGallery
import fr.hurtiglastbil.interfaces.IConfiguration
import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import org.json.JSONObject
import java.io.File
import java.io.InputStream

private const val title = "hurtiglastbil"

class Configuration(private val context: Context) : IConfiguration {
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

    override fun configurationDepuisJSONObject(json: JSONObject): Configuration {
        listeBlanche = ListeBlanche().creerUneListeBlancheDepuisTableauDeJSon(json.getJSONArray(
            JsonEnum.LISTE_BLANCHE.cle))
        tempsDeRafraichissment = json.getInt(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle)
        typesDeTextos = ListeDesTypesDeTextos().creerDepuisJSONArray(json.getJSONArray(JsonEnum.TYPES_DE_TEXTO.cle))

        return this
    }

    override fun configurationVersJSONObject(): JSONObject {
        val json = JSONObject()
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
        val json = configurationVersJSONObject()
        return json.toString(4)
    }

    override fun configurationDepuisAssets(cheminDuFichier: String): Configuration {
        val configStream: InputStream = context.assets.open(cheminDuFichier)
        val configString: String = configStream.reader().readText()
        return configurationDepuisJSON(configString)
    }

    override fun insererPersonne(personne: Personne) {
        this.listeBlanche!!.insererPersonne(personne)
    }

    override fun sauvegarder(cheminDuFichier: CheminFichier) {
        val cheminComplet = if (cheminDuFichier.subDir != null) "${cheminDuFichier.subDir}/${cheminDuFichier.cheminDuFichier}" else cheminDuFichier.cheminDuFichier
        if (utiliseStockageInterne) {
            val fichier = File(context.applicationContext.filesDir, cheminComplet)
            fichier.writeText(configurationVersJSON())
        } else {
            val fichier = File(context.getExternalFilesDir(title), cheminComplet)
            Log.d("Tests", "chemin du fichier : ${fichier.toString()}")
            if (cheminDuFichier.subDir != null && !File(context.getExternalFilesDir(title), cheminDuFichier.subDir).exists()) {
                File(context.getExternalFilesDir(title), cheminDuFichier.subDir).mkdirs()
            }
            if (!fichier.exists()) {
                fichier.createNewFile()
            }
            fichier.writeText(configurationVersJSON())
            updateGallery(context, fichier, cheminDuFichier.subDir)
        }
    }

    fun reinitialiser(administarateur: Personne) {
        listeBlanche = ListeBlanche()
        insererPersonne(administarateur)
        tempsDeRafraichissment = 5
        typesDeTextos = ListeDesTypesDeTextos()
    }

    private fun loadConfigurationFromStorage(cheminFichier: CheminFichier, isInternal: Boolean): Configuration {
        val fileDirectory = if (isInternal) {
            context.applicationContext.filesDir
        } else {
            context.getExternalFilesDir(title)
        }

        val fullPath = if (cheminFichier.subDir != null) "${cheminFichier.subDir}/${cheminFichier.cheminDuFichier}" else cheminFichier.cheminDuFichier
        val file = File(fileDirectory, fullPath)

        if (file.exists()) {
            val configStream: InputStream = file.inputStream()
            val configString: String = configStream.reader().readText()
            return configurationDepuisJSON(configString)
        }

        val config = configurationDepuisAssets(cheminFichier.cheminDuFichier)
        config.sauvegarder(cheminFichier)
        return this
    }

    override fun configurationDepuisStockageExterne(cheminDuFichier: CheminFichier): Configuration {
        utiliseStockageInterne = false
        return loadConfigurationFromStorage(cheminDuFichier, false)
    }

}