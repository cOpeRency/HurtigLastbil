package fr.hurtiglastbil

import fr.hurtiglastbil.models.Personne
import fr.hurtiglastbil.models.ListeBlanche
import org.assertj.core.api.Assertions.*
import org.json.JSONArray
import org.junit.*
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class WhiteListUnitTest {

    @Mock
    private lateinit var mockJSONArray: JSONArray

    @Test
    fun creation_de_la_liste_blanche_depuis_JSONArray_est_correcte() {
        val whiteList: ListeBlanche = ListeBlanche()
        val listeDePersonnesString = "[{\"name\":\"John Doe\",\"role\":\"CEO\",\"number\":\"0123456789\"}]"
        val mockJSONArray = mock<JSONArray> {
            on { length() } doReturn 1
        }
        val personne = Personne("John Doe", "CEO", "0123456789")
        val listeDePersonnes: List<Personne> = listOf(personne)

        whiteList.creerListeBlancheDepuisUneChaineDeCharacteres(listeDePersonnesString)

        assertThat(whiteList).isNotNull
        assertThat(whiteList.listeBlanche).isEqualTo(listeDePersonnes)
        assertThat(whiteList.estDansLaListeBlanche(personne)).isTrue()
        assertThat(whiteList.creerPersonneSiInserer(Personne(numeroDeTelephone =  "0123456789"))).isEqualTo(Personne("John Doe", "CEO", "0123456789"))
    }

    @Test
    fun creation_de_la_liste_blanche_depuis_string_est_correcte() {
        // TODO : Créer une liste blanche depuis une chaîne de caractères au format json
    }
}