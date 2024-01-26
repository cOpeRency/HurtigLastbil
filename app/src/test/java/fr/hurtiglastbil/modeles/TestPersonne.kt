package fr.hurtiglastbil.modeles

import android.util.Log
import fr.hurtiglastbil.enumerations.JsonEnum
import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.*
import org.junit.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestPersonne {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @Test
    fun deux_personnes_avec_le_même_numéro_de_telephone_sont_egales() {
        val personne1 = Personne("John Doe", "CEO", "0123456789")
        val personne2 = Personne("Jane Doe", "Manager", "0123456789")

        assertThat(personne1).isEqualTo(personne2)
    }

    @Test
    fun deux_personnes_avec_des_numéros_de_telephone_differents_ne_sont_pas_egales() {
        val personne1 = Personne("John Doe", "CEO", "0123456789")
        val personne2 = Personne("Jane Doe", "Manager", "0987654321")

        assertThat(personne1).isNotEqualTo(personne2)
    }

    @Test
    fun le_hashCode_de_deux_personnes_identiques_doit_être_le_meme() {
        val personne1 = Personne("John Doe", "CEO", "0123456789")
        val personne2 = Personne("John Doe", "CEO", "0123456789")

        assertThat(personne1.hashCode()).isEqualTo(personne2.hashCode())
    }

    @Test
    fun personne_valide_passe_la_validation() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        assertThat(!personne.valider()).isTrue()
    }

    @Test
    fun validation_echoue_pour_numéro_de_téléphone_invalide() {
        val personne = Personne("John Doe", "CEO", "invalidPhone")
        assertThat(personne.valider()).isFalse()
    }

    @Test
    fun json_genere_correspond_aux_donnees_de_la_personne() {
        val personne = Personne("John Doe", "CEO", "0123456789")
        val json = personne.versJSON()

        assertThat(json.getString(JsonEnum.NOM_PERSONNE.cle)).isEqualTo("John Doe")
        assertThat(json.getString(JsonEnum.ROLE_PERSONNE.cle)).isEqualTo("CEO")
        assertThat(json.getString(JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle)).isEqualTo("0123456789")
    }

    @Test
    fun validation_echoue_pour_des_valeurs_non_valides() {
        val personneAvecNomVide = Personne("", "CEO", "0123456789")
        val personneAvecRoleVide = Personne("John Doe", "", "0123456789")
        val personneAvecNumeroVide = Personne("John Doe", "CEO", "")

        assertThat(personneAvecNomVide.valider()).isFalse()
        assertThat(personneAvecRoleVide.valider()).isFalse()
        assertThat(personneAvecNumeroVide.valider()).isFalse()
    }

}
