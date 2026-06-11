# Gestionnaire de Notes

Application Android en Java réalisée pour l'examen de développement Android 2025/2026.

## Fonctionnalités obligatoires

- Création d'une note avec titre, contenu et couleur.
- Consultation de toutes les notes enregistrées.
- Modification d'une note existante.
- Recherche par titre.
- Filtre des notes favorites.
- Ajout/retrait des favoris par double appui sur une note.
- Persistance locale avec `SharedPreferences` et JSON.
- Données conservées après fermeture et redémarrage de l'application.
- Blocage de l'enregistrement si le titre ou le contenu est vide.

## Bonus considérés comme obligatoires

- Suppression d'une note avec confirmation.
- Tri des notes par date, titre, couleur ou favoris.
- Compteur total des notes et des favoris.
- Mode sombre persistant.
- Partage d'une note via les applications du téléphone.
- Améliorations UX : messages de confirmation, validation des champs, palette de couleurs, interface claire.

## Design

L'interface reprend les maquettes fournies :

- état vide avec message `Aucune notes` ;
- bouton flottant noir `+` ;
- palette verticale de couleurs ;
- liste de notes colorées avec titre, date et favori ;
- écran de création avec bouton `Créer` ;
- écran de modification avec bouton `Modifier`.

Couleurs principales utilisées :

- `#000000`
- `#FFFFFF`
- `#828282`
- `#219653`
- `#EB5757`
- `#2F80ED`
- `#F2C94C`
- `#F2994A`

## Compilation

Depuis la racine du projet :

```bash
./gradlew assembleDebug
```

Sur Windows :

```powershell
.\gradlew.bat assembleDebug
```

L'APK debug est généré dans :

```text
app/build/outputs/apk/debug/app-debug.apk
```
