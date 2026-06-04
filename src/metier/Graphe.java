package metier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graphe 
{
    private Sommet[] listeSommets;
    private boolean[][] grillePlanairePossibles; 
    private List<String> cheminsTracesJoueur;    
    private int hauteur;
    private int largeur;

    public Graphe() 
    {
        this.hauteur = 7;
        this.largeur = 7;
        
        int totalSommets = this.hauteur * this.largeur;
        this.listeSommets = new Sommet[totalSommets];
        this.grillePlanairePossibles = new boolean[totalSommets][totalSommets];
        this.cheminsTracesJoueur = new ArrayList<>();

        this.initialiserSommetsParDefaut();
        this.genererGrillePlanaire();
    }

    private void initialiserSommetsParDefaut() 
    {
        String[] types = {"FERME", "HOPITAL", "PETROLIER", "PORT", "TANK"};
        int id = 0;

        for (int y = 0; y < this.hauteur; y++) 
        {
            for (int x = 0; x < this.largeur; x++) 
            {
                int posX = 40 + (x * 85);
                int posY = 40 + (y * 85);
                String zone = types[(int)(Math.random() * types.length)];

                this.listeSommets[id] = new Sommet(id, posX, posY, zone);
                id++;
            }
        }
    }

    // MÉTHODE DE LECTURE : Utilise le FileReader demandé
    public void chargerDepuisFichier(String cheminFichier) 
    {
        // Utilisation explicite de FileReader demandée par le prof
        try (BufferedReader reader = new BufferedReader(new FileReader(cheminFichier))) 
        {
            String ligne;
            while ((ligne = reader.readLine()) != null) 
            {
                String[] elements = ligne.split(";");
                int idSommet = Integer.parseInt(elements[0]);
                int posX = Integer.parseInt(elements[1]);
                int posY = Integer.parseInt(elements[2]);
                String zone = elements[3];

                // On remplace le sommet par celui configuré à la conception
                this.listeSommets[idSommet] = new Sommet(idSommet, posX, posY, zone);
            }
            System.out.println("Chargement réussi avec FileReader !");
        } 
        catch (IOException e) 
        {
            System.out.println("Fichier introuvable, utilisation de la grille par défaut.");
        }
    }

    private void genererGrillePlanaire() 
    {
        for (int y = 0; y < this.hauteur; y++) 
        {
            for (int x = 0; x < this.largeur; x++) 
            {
                int idActuel = y * this.largeur + x;

                if (x < this.largeur - 1) 
                {
                    int idDroite = y * this.largeur + (x + 1);
                    this.grillePlanairePossibles[idActuel][idDroite] = true;
                    this.grillePlanairePossibles[idDroite][idActuel] = true;
                }
                if (y < this.hauteur - 1) 
                {
                    int idBas = (y + 1) * this.largeur + x;
                    this.grillePlanairePossibles[idActuel][idBas] = true;
                    this.grillePlanairePossibles[idBas][idActuel] = true;
                }
            }
        }
    }

    public boolean tracerRoute(int idSrc, int idCible, String zoneDemandee) 
    {
        if (!this.grillePlanairePossibles[idSrc][idCible]) return false;

        if (!this.listeSommets[idCible].getTypeZone().equals(zoneDemandee) && !zoneDemandee.equals("JOKER")) 
        {
            return false;
        }

        String clefLien = Math.min(idSrc, idCible) + "-" + Math.max(idSrc, idCible);
        if (this.cheminsTracesJoueur.contains(clefLien)) return false;

        this.cheminsTracesJoueur.add(clefLien);
        return true;
    }

    public int getNombreZonesTraversees() 
    {
        List<String> zonesVisitees = new ArrayList<>();
        
        for (String lien : this.cheminsTracesJoueur) 
        {
            int idSrc = Integer.parseInt(lien.split("-")[0]);
            int idCible = Integer.parseInt(lien.split("-")[1]);

            String zoneSrc = this.listeSommets[idSrc].getTypeZone();
            String zoneCible = this.listeSommets[idCible].getTypeZone();

            if (!zonesVisitees.contains(zoneSrc))   zonesVisitees.add(zoneSrc);
            if (!zonesVisitees.contains(zoneCible)) zonesVisitees.add(zoneCible);
        }
        return zonesVisitees.size();
    }

    public int getMaxSommetsDansUneZone() 
    {
        int ferme = 0, hopital = 0, petrolier = 0, port = 0, tank = 0;

        for (String lien : this.cheminsTracesJoueur) 
        {
            int idSrc = Integer.parseInt(lien.split("-")[0]);
            int idCible = Integer.parseInt(lien.split("-")[1]);

            switch (this.listeSommets[idSrc].getTypeZone()) 
            {
                case "FERME" -> ferme++; case "HOPITAL" -> hopital++; 
                case "PETROLIER" -> petrolier++; case "PORT" -> port++; case "TANK" -> tank++;
            }
            switch (this.listeSommets[idCible].getTypeZone()) 
            {
                case "FERME" -> ferme++; case "HOPITAL" -> hopital++; 
                case "PETROLIER" -> petrolier++; case "PORT" -> port++; case "TANK" -> tank++;
            }
        }

        return Math.max(ferme, Math.max(hopital, Math.max(petrolier, Math.max(port, tank))));
    }

    public int calculerScoreFormuleProf() 
    {
        return this.getNombreZonesTraversees() * this.getMaxSommetsDansUneZone();
    }

    public List<String> getCheminsTracesJoueur() { return this.cheminsTracesJoueur; }
    public Sommet[] getListeSommets() { return this.listeSommets; }
    public boolean[][] getGrillePlanairePossibles() { return this.grillePlanairePossibles; }
}