# Opération Réseau Rouge

## Description
Jeu de stratégie où des agents doivent connecter un réseau de logistique sans croiser leurs câbles.

## Règles
- 4 manches
- 5 types de stations : HOPITAL, FERME, PETROLIER, PORT, TANK
- 11 cartes par manche (2 par type + 1 JOKER)
- Les câbles ne doivent pas se croiser
- Max 2 connexions par station
- Score = nombre de zones × max stations dans une zone

## Comment jouer
1. Lancer `ControleurConception`
2. Configurer le plateau 
3. Valider pour lancer la partie
4. Piocher des cartes et connecter les stations



## Compilation
```bash
javac commun/*.java conception/controleur/*.java jeu/controleur/*.java
