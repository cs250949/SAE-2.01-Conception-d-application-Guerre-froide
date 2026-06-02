package controleur;

import ihm.FramePlateau;
import metier.Graphe;

public class Controleur 
{
    private FramePlateau framePlateau;
    private Graphe graphe;

	private int idCaseSelectionnee = -1;

    public Controleur(FramePlateau framePlateau) 
    {
        this.framePlateau = framePlateau;
        this.graphe = new Graphe();
    }

    public void clicSurCase(int x, int y) 
	{
		// On transforme les coordonnées (x,y) en un ID unique entre 0 et 48
		int idCase = x * 7 + y;
		System.out.println("Clic détecté sur l'ID : " + idCase);

		if (this.idCaseSelectionnee == -1) 
		{
			// Premier clic : on sélectionne la case de départ
			this.idCaseSelectionnee = idCase;
			System.out.println("Case de départ sélectionnée : " + idCaseSelectionnee);
		} 
		else 
		{
			// Deuxième clic : on crée le lien avec la case d'arrivée
			int idArrivee = idCase;
			
			if (this.idCaseSelectionnee != idArrivee) 
			{
				// On demande au métier d'enregistrer le lien dans la matrice !
				this.graphe.ajouterLien(this.idCaseSelectionnee, idArrivee);
			}
			
			// On réinitialise la sélection pour le prochain lien
			this.idCaseSelectionnee = -1;
		}
	}

    public static void main(String[] args)
    {
        new Controleur(new FramePlateau());
    }

}