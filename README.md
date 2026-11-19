# TP Android — Gestion des contacts V3

Application Android autonome en Kotlin/XML, conçue comme base V3 lorsque le projet V2 n'est pas disponible.

## Démarrage

1. Installer Android Studio (JDK 17 et Android SDK 35).
2. Ouvrir le dossier `ContactsV3` dans Android Studio et laisser Gradle synchroniser.
3. Créer `local.properties` à partir de `local.properties.example`, renseigner `sdk.dir` si nécessaire et `MAPS_API_KEY=...`.
4. Dans Google Cloud, activer Maps SDK for Android et configurer la clé (restriction Android avec package `fr.tp.contacts` et empreinte SHA-1). Les conditions Google peuvent nécessiter la facturation.
5. Lancer sur un émulateur ou téléphone. Les contacts d'exemple sont en mémoire et reviennent aux valeurs initiales après arrêt du processus.

La clé se lit depuis `local.properties`; elle n'est jamais incluse dans les ressources versionnées. Ne publiez jamais `local.properties`.

## Architecture

- `MainActivity` : hôte unique `FragmentContainerView`; restaure le fragment après rotation.
- `ListeContactsFragment` : RecyclerView, recherche instantanée insensible à la casse, message vide.
- `DetailContactFragment` : consultation/édition, validation atomique et actions externes.
- `CarteContactFragment` : Google Maps, marqueur et caméra centrée; message si position invalide.
- `ContactsViewModel` partagé à l'activité : source en mémoire, mises à jour observées; les fragments s'échangent l'identifiant stable via arguments Bundle. Le retour arrière revient au détail.
- Galerie via `GetContent`; caméra via `TakePicture` et URI FileProvider temporaire. Les refus, annulations et intents sans application sont traités.

## Vérifications manuelles

- Rechercher par nom, prénom, téléphone, casse différente, résultat absent, puis effacer.
- Ouvrir un contact filtré; modifier des champs et enregistrer; vérifier la liste actualisée.
- Saisir des champs invalides (nom vide, e-mail mal formé, latitude > 90, longitude > 180); vérifier le refus d'enregistrement.
- Annuler l'édition et vérifier que les données précédentes sont conservées.
- Tester Appeler (composeur sans permission), E-mail, Galerie et Caméra; annuler chaque sélecteur et tester l'indisponibilité d'une application.
- Afficher la carte avec et sans coordonnées; vérifier centrage, marqueur et retour.
- Tourner l'écran sur la liste, le détail et la carte; vérifier qu'aucun écran n'est empilé en double.

## Questions de compréhension

1. Une Activity fournit une fenêtre et un cycle de vie d'écran; un Fragment est une portion d'interface réutilisable, attachée à une Activity et dotée de son propre cycle de vie de vue.
2. FragmentContainerView héberge correctement les fragments et leurs vues. `addToBackStack()` rend une transaction rembobinable avec Retour.
3. L'Adapter relie les données aux lignes et crée/alimente les ViewHolder; le ViewHolder conserve les références de vues recyclées.
4. Le filtrage produit une liste affichée distincte; le clic récupère l'objet par son identifiant, jamais par la position dans la liste source.
5. Un identifiant stable reste valable quand l'ordre ou le filtre change; une position est seulement un emplacement visuel temporaire.
6. La clé n'est pas commitée : `local.properties` est ignoré par Git. En production/dépôt, restreindre aussi la clé par application et API, et la renouveler si elle a fuité. Une clé embarquée dans une app peut être extraite; les restrictions côté Google restent indispensables.

## Limites et dépôt

Ce projet remplace fonctionnellement le TP V2 mais ne peut pas fusionner son code sans les sources V2. Les captures réelles demandées doivent être prises après lancement sur émulateur/appareil; aucune capture simulée n'est fournie. Le compte rendu PDF joint synthétise architecture, tests et difficultés.

Pour créer le dépôt: `git init`, vérifier `git status`, puis pousser vers un dépôt GitHub privé/public selon la consigne de l'enseignant. Ne pas ajouter `local.properties`, `.gradle/`, `build/` ni une clé.
