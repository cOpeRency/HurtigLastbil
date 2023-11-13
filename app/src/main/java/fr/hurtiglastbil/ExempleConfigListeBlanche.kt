package fr.hurtiglastbil

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.ListeBlanche
import fr.hurtiglastbil.modeles.Personne
import org.json.JSONArray

class ExempleConfigListeBlanche : AppCompatActivity() {
    val TAG = "Tests"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main) // Assurez-vous que le nom du layout correspond au vôtre

        // Votre code initial ici

        val nom = "John Doe"
        val role = "CEO"
        val numero = "0123456789"
        val stringJsonListeBlanche = "[{\"nom\":\"${nom}\",\"role\":\"${role}\",\"numero\":\"${numero}\"}]"
        val stringJsonConfiguration ="{\"liste blanche\" : ${stringJsonListeBlanche}, \"temps de rafraichissement\": 10}"
        val cheminDuFichierDeConfiguration = "configuration.dev.json"

        val personne = Personne(nom, role, numero)
        Log.d(TAG, "Personne: ${personne.nom} ${personne.role} ${personne.numeroDeTelephone}")

        val jsonArray = JSONArray(stringJsonListeBlanche)
        Log.d(TAG, "JsonArray: ${jsonArray.getJSONObject(0).getString("nom")}")
        val listeBlanche = ListeBlanche().creerUneListeBlancheDepuisTableauDeJSon(jsonArray)
        Log.d(TAG, "Liste blanche: ${listeBlanche.listeBlanche}")

        val configuration = Configuration(this).configurationDepuisJSON(stringJsonConfiguration)
        Log.d(TAG, "Temps de rafraichissement du config: ${configuration.tempsDeRafraichissment}")
        Log.d(TAG, "Liste blanche du config: ${configuration.listeBlanche!!.listeBlanche}")

        val configurationDepuisAssets = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        Log.d(TAG, "Temps de rafraichissement du config depuis assets: ${configurationDepuisAssets.tempsDeRafraichissment}")
        Log.d(TAG, "Liste blanche du config depuis assets: ${configurationDepuisAssets.listeBlanche!!.listeBlanche}")

        val configurationDepuisFichierInterne = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        Log.d(TAG, "Temps de rafraichissement du config depuis fichier interne: ${configurationDepuisFichierInterne.tempsDeRafraichissment}")
        Log.d(TAG, "Liste blanche du config depuis fichier interne: ${configurationDepuisFichierInterne.listeBlanche!!.listeBlanche}")

        val config = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        config.insererPersonne(Personne("Michel", "Camionneur", "0623456799"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Liste blanche avec michel: ${config.listeBlanche!!.listeBlanche}")

        Log.d(TAG, "Y'a bien michel: ${config.listeBlanche!!.estDansLaListeBlanche(Personne(numeroDeTelephone = "0623456799"))}")
        Log.d(TAG, "Michel par son num: ${config.listeBlanche!!.creerPersonneSiInseree(Personne(numeroDeTelephone = "0623456799"))}")

        Log.d(TAG, "Essaie mauvaise insertion avec le nom vide: ")
        config.insererPersonne(Personne("", "Camionneur", "0619752459"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le nom trop court:")
        config.insererPersonne(Personne("j", "Camionneur", "0619752459"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le nom mal écrit:")
        config.insererPersonne(Personne("Jean-Pierre Culot 1234", "Camionneur", "0619752459"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le role vide:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "", "0619752459"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le num vide:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", ""), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le num trop long:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "06234567890"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le num trop court:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "062345678"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le num qui est ni 06 ni 07:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "0123456789"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Essaie mauvaise insertion avec le num qui ne commence pas par 0:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "1223456789"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Insertion de Jean-Pierre Culot:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "+33623456789"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Liste blanche avec Jean-Pierre Culot: ${config.listeBlanche!!.estDansLaListeBlanche(Personne(numeroDeTelephone = "+33623456789"))}")
        Log.d(TAG, "Jean-Pierre Culot par son num: ${config.listeBlanche!!.creerPersonneSiInseree(Personne(numeroDeTelephone = "+33623456789"))}")
    }

}