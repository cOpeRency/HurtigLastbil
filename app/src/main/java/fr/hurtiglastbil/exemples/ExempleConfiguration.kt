package fr.hurtiglastbil.exemples

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.hurtiglastbil.enumerations.TagsErreur
import fr.hurtiglastbil.exceptions.ExceptionNouveauMotCle
import fr.hurtiglastbil.exceptions.ExceptionNouveauTypeTexto
import fr.hurtiglastbil.exceptions.ExceptionSuppressionMotCle
import fr.hurtiglastbil.exceptions.ExceptionSuppressionTypeTexto
import fr.hurtiglastbil.modeles.Configuration
import fr.hurtiglastbil.modeles.ListeBlanche
import fr.hurtiglastbil.modeles.Personne
import fr.hurtiglastbil.modeles.texto.TypeTexto
import org.json.JSONArray

class ExempleConfiguration : AppCompatActivity() {
    val TAG = "Tests"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main) // Assurez-vous que le nom du layout correspond au vôtre

        // Votre code initial ici

        val nom = "John Doe"
        val role = "CEO"
        val numero = "0123456789"
        val stringJsonListeBlanche = "[{\"nom\":\"${nom}\",\"role\":\"${role}\",\"numero\":\"${numero}\"}]"
        val stringJsonTypeDeTexto = "[{\"rdv livraison\":[\"livraison\",\"sodimat\",\"ecoval\",\"7h\",\"9h\",\"retour depot\"]}, {\"rdv remorque vide\":[\"remorque vide\",\"gueret\",\"demain\"]}]"
        val stringJsonConfiguration ="{\"liste blanche\" : ${stringJsonListeBlanche}, \"temps de rafraichissement\": 10, \"types de texto\": ${stringJsonTypeDeTexto}}"
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
        config.insererPersonne(Personne("Michel", "Camionneur", "0623456799"))
        Log.d(TAG, "Liste blanche avec michel: ${config.listeBlanche!!.listeBlanche}")

        Log.d(TAG, "Y'a bien michel: ${config.listeBlanche!!.estDansLaListeBlanche(Personne(numeroDeTelephone = "0623456799"))}")
        Log.d(TAG, "Michel par son num: ${config.listeBlanche!!.creerPersonneSiInseree(Personne(numeroDeTelephone = "0623456799"))}")

        Log.d(TAG, "Essaie mauvaise insertion avec le nom vide: ")
        config.insererPersonne(Personne("", "Camionneur", "0619752459"))
        Log.d(TAG, "Essaie mauvaise insertion avec le nom trop court:")
        config.insererPersonne(Personne("j", "Camionneur", "0619752459"))
        Log.d(TAG, "Essaie mauvaise insertion avec le nom mal écrit:")
        config.insererPersonne(Personne("Jean-Pierre Culot 1234", "Camionneur", "0619752459"))
        Log.d(TAG, "Essaie mauvaise insertion avec le role vide:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "", "0619752459"))
        Log.d(TAG, "Essaie mauvaise insertion avec le num vide:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", ""))
        Log.d(TAG, "Essaie mauvaise insertion avec le num trop long:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "06234567890"))
        Log.d(TAG, "Essaie mauvaise insertion avec le num trop court:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "062345678"))
        Log.d(TAG, "Essaie mauvaise insertion avec le num qui est ni 06 ni 07:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "0123456789"))
        Log.d(TAG, "Essaie mauvaise insertion avec le num qui ne commence pas par 0:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "1223456789"))
        Log.d(TAG, "Insertion de Jean-Pierre Culot:")
        config.insererPersonne(Personne("Jean-Pierre Culot", "Camionneur", "0623456789"))
        Log.d(TAG, "Liste blanche avec Jean-Pierre Culot: ${config.listeBlanche!!.estDansLaListeBlanche(Personne(numeroDeTelephone = "0623456789"))}")
        Log.d(TAG, "Jean-Pierre Culot par son num: ${config.listeBlanche!!.creerPersonneSiInseree(Personne(numeroDeTelephone = "0623456789"))}")

        Log.d(TAG, "Modification du temps de raffraichissement:")
        config.modifierTempsDeRafraichissement(20)
        Log.d(TAG, "Temps de raffraichissement: ${config.tempsDeRafraichissment}")

        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "Ajout d'un type de texto:")
        config.typesDeTextos!!.ajouterTypeTexto(TypeTexto("rdv test", mutableSetOf("test", "rdv")))
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "Suppression d'un type de texto:")
        config.typesDeTextos!!.supprimerTypeTexto("rdv test")
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "Ajout d'un mot clé à un type de texto:")
        config.typesDeTextos!!.recupererTypeTexto("rdv livraison")!!.ajouterMotCle("test")
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "On ne double pas l'ajout d'un mot clé à un type de texto:")
        try {
            config.typesDeTextos!!.recupererTypeTexto("rdv livraison")!!.ajouterMotCle("test")
        } catch (e: ExceptionNouveauMotCle) {
            Log.e(TagsErreur.ERREUR_AJOUT_MOT_CLE.tag, e.message + " pour le type suivant : rdv livraison")
        }
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "Suppression d'un mot clé à un type de texto:")
        config.typesDeTextos!!.recupererTypeTexto("rdv livraison")!!.supprimerMotCle("test")
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")
        Log.d(TAG, "On ne supprime pas un mot clé qui n'existe pas à un type de texto:")
        try {
            config.typesDeTextos!!.recupererTypeTexto("rdv livraison")!!.supprimerMotCle("test")
        } catch (e: ExceptionSuppressionMotCle) {
            Log.e(TagsErreur.ERREUR_SUPPRESSION_MOT_CLE.tag, e.message + " pour le type suivant : rdv livraison")
        }
        Log.d(TAG, "Liste des types de texto: ${config.typesDeTextos}")

        Log.d(TAG, "Doit envoyer une exception car le type de texto n'est pas valide:")
        try {
            config.typesDeTextos!!.ajouterTypeTexto(TypeTexto("rdv test", mutableSetOf("")))
        } catch (e: ExceptionNouveauTypeTexto) {
            Log.e(TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.tag, e.message + " pour le type suivant : rdv test")
        }
        Log.d(TAG, "Doit envoyer une exception car le type de texto n'est pas valide: rdv test")
        try {
            config.typesDeTextos!!.supprimerTypeTexto("rdv test")
        } catch (e: ExceptionSuppressionTypeTexto) {
            Log.e(TagsErreur.ERREUR_AJOUT_TYPE_TEXTO.tag, e.message + " pour le type suivant : rdv test")
        }
        Log.d(TAG, "Doit envoyer une exception car le mot cle existe déja :")
        config.typesDeTextos!!.ajouterMotCle("rdv livraison","livraison")
        Log.d(TAG, "Doit envoyer une exception car le mot cle n'existe pas :")
        config.typesDeTextos!!.supprimerMotCle("rdv livraison","test")
    }

}