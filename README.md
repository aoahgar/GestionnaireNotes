# Gestionnaire de Notes

Application Android en Java realisee pour l'examen de developpement Android 2025/2026.

## Fonctionnalites obligatoires

- Creation d'une note avec titre, contenu et couleur.
- Consultation de toutes les notes enregistrees.
- Modification d'une note existante.
- Recherche par titre.
- Ajout/retrait des favoris par double appui sur une note.
- Filtre des notes favorites.
- Persistance locale avec `SharedPreferences` et JSON.
- Blocage de l'enregistrement si le titre ou le contenu est vide.
- Donnees conservees apres fermeture et redemarrage de l'application.

## Bonus

- Suppression d'une note avec confirmation.
- Tri des notes par date, titre, couleur ou favoris.
- Mode sombre persistant.
- Partage d'une note via les applications du telephone.
- Compteur total des notes et des favoris.
- Ameliorations UX : messages de confirmation, validation des champs, palette de couleurs, interface claire.

## Design

L'interface reprend les maquettes fournies :

- etat vide avec message `Aucune notes` ;
- bouton flottant noir `+` ;
- liste de notes colorees avec titre, date et favori ;
- palette verticale de couleurs ;
- ecran de creation avec bouton `Creer` ;
- ecran de modification avec bouton `Modifier`.

Couleurs principales utilisees :

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

L'APK debug est genere dans :

```text
app/build/outputs/apk/debug/app-debug.apk
```
