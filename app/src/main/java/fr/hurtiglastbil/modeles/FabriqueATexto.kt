package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.modeles.texto.TextoRendezVousLivraison
import fr.hurtiglastbil.modeles.texto.Texto
import fr.hurtiglastbil.modeles.texto.TextoIndefini
import java.text.Normalizer
import java.util.Date

class FabriqueATexto {
    fun creerTexto(envoyeur: String, receveur: String, date: Date, contenu: String, listeDesTypesDeTextos: ListeDesTypesDeTextos): Texto {
        if(estRendezVousDeLivraison(contenu, listeDesTypesDeTextos)){
            return TextoRendezVousLivraison(envoyeur, receveur, date, contenu)
        }
        if(estRendezVousDeRemorqueVide(contenu, listeDesTypesDeTextos)){
            return TextoRendezVousLivraison(envoyeur, receveur, date, contenu)
        }
        return TextoIndefini(envoyeur, receveur, date, contenu)
    }

    private fun rechercherMotsCles(contenu: String, motsCles: MutableSet<String>): Boolean {
        val contenuEnMinuscule = Normalizer.normalize(contenu.lowercase(), Normalizer.Form.NFD)
        motsCles.forEach { motCle ->
            if(contenuEnMinuscule.contains(motCle)) return true
        }
        return false
    }

    private fun estRendezVousDeLivraison(receveur: String, typesDeTextos: ListeDesTypesDeTextos): Boolean {
        return rechercherMotsCles(receveur, typesDeTextos.recupererLaListeDesMotsCles("rdv livraison")!!)
    }

    private fun estRendezVousDeRemorqueVide(contenu: String, typesDeTextos: ListeDesTypesDeTextos): Boolean {
        return rechercherMotsCles(contenu, typesDeTextos.recupererLaListeDesMotsCles("rdv remorque vide")!!)
    }
}