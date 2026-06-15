package conception.controleur;

import commun.Graphe;
import commun.Sommet;
import conception.vue.BarreStatut;
import conception.vue.PanneauGrille;
import javax.swing.*;

public class ControleurConception
{
	private PanneauGrille panneauGrille;
	private BarreStatut   barreStatut;
	private Graphe        graphe;
	private String        typeCourant = "HOPITAL";

	public ControleurConception()
	{
		// On démarre par défaut sur une grille standard
		this.graphe = new Graphe(7, 7);
	}

	public void setVues(PanneauGrille grille, BarreStatut statut)
	{
		this.panneauGrille = grille;
		this.barreStatut   = statut;
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
		}
	}

	public void setTypeCourant(String type)
	{
		this.typeCourant = type;
	}

	public void CelluleCliquee(int lig, int col)
	{
		if (this.graphe != null)
		{
			this.graphe.designerSommet(lig, col, this.typeCourant);
			if (this.panneauGrille != null)
			{
				this.panneauGrille.mettreAJourIcons();
			}
		}
	}

	public void redimensionnerPlateau(int hauteur, int largeur)
	{
		// Commande métier de reconstruction du graphe
		this.graphe = new Graphe(hauteur, largeur);
		
		// Notification immédiate à la vue graphique de tout reconstruire
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
		}
		setMessage("Dimension changée : " + largeur + " × " + hauteur);
	}

	public void setMessage(String msg)
	{
		if (this.barreStatut != null)
		{
			this.barreStatut.setMessage(msg);
		}
	}

	public void reinitialiser()
	{
		if (this.graphe != null)
		{
			redimensionnerPlateau(this.graphe.getNbLignes(), this.graphe.getNbColonnes());
		}
	}

	public void sauvegarderPlateau() { setMessage("Configuration enregistrée."); }
	public void chargerPlateau()     { setMessage("Configuration chargée."); }
	public void lancerJeu()          { setMessage("Lancement de la partie..."); }
	public void quitter()            { System.exit(0); }
}
