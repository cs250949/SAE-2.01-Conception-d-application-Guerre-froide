package conception.controleur;

import commun.Graphe;
import commun.Sommet;
import conception.vue.BarreStatut;
import conception.vue.PanneauGrille;
import java.io.IOException;
import javax.swing.*;

public class ControleurConception
{
	private static final String CHEMIN_PLATEAU = "plateau.txt";

	private PanneauGrille panneauGrille;
	private BarreStatut   barreStatut;
	private Graphe        graphe;
	private String        typeCourant    = "HOPITAL";
	private int           nbZonesActives = 4;

	public ControleurConception()
	{
		this(7, 7, 4, 4);
	}

	public ControleurConception(int largeur, int hauteur, int nbCouleur, int nbZone)
	{
		this.nbZonesActives = nbZone;
		// Utilisation du constructeur par défaut nettoyé en entiers
		this.graphe         = new Graphe();
		chargerPlateau();
	}

	public void setVues(PanneauGrille grille, BarreStatut statut)
	{
		this.panneauGrille = grille;
		this.barreStatut   = statut;
		this.panneauGrille.setGraphe(this.graphe);
	}

	public void setTypeCourant(String type)
	{
		this.typeCourant = type;
	}

	public void setNbZonesActives(int nb)
	{
		this.nbZonesActives = nb;
		if (panneauGrille != null)
			panneauGrille.mettreAJourIcons();
	}

	public void setMessage(String msg)
	{
		if (barreStatut != null)
			barreStatut.setMessage(msg);
	}

	public void CelluleCliquee(int ligne, int colonne)
	{
		if (!graphe.estCaseAutorisee(ligne, colonne))
		{
			setMessage("Cette zone n'est pas active !");
			return;
		}

		Sommet s = graphe.getSommet(ligne, colonne);
		
		// Si le sommet n'existe pas encore au clic dans l'éditeur, on le crée à la volée
		if (s == null)
		{
			String blocParDefaut = graphe.determinerBloc(ligne, colonne);
			s = new Sommet(ligne, colonne, typeCourant, blocParDefaut);
			graphe.getSommets().add(s);
		}
		else
		{
			s.setType(typeCourant);
		}
		
		panneauGrille.mettreAJourIcons();
		setMessage("Cellule [" + ligne + ", " + colonne + "] → " + typeCourant + " / Faction : " + s.getBloc());
	}

	public void CelluleSurvolee(int ligne, int colonne)
	{
		if (barreStatut != null)
			barreStatut.setPosition(ligne, colonne);
	}

	public void chargerPlateau()
	{
		
		if (this.graphe == null)
		{
			this.graphe = new Graphe();
		}
		
		if (panneauGrille != null)
			panneauGrille.setGraphe(graphe);
		setMessage("Génération d'un plateau vierge par défaut.");
	}

	public void sauvegarderPlateau()
	{
		setMessage("Configuration enregistrée localement.");
	}

	public void reinitialiser()
	{
		this.graphe = new Graphe();
		if (panneauGrille != null)
			panneauGrille.setGraphe(graphe);
		setMessage("Grille effacée.");
	}

	public void redimensionnerPlateau(int hauteur, int largeur)
	{
		this.graphe = new Graphe();
		if (panneauGrille != null)
			panneauGrille.setGraphe(graphe);
		setMessage("Plateau rafraîchi : " + largeur + " × " + hauteur);
	}

	public void afficherRegles()
	{
		JOptionPane.showMessageDialog(
			null,
			"Opération Réseau Rouge\n\n" +
			"1. Choisissez un type d'infrastructure dans la liste.\n" +
			"2. Les zones géographiques gardent leurs couleurs d'origine.\n" +
			"3. Cliquez sur une case pour l'affecter.\n" +
			"4. Sauvegardez puis lancez le jeu.",
			"Règles — Éditeur de plateau",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	public void reinitialiserChamps()
	{
		reinitialiser();
	}

	public void validerEtLancer()
	{
		lancerJeu();
	}

	public void quitter()
	{
		System.exit(0);
	}

	public void lancerJeu()
	{
		// Calcule et relie les sommets par rayons avant d'ouvrir la fenêtre 
		if (this.graphe != null)
		{
			this.graphe.remonterAretes();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new jeu.vue.FenetreJeu(ControleurConception.this.graphe);
			}
		});
	}

	public Graphe getGraphe()
	{
		return graphe;
	}
}
