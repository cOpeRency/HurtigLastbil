package fr.hurtiglastbil.model.texto

import java.util.Date


class TextoRendezVousLivraison(envoyeur: String, receveur: String, date: Date, contenu: String):
    Texto(envoyeur, receveur, date, contenu) {
}