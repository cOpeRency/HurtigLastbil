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
        Log.d(TAG, "Temps de rafraichissement du config: ${configuration.tempsDeRaffraichissment}")
        Log.d(TAG, "Liste blanche du config: ${configuration.listeBlanche!!.listeBlanche}")

        val configurationDepuisAssets = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        Log.d(TAG, "Temps de rafraichissement du config depuis assets: ${configurationDepuisAssets.tempsDeRaffraichissment}")
        Log.d(TAG, "Liste blanche du config depuis assets: ${configurationDepuisAssets.listeBlanche!!.listeBlanche}")

        val configurationDepuisFichierInterne = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        Log.d(TAG, "Temps de rafraichissement du config depuis fichier interne: ${configurationDepuisFichierInterne.tempsDeRaffraichissment}")
        Log.d(TAG, "Liste blanche du config depuis fichier interne: ${configurationDepuisFichierInterne.listeBlanche!!.listeBlanche}")

        val config = Configuration(this).configurationDepuisFichierInterne(cheminDuFichierDeConfiguration)
        config.insererPersonne(Personne("Michel", "Camionneur", "01234567899"), cheminDuFichierDeConfiguration)
        Log.d(TAG, "Liste blanche avec michel: ${config.listeBlanche!!.listeBlanche}")
    }

}