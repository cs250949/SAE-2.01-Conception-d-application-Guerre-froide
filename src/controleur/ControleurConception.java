package controleur;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import metier.Graphe;
import metier.Sommet;

public class ControleurConception 
{
    private Graphe plateauEnCoursDeConception;

    public ControleurConception() 
    {
        // On génère une grille de base en 7x7
        this.plateauEnCoursDeConception = new Graphe();
    }

    public void genererNouvelleCarte()
    {
        this.plateauEnCoursDeConception = new Graphe();
    }

    public void sauvegarderFichier(String cheminFichier) 
    {
        // Utilisation explicite de FileWriter demandée par le prof
        try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) 
        {
            Sommet[] sommets = this.plateauEnCoursDeConception.getListeSommets();
            
            for (Sommet s : sommets) 
            {
                // On écrit : ID;X;Y;TYPE
                writer.println(s.getId() + ";" + s.getX() + ";" + s.getY() + ";" + s.getTypeZone());
            }
            System.out.println("Sauvegarde réussie avec FileWriter !");
        } 
        catch (IOException e) 
        {
            System.out.println("Erreur d'écriture avec FileWriter : " + e.getMessage());
        }
    }

    public Graphe getPlateau() 
    {
        return this.plateauEnCoursDeConception;
    }
}