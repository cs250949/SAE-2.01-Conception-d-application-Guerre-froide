#!/bin/bash

clear

echo "============================================"
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
