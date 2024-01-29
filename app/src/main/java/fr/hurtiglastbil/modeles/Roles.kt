package fr.hurtiglastbil.modeles

enum class Roles(val motCle: String,val motsValides: Array<String>) {
    CAMIONNEUR("camionneur",arrayOf("CAMIONNEUR", "camionneur", "camionneuse", "CAMIONNEUSE")),
    ADMINISTRATEUR("administrateur",
        arrayOf(
            "ADMINISTRATEUR",
            "administrateur",
            "ADMIN",
            "admin",
            "administratrice",
            "ADMINISTRATRICE"
        )
    ),
    SECRETARIAT("secrétariat",
        arrayOf(
            "secrétariat",
            "SECRETARIAT",
            "SECRÉTARIAT",
            "SECRÉTAIRE",
            "secretariat",
            "secretaire",
            "secrétaire",
            "SECRETAIRE"
        )
    ),
    MECANICIEN("mécanicien",
        arrayOf(
            "mécanicien",
            "MECANICIEN",
            "MECANO",
            "mécano",
            "mecano",
            "mecanos",
            "mécanos",
            "mécaniciens",
            "mecanicien",
            "mecaniciens",
            "mécanicienne",
            "MECANICIENS",
            "MÉCANICIENS",
            "MECANICIENNE",
            "mecanicienne",
            "MÉCANICIEN",
            "MÉCANICIENNE"
        )
    );

    companion object {
        fun obtenirRole(mot: String): Roles? {
            for (role in entries) {
                for (motValide in role.motsValides) {
                    if (motValide.equals(mot, ignoreCase = true)) {
                        return role
                    }
                }
            }
            return null
        }

        fun estUnRole(mot: String): Boolean {
            return obtenirRole(mot) != null
        }
    }
}
