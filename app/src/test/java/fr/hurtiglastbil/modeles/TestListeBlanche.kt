package fr.hurtiglastbil.modeles

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.*
import org.json.JSONArray
import org.junit.*

class TestListeBlanche {

    @Test
    fun creation_de_la_liste_blanche_depuis_JSONArray_est_correcte() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val listeDePersonnes = listOf(personne)
        val jsonArray = mockk<JSONArray>()

        every { jsonArray.length() } returns 1
        every { jsonArray.getJSONObject(0) } returns mockk {
            every { getString(any()) } returns "John Doe" andThen "CEO" andThen "0123456789"
        }

        val whiteList = ListeBlanche()
        whiteList.creerUneListeBlancheDepuisTableauDeJSon(jsonArray)

        assertThat(whiteList).isNotNull
        assertThat(whiteList.listeBlanche.toList()).isEqualTo(listeDePersonnes)
        assertThat(whiteList.estDansLaListeBlanche(personne)).isTrue()
        assertThat(whiteList.creerPersonneSiInseree(Personne(numeroDeTelephone = "0123456789"))).isEqualTo(
            Personne("John Doe", "CEO", "0123456789")
        )
    }

    @Test
    fun personne_ajoute_dans_la_liste_blanche_est_present_dans_la_liste_blanche() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val whiteList = ListeBlanche()
        whiteList.listeBlanche.add(personne)

        assertThat(whiteList.estDansLaListeBlanche(personne)).isTrue()
    }

    @Test
    fun personne_pas_ajoute_nest_pas_present_dans_la_liste_blanche() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val whiteList = ListeBlanche()

        assertThat(whiteList.estDansLaListeBlanche(personne)).isFalse()
    }

    @Test
    fun recuperation_personne_dans_la_liste_blanche() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val personneTel = Personne(numeroDeTelephone = "0123456789")
        val whiteList = ListeBlanche()
        whiteList.listeBlanche.add(personne)

        assertThat(whiteList.creerPersonneSiInseree(personneTel)).isEqualTo(
            Personne("John Doe", "CEO", "0123456789")
        )
    }

    @Test
    fun recuperation_personne_pas_dans_liste_blanche() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val personneTel = Personne(numeroDeTelephone = "0999999999")
        val whiteList = ListeBlanche()
        whiteList.listeBlanche.add(personne)

        assertThat(whiteList.creerPersonneSiInseree(personneTel))
            .isNotEqualTo(Personne("John Doe", "CEO", "0123456789"))
            .isEqualTo(personneTel)
    }
/*
    @Test
    fun insertion_personne_est_OK() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val whiteList = ListeBlanche()
        whiteList.insererPersonne(personne)

        assertThat(whiteList.listeBlanche.toList()).contains(personne)
    }

    @Test
    fun insertion_personne_existante() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val whiteList = ListeBlanche()
        whiteList.insererPersonne(personne)
    }*/
}
