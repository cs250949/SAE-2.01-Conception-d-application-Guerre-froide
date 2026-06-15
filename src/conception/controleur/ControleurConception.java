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

	/**
	 * Constructeur par défaut : initialise un plateau standard 7x7
	 */
	public ControleurConception()
	{
		this.graphe = new Graphe(7, 7);
	}

	/**
	 * Constructeur secondaire pour compatibilité avec l'ancienne structure
	 */
	public ControleurConception(int largeur, int hauteur, int nbCouleur, int nbZone)
	{
		this.graphe = new Graphe(hauteur, largeur);
	}

	/**
	 * Lie les vues graphiques au contrôleur pour permettre les rafraîchissements
	 */
	public void setVues(PanneauGrille grille, BarreStatut statut)
	{
		this.panneauGrille = grille;
		this.barreStatut   = statut;
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
		}
	}

	/**
	 * Change l'infrastructure sélectionnée dans le formulaire 
	 */
	public void setTypeCourant(String type)
	{
		this.typeCourant = type;
	}

	/**
	 * Gère le clic sur une case pour poser ou effacer une infrastructure
	 */
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
	 * Reçoit l'ordre du formulaire de détruire l'ancien plateau et d'en recréer un nouveau
	 */
	public void redimensionnerPlateau(int hauteur, int largeur)
	{
		// Instanciation d'un tout nouveau graphe aux bonnes dimensions
		this.graphe = new Graphe(hauteur, largeur);
		
		// Demande au panneau visuel de reconstruire sa grille de boutons
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
		}
		
		setMessage("Nouveau plateau généré : " + largeur + " × " + hauteur + " (Scénario appliqué).");
	}

	/**
	 * Envoie un message texte dans la barre du bas
	 */
	public void setMessage(String msg)
	{
		if (this.barreStatut != null)
		{
			this.barreStatut.setMessage(msg);
		}
	}

	/**
	 * Réinitialise le plateau à blanc en gardant la taille actuelle
	 */
	public void reinitialiser()
	{
		if (this.graphe != null)
		{
			redimensionnerPlateau(this.graphe.getNbLignes(), this.graphe.getNbColonnes());
		}
	}

	public void reinitialiserChamps()
	{
		reinitialiser();
	}

	// Méthodes de stub pour les boutons du menu et de la vue
	public void sauvegarderPlateau() { setMessage("Configuration enregistrée localement."); }
	public void chargerPlateau()     { setMessage("Configuration chargée depuis le fichier."); }
	public void lancerJeu()          { setMessage("Lancement de la partie en cours..."); }
	public void validerEtLancer()    { lancerJeu(); }
	
	public void quitter()
	{
		System.exit(0);
	}

	public void afficherRegles()
	{
		JOptionPane.showMessageDialog(
			null,
			"Opération Réseau Rouge\n\n" +
			"1. Ajustez la taille avec les compteurs puis cliquez sur 'Générer'.\n" +
			"2. Sélectionnez une infrastructure dans la liste déroulante.\n" +
			"3. Cliquez sur une case du plateau pour la modifier.\n" +
			"4. Utilisez le menu du haut pour sauvegarder ou lancer la partie.",
			"Règles — Éditeur de plateau",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
}
