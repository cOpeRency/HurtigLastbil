package fr.hurtiglastbil.Textos

import fr.hurtiglastbil.model.FabriqueATexto
import fr.hurtiglastbil.model.texto.TextoRendezVousLivraison
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class TestsTexto {
    private lateinit var fabriqueATexto: FabriqueATexto;
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
            "Livraison")
        val json = texto.enJson()
        val attendu = "{\"envoyeur\":\"0123456789\",\"receveur\":\"0987654321\",\"date\":991,\"contenu\":\"Livraison\"}"
        assertEquals(attendu, json)
    }

    @Test
    fun test_est_rdv_livraison(){
        val texto = fabriqueATexto.creerTexto(
            "0123456789",
            "0987654321",
            Date(991),
            "Livraison")
        assertTrue(texto is TextoRendezVousLivraison)
    }
}
