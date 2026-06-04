package controleur;

import java.util.Random;
import ihm.FramePlateau;
import metier.Joueur;

public class Controleur 
{
    private FramePlateau framePlateau;
    private Joueur[] listeJoueurs;
    private int indexJoueurCourant;
    private int nbJoueurs;
    
    private String[] paquetCartes;
    private String carteActive; 
    private Random random;

    public Controleur() 
    {
        this.indexJoueurCourant = 0;
        this.random = new Random();
        
        // Les types de zones de votre univers qui servent de pioche
        this.paquetCartes = new String[] 
        {
            "FERME", 
            "HOPITAL", 
            "PETROLIER", 
            "PORT", 
            "TANK", 
            "JOKER"
        };
    }

    public void setFrame(FramePlateau framePlateau) 
    {
        this.framePlateau = framePlateau;
    }

    // CORRECTION : Signature vide pour correspondre à l'appel depuis PanelConfig
    public void demarrerPartie() 
    {
        this.nbJoueurs = 4;
        this.listeJoueurs = new Joueur[this.nbJoueurs];
        
        for (int i = 0; i < this.nbJoueurs; i++) 
        {
            // Instanciation propre des joueurs sans paramètres superflus
            this.listeJoueurs[i] = new Joueur(i + 1, "Joueur " + (i + 1));
        }
        
        // On pioche la première carte zone de la manche
        this.piocherCarteZone();
        
        // On demande à la vue d'afficher l'écran du plateau
        this.framePlateau.activerEcranJeu();
    }

    public void piocherCarteZone() 
    {
        int index = this.random.nextInt(this.paquetCartes.length);
        this.carteActive = this.paquetCartes[index];
        System.out.println("Nouvelle carte piochée : " + this.carteActive);
    }

    // Action 1 : Le joueur valide un tracé de route entre deux sommets
    public boolean tenterTracerRoute(int idSrc, int idCible) 
    {
        Joueur jActuel = this.listeJoueurs[this.indexJoueurCourant];
        
        // Demande de tracé au graphe métier du joueur
        boolean traceReussi = jActuel.getPlateau().tracerRoute(idSrc, idCible, this.carteActive);
        
        if (traceReussi) 
        {
            this.passerAuJoueurSuivant();
            return true;
        }
        
        return false; // Tracé invalide 
    }

    // Action 2 : Le joueur choisit de passer son tour
    public void joueurPasseSonTour() 
    {
        this.passerAuJoueurSuivant();
    }

    private void passerAuJoueurSuivant() 
    {
        // Roulement des index 
        this.indexJoueurCourant = (this.indexJoueurCourant + 1) % this.nbJoueurs;
        
        // Si la boucle revient à 0, le tour de table complet est fini
        if (this.indexJoueurCourant == 0) 
        {
            // On pioche une nouvelle contrainte pour le prochain tour de table
            this.piocherCarteZone();
        }
    }

    public boolean isPasserAutorise()
    {
        return true; 
    }

    public Joueur getJoueurCourant() 
    {
        return this.listeJoueurs[this.indexJoueurCourant];
    }

    public String getCarteActive() 
    {
        return this.carteActive;
    }
}