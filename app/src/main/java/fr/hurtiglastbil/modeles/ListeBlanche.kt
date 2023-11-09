package fr.hurtiglastbil.modeles

import fr.hurtiglastbil.enumerations.JsonEnum
import fr.hurtiglastbil.interfaces.IListeBlanche
import org.json.JSONArray

class ListeBlanche : IListeBlanche {
    /**
     * La liste blanche au format d'un array
     */
    var listeBlanche: MutableSet<Personne> = mutableSetOf();
    override fun creerUneListeBlancheDepuisTableauDeJSon(listeBlanche: JSONArray) : ListeBlanche
    {
        for (i in 0 until listeBlanche.length()) {
            val nom = listeBlanche.getJSONObject(i).getString(JsonEnum.NOM_PERSONNE.cle)
            val role = listeBlanche.getJSONObject(i).getString(JsonEnum.ROLE_PERSONNE.cle)
            val numeroDeTelephone = listeBlanche.getJSONObject(i).getString(JsonEnum.NUMERO_DE_TELEPHONE_PERSONNE.cle)

            val personne = Personne(nom, role, numeroDeTelephone)

            this.listeBlanche.add(personne)
        }

        return this
    }

    override fun creerListeBlancheDepuisUneChaineDeCharacteres(listeBlanche: String) : ListeBlanche
    {
        val jsonArray = JSONArray(listeBlanche)
        return creerUneListeBlancheDepuisTableauDeJSon(jsonArray)
    }

    override fun estDansLaListeBlanche(personne: Personne): Boolean {
        for (personneDansLaListe in listeBlanche) {
            if (personneDansLaListe.equals(personne)) {
                return true
            }
        }
        return false
    }

    override fun creerPersonneSiInserer(personne: Personne): Personne {
        if (estDansLaListeBlanche(personne)) {
            return listeBlanche.find { it.equals(personne) }!!
        } else {
            return personne
        }
    }

    override fun insererPersonne(personne: Personne) {
        if (!estDansLaListeBlanche(personne)) {
            listeBlanche.add(personne)
        }
    }

    override fun listeBlancheVersJSONArray(): JSONArray {
        val jsonArray = JSONArray()
        listeBlanche.forEach {
            jsonArray.put(it.versJSON())
        }
        return jsonArray
    }
}