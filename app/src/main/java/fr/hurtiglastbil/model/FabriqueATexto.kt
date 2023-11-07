package fr.hurtiglastbil.model

import fr.hurtiglastbil.model.texto.TextoRendezVousLivraison
import fr.hurtiglastbil.model.texto.Texto
import fr.hurtiglastbil.model.texto.TextoIndefini
import java.text.Normalizer
import java.util.Date

class FabriqueATexto {
    fun creerTexto(envoyeur: String, receveur: String, date: Date, contenu: String): Texto {
        if(estRendezVousDeLivraison(envoyeur, contenu)){
            return TextoRendezVousLivraison(envoyeur, receveur, date, contenu)
        }
        return TextoIndefini(envoyeur, receveur, date, contenu)
    }

    private fun rechercherMotsCles(contenu: String, motsCles: Array<String>): Boolean {
        val contenuEnMinuscule = Normalizer.normalize(contenu.lowercase(), Normalizer.Form.NFD)
        motsCles.forEach { keyword ->
            if(contenuEnMinuscule.contains(keyword)) return true
        }
        return false
    }

    private fun estRendezVousDeLivraison(envoyeur: String, receveur: String): Boolean {
        return rechercherMotsCles(receveur, MotsCles.MOTS_CLE_RDV_LIVRAISON.listeDeMotsCles)
    }

    private fun estRendezVousDeRemorqueVide(sender: String, contenu: String): Boolean {
        return rechercherMotsCles(contenu, MotsCles.MOTS_CLE_RDV_REMORQUE_VIDE.listeDeMotsCles)
    }
}