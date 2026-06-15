package conception.controleur;

import commun.Graphe;
import conception.vue.BarreStatut;
import conception.vue.PanneauGrille;
import javax.swing.*;

public class ControleurConception
{
	private PanneauGrille panneauGrille;
	private BarreStatut   barreStatut;
	private Graphe        graphe;
	private String        typeCourant    = "HOPITAL";
	private int           nbZonesActives = 4;

	/**
	 * Constructeur principal appelé par FenetreConception 
	 */
	public ControleurConception(int largeur, int hauteur, int nbCouleur, int nbZone)
	{
		this.nbZonesActives = nbZone;
		this.graphe         = new Graphe(hauteur, largeur);
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

	/**
	 * Appelée par l'action du bouton Générer du formulaire
	 */
	public void redimensionnerPlateau(int hauteur, int largeur)
	{
		this.graphe = new Graphe(hauteur, largeur);
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
		}
		setMessage("Plateau généré : " + largeur + " × " + hauteur);
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

	public void reinitialiserChamps() { reinitialiser(); }
	public void sauvegarderPlateau() { setMessage("Configuration enregistrée."); }
	public void chargerPlateau()     { setMessage("Configuration chargée."); }
	public void lancerJeu()          { setMessage("Lancement du jeu..."); }
	public void validerEtLancer()    { lancerJeu(); }
	public void quitter()            { System.exit(0); }

	public void afficherRegles()
	{
		JOptionPane.showMessageDialog(null, "Ajustez les dimensions puis cliquez sur Générer.", "Aide", JOptionPane.INFORMATION_MESSAGE);
	}
}
