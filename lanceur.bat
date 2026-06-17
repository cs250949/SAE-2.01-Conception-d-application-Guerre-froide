@echo off

cls

echo ============================================
echo  OPERATION RESEAU ROUGE
echo ============================================
echo.

echo Compilation...
cd ./src/
javac @compile.txt -d ../class

echo Copie des ressources...
xcopy /E /I /Y images ..\class\images > nul
xcopy /E /I /Y data ..\class\data > nul

echo.
echo Que voulez-vous lancer ?
echo 1. Editeur de plateau
echo 2. Jeu (charge plateau.txt)
echo.
set /p choix="Votre choix (1 ou 2) : "

cd ../class

if "%choix%"=="1" (
    echo Lancement de l'editeur...
    java conception.controleur.ControleurConception
) else if "%choix%"=="2" (
    echo Lancement du jeu...
    java jeu.controleur.ControleurJeu
) else (
    echo Choix invalide, lancement du jeu par defaut...
    java jeu.controleur.ControleurJeu
)

pause