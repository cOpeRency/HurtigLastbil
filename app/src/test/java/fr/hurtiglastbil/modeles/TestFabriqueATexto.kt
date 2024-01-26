package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.modeles.texto.DonneesTexto
import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.modeles.texto.TextoRendezVousLivraison
import fr.hurtiglastbil.modeles.texto.TypeTexto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.Date

class TestFabriqueATexto {
    private lateinit var fabriqueATexto: FabriqueATexto
    private val listeDeTypeDeTexto : MutableSet<TypeTexto> = mutableSetOf(
        TypeTexto("rdv livraison", mutableSetOf("livraison", "sodimat", "ecoval", "7h", "9h", "retour depot")),
        TypeTexto("rdv remorque vide", mutableSetOf("remorque vide","gueret","demain"))
    )
    @Before
    fun before(){
        fabriqueATexto = FabriqueATexto()
    }

    @Test
    fun retourne_un_texto_serialise(){
        val texto = fabriqueATexto.creerTexto(
            DonneesTexto(
                envoyeur = "0123456789",
                receveur = "0987654321",
                date = Date(991),
                contenu =  "Livraison",
            ),
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        val json = texto.enJson()
        val attendu = "{\n    \"envoyeur\": \"0123456789\",\n    \"receveur\": \"0987654321\",\n    \"date\": 991,\n    \"contenu\": \"Livraison\"\n}"
        assertEquals(attendu, json)
    }

    @Test
    fun texto_est_un_rendez_vous_de_livraison(){
        val texto = fabriqueATexto.creerTexto(
            DonneesTexto(
                envoyeur = "0123456789",
                receveur = "0987654321",
                date = Date(991),
                contenu =  "Livraison",
            ),
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        assertTrue(texto is TextoRendezVousLivraison)
    }

    @Test
    fun texto_est_un_rendez_vous_avec_remorque_vide() {
        val texto = fabriqueATexto.creerTexto(
            DonneesTexto(
                envoyeur = "0123456789",
                receveur = "0987654321",
                date = Date(991),
                contenu =  "Remorque vide",
            ),
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        assertTrue(texto is TextoRendezVousLivraison)
    }
}
