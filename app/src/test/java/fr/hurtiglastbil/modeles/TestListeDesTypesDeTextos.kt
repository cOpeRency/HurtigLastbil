package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.modeles.texto.ListeDesTypesDeTextos
import fr.hurtiglastbil.modeles.texto.TypeTexto
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Test
import org.junit.Assert.*
import org.json.JSONArray
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
class TestListeDesTypesDeTextos {

    @Test
    fun creerDepuisJSONArray() {
        val jsonArray = JSONArray().apply {
            put(JSONObject().put("type1", JSONArray(listOf("motCle1", "motCle2"))))
        }

        val liste = ListeDesTypesDeTextos().creerDepuisJSONArray(jsonArray)

        assertNotNull(liste.typeTextos)
        assertTrue(liste.typeTextos!!.contains(TypeTexto("type1", mutableSetOf("motCle1", "motCle2"))))
    }

    @Test
    fun ajouterTypeTexto() {
        val liste = ListeDesTypesDeTextos(mutableSetOf())
        val typeTexto = TypeTexto("type1", mutableSetOf("motCle1"))

        liste.ajouterTypeTexto(typeTexto)

        assertTrue(liste.typeTextos!!.contains(typeTexto))
    }

    @Test
    fun supprimerTypeTexto() {
        val liste = ListeDesTypesDeTextos(mutableSetOf(TypeTexto("type1", mutableSetOf("motCle1"))))

        liste.supprimerTypeTexto("type1")

        assertFalse(liste.typeTextos!!.contains(TypeTexto("type1", mutableSetOf("motCle1"))))
    }



    @Test
    fun recupererTypeTexto() {
        val liste = ListeDesTypesDeTextos(mutableSetOf(TypeTexto("type1", mutableSetOf("motCle1"))))

        val typeTexto = liste.recupererTypeTexto("type1")

        assertNotNull(typeTexto)
        assertEquals("type1", typeTexto!!.cle)
    }
}