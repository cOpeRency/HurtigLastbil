package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.modeles.texto.TextoRendezVousLivraison
import fr.hurtiglastbil.modeles.texto.TypeTexto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class TestsTexto {
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
    fun test_serialisation_texto(){
        val texto = fabriqueATexto.creerTexto(
            "0123456789",
            "0987654321",
            Date(991),
            "Livraison",
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        val json = texto.enJson()
        val attendu = "{\n    \"envoyeur\": \"0123456789\",\n    \"receveur\": \"0987654321\",\n    \"date\": 991,\n    \"contenu\": \"Livraison\"\n}"
        assertEquals(attendu, json)
    }

    @Test
    fun test_est_rdv_livraison(){
        val texto = fabriqueATexto.creerTexto(
            "0123456789",
            "0987654321",
            Date(991),
            "Livraison",
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        assertTrue(texto is TextoRendezVousLivraison)
    }

    @Test
    fun test_est_rdv_remorque_vide() {
        val texto = fabriqueATexto.creerTexto(
            "0123456789",
            "0987654321",
            Date(991),
            "Remorque vide",
            ListeDesTypesDeTextos(listeDeTypeDeTexto)
        )
        assertTrue(texto is TextoRendezVousLivraison)
    }
}
