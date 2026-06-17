clear

echo ============================================
echo  OPERATION RESEAU ROUGE
echo ============================================
echo.

echo Compilation...
cd ./src/
javac @compile.txt -d ../class

if %errorlevel% neq 0 (
    echo ERREUR DE COMPILATION !
    pause
    exit /b
)

echo Copie des images...
xcopy /E /I /Y images ..\class\images

echo Execution...
cd ../class
java conception.controleur.ControleurConception

pause