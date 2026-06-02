# 🌍 Guerre Froide : Opération Réseau Rouge

Projet de développement informatique dans le cadre de la **SAE 2.01**. 
Ce logiciel est une application de stratégie et de gestion de réseau basée sur le thème de la Guerre Froide.

---

## 🎮 Le Concept
Le monde est divisé en quatre zones d'influence (Bloc OUEST, Bloc EST, Bloc chinois, Bloc non-aligné). À la tête de votre camp, vous devez déployer vos **Soldats** et construire un réseau militaire reliant des points stratégiques essentiels pour étendre votre influence.

> **Le défi :** Anticipez vos déplacements, sécurisez les zones clés et bloquez les régiments adverses avant qu'ils ne verrouillent le territoire !

---

## 🛠️ Fonctionnalités du jeu
- [ ] **Mode Conception :** Créez, modifiez et dupliquez vos plateaux de jeu (grille 7x7).
- [ ] **Moteur de jeu :** Déroulement en 4 manches avec gestion d'une pioche de cartes.
- [ ] **Système de Soldats :** Déploiement tactique basé sur des connexions (arêtes) entre les sommets.
- [ ] **Multijoueur :** Affrontement de 2 à 4 joueurs en local.
- [ ] **Garnisons :** Système de contrôle de territoire par occupation de sommets.

---

## 🏗️ Sommets Stratégiques
Chaque point sur la carte représente une infrastructure vitale :
* ⚓ **Port** : Logistique navale.
* 🏥 **Hôpital** : Soutien médical.
* 🌾 **Ferme** : Ravitaillement alimentaire.
* 🛢️ **Site pétrolier** : Carburant et énergie.
* 🪖 **Base militaire** : QG de commandement (Point de départ).

---

## 🕹️ Comment jouer (Résumé)
1. **Piochez** une carte de déplacement au début de votre tour.
2. **Identifiez** les sommets compatibles avec le symbole de la carte.
3. **Déployez** un soldat depuis l'une de vos deux extrémités de réseau.
4. **Occupez** le sommet pour y installer une **Garnison** et marquez des points.
5. **Bloquez** vos adversaires en occupant les passages stratégiques !

---

```bash
git clone git@github.com:cs250949/SAE-2.01-Conception-d-application-Guerre-froide.git
cd SAE-2.01-Conception-d-application-Guerre-froide
# Lancer le jeu
./run_game.sh
