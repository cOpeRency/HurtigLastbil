# HurtigLastbil
Dans ce document, vous retrouverez comment initialiser et utiliser l'application HurtigLastbil.

## Sommaire
1. [Commandes possibles](#1-commandes-possibles)
   - [1.1 Actions liées à une personne](#11-actions-liées-à-une-personne)
   - [1.2 Actions liées à des textos](#12-actions-liées-à-des-textos)
   - [1.3 Réinitialisation de la configuration](#13-réinitialisation-de-la-configuration)
2. [Stockage des messages](#2-stockage-des-messages)
3. [Crédits](#3-à-propos-de-lapplication)

## 1. Commandes possibles
Dans cette section vous trouverez l'ensemble des actions disponibles pour modifier le fonctionnement de l'application.
Afin d'offrir la possibilité de les appliquer, vos messages devront commencer par "configuration" ou "config".
Ensuite, afin de vous permettre une meilleure lisibilité des messages, vous pouvez séparer les lignes par des "=".

Vous pourrez avec ce message effectuer les actions suivantes :
- Ajouter des éléments de configuration
- Supprimer des éléments de configuration
- Modifier des droits et des noms
- Réinitialiser l'ensemble de la configuration à son état par défaut avec un administrateur définissable

Chacune de ces actions seront représentées par le mot "Action" dans la suite.
Ensuite, les lignes constituant votre volonté de modification devront suivre les syntaxes suivantes :

### 1.1 Actions liées à une personne
En suivant la syntaxe suivante vous pourrez ajouter, supprimer, ou modifier des personnes pouvant intéragir avec l'application :  
``` Action Role : Nom de la personne, Numéro de téléphone ```  
Le rôle se devra d'être l'un des suivants :
- Camionneur
- Mécanicien
- Secrétaire
- Administrateur

Le nom de la personne ne peut pas contenir des caractères inhabituel tel que ! ou ?.
Le numéro de téléphone peut être au format ``+33 6`` ou ``06``, cependant tout numéro ne commençant ni par ``06`` ou ``07`` et/ou ayant un nombre de chiffres différent du format français.

### 1.2 Actions liées à des textos
En suivant la syntaxe suivante, vous pourrez ajouter ou supprimer des types de texto ou des mots clés définissant ces derniers afin de vous permettre de trier parmi les différents types de texto reçus :  
``` Action "type"/"mots clés" : Nom du type de texto : Liste de mots clés ```

Vous pourrez donc ajouter des types ou des mots clés en précisant sur lequel des 2 vous souhaitez intéragir.
Vous définirez ensuite le terme caractérisant le texto.
Vous finirez par écrire une liste de mots clés définie comme suivant : ```mot1, mot2, ...```

### 1.3 Réinitialisation de la configuration

:warning: Cette action n'est à utiliser qu'en ultime recours, ne l'utilisez pas si la situation ne l'impose pas :warning:

En suivant la syntaxe suivante, vous pourrez réinitialiser la configuration en définissant un utilisateur administrateur :  
``` Réinitialiser Nom de la personne, Numéro de la personne```

Ainsi vous effacerez l'intégralité des configurations faites précédemment, en redéfinissant un administrateur lui permettant de faire les configurations nécessaires.

## 2. Stockage des messages
Les textos sont stockés dans un fichier texte, suivant le chemin suivant : ``Documents/hurtiglastbil``.
Chaque type de texto est stocké dans un dossier différent, portant le nom du type de texto. Exemple : ``Documents/hurtiglastbil/rdv/livraison``.
A l'avenir, cette organisation de fichiers sera utile à l'implémentation de l'API REST.

## 3. À propos de l'application
C'est un projet Android 10 développé en Kotlin.  
Cette application a pour but de stocker les textos reçus par les téléphones pour confirmer que les messages ont bien été reçus.

Ce projet à été développé par:
- Erwan Bourgeault
- Florian Rajoye
- Mattéo Nadler Campourcy
- Théo Lavauzelle