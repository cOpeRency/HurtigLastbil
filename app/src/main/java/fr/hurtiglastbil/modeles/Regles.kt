package fr.hurtiglastbil.modeles

enum class Regles {
    TEL_FR {
       override fun valider(chaine: String?): Boolean {
           return PAS_VIDE_OU_NULL.valider(chaine) && TEL_PORTABLE.valider(chaine) && ((chaine!!.length == 10) || (chaine.length == 12))
       }
    },
    NOM_PERSONNE {
        override fun valider(chaine: String?): Boolean {
            return PAS_VIDE_OU_NULL.valider(chaine) && CARACTERES_AUTORISES.valider(chaine) && chaine!!.length > 2
        }
    },
    ROLE_PERSONNE {
        override fun valider(chaine: String?): Boolean {
            return PAS_VIDE_OU_NULL.valider(chaine)
        }
    },
    TEL_PORTABLE {
        override fun valider(chaine: String?): Boolean {
            return chaine!!.startsWith("06") || chaine.startsWith("07") || chaine.startsWith("+336") || chaine.startsWith("+337")
        }
    },
    PAS_VIDE_OU_NULL {
        override fun valider(chaine: String?): Boolean {
            return !chaine.isNullOrEmpty()
        }
    },
    CARACTERES_AUTORISES {
        override fun valider(chaine: String?): Boolean {
            // Valide les caractères suivants : a-z, A-Z, -, espace
            return chaine?.matches(Regex("^[a-zA-ZÀ-ÖØ-öø-ÿ\\- ]+\$"))!!
        }
    };

    abstract fun valider(chaine: String?): Boolean
}