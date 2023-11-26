package fr.hurtiglastbil.modeles

import android.content.Context
import android.util.Log
import fr.hurtiglastbil.enumerations.JsonEnum
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

    override fun configurationDepuisJSONObject(json: JSONObject): Configuration {
        listeBlanche = ListeBlanche().creerUneListeBlancheDepuisTableauDeJSon(json.getJSONArray(JsonEnum.LISTE_BLANCHE.cle))
        tempsDeRafraichissment = json.getInt(JsonEnum.DELAI_DE_RAFRAICHISSEMENT.cle)
        typesDeTextos = ListeDesTypesDeTextos().creerDepuisJSONArray(json.getJSONArray(JsonEnum.TYPES_DE_TEXTO.cle))

        Log.d("Tests", "configurationDepuisJSONObject: $typesDeTextos")
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
        return json.toString()
    }

    override fun configurationDepuisAssets(cheminDuFichier: String): Configuration {
        val configStream: InputStream = context.assets.open(cheminDuFichier)
        val configString: String = configStream.reader().readText()
        return configurationDepuisJSON(configString)
    }

    override fun configurationDepuisFichierInterne(cheminDuFichier: String): Configuration {
        if (leFichierExiste(cheminDuFichier)) {
            val configStream: InputStream = context.openFileInput(cheminDuFichier)
            val configString: String = configStream.reader().readText()
            return configurationDepuisJSON(configString)
        }
        val config = configurationDepuisAssets(cheminDuFichier)
        config.sauvegarder(cheminDuFichier)
        return this
    }

    override fun insererPersonne(personne: Personne, cheminDuFichier: String) {
        this.listeBlanche!!.insererPersonne(personne)
        sauvegarder(cheminDuFichier)
    }

    override fun sauvegarder(cheminDuFichier: String) {
        val fichier = File(context.applicationContext.filesDir, cheminDuFichier)
        fichier.writeText(configurationVersJSON())
    }

    override fun leFichierExiste(cheminDuFichier: String): Boolean {
        return File(context.applicationContext.filesDir, cheminDuFichier).exists()
    }

    fun modifierTempsDeRafraichissement(tempsDeRafraichissement: Int, cheminDuFichier: String) {
        this.tempsDeRafraichissment = tempsDeRafraichissement
        Journaliseur.journaliserModificationDeLaConfiguration(
            TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.tag,
            "${TagsModificationConfig.MODIFICATION_TEMPS_RAFRAICHISSEMENT.message}: $tempsDeRafraichissement"
        )
        sauvegarder(cheminDuFichier)
    }
}