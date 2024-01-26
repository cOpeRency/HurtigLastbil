package fr.hurtiglastbil.modeles

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import fr.hurtiglastbil.exceptions.ExceptionNouveauMotCle
import fr.hurtiglastbil.modeles.texto.TypeTexto
import fr.hurtiglastbil.exceptions.ExceptionSuppressionMotCle
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestTypeTexto {

    private lateinit var typeTexto: TypeTexto

    @Before
    fun setup() {
        typeTexto = TypeTexto("cleTest", mutableSetOf("motCle1", "motCle2"))
    }

    @Test
    fun ajouterMotCle() {
        typeTexto.ajouterMotCle("motCle3")
        assertTrue(typeTexto.listeDeMotsCles.contains("motCle3"))
    }

    @Test(expected = ExceptionNouveauMotCle::class)
    fun ajouterMotCleExistantLanceException() {
        typeTexto.ajouterMotCle("motCle1")
    }

    @Test
    fun supprimerMotCle() {
        typeTexto.supprimerMotCle("motCle2")
        assertFalse(typeTexto.listeDeMotsCles.contains("motCle2"))
    }

    @Test(expected = ExceptionSuppressionMotCle::class)
    fun supprimerMotCleInexistantLanceException() {
        typeTexto.supprimerMotCle("motCleInexistant")
    }

    @Test
    fun versJSONRetourneLeBonFormat() {
        val json = typeTexto.versJSON()
        val jsonArray = json.getJSONArray("cleTest")

        assertEquals(2, jsonArray.length())
        assertTrue(jsonArray.getString(0) == "motCle1" || jsonArray.getString(1) == "motCle1")
    }

    @Test
    fun validationReussitPourTypeTextoValide() {
        assertTrue(typeTexto.valider())
    }

    @Test
    fun validationEchouePourTypeTextoInvalide() {
        val typeTextoInvalide = TypeTexto("", mutableSetOf())
        assertFalse(typeTextoInvalide.valider())
    }

}