#!/bin/bash

clear

echo "============================================"
HEAD
echo " OPERATION RESEAU ROUGE"
echo "============================================"
echo

echo "Compilation..."
cd ./src/ || exit 1
javac @compile.txt -d ../class


echo "Copie des images..."
cp -r images/ ../class/

echo "Execution..."
cd ../class || exit 1
java conception.controleur.ControleurConception

read -p "Appuyez sur Entrée pour terminer..."

echo "  OPERATION RESEAU ROUGE"
echo "============================================"
echo ""

echo "Compilation..."
cd ./src/
javac @compile.txt -d ../class

if [ $? -ne 0 ]; then
    echo "ERREUR DE COMPILATION !"
    exit 1
fi

echo "Copie des ressources..."
cp -r images ../class/ 2>/dev/null
cp -r data ../class/ 2>/dev/null

echo ""
echo "Que voulez-vous lancer ?"
echo "1. Editeur de plateau"
echo "2. Jeu (charge plateau.txt)"
echo ""
read -p "Votre choix (1 ou 2) : " choix

cd ../class

if [ "$choix" = "1" ]; then
    echo "Lancement de l'editeur..."
    java conception.controleur.ControleurConception
elif [ "$choix" = "2" ]; then
    echo "Lancement du jeu..."
    java jeu.controleur.ControleurJeu
else
    echo "Choix invalide, lancement du jeu par defaut..."
    java jeu.controleur.ControleurJeu
fi
 4802256 (Mise à jour du jeu pour la démo et nouveau script shell)
