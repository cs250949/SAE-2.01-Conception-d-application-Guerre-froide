package conception.controleur;

import commun.Graphe;
import commun.Sommet;
import conception.vue.BarreStatut;
import conception.vue.FenetreConception;
import conception.vue.PanneauGrille;
import javax.swing.*;

public class ControleurConception
{
	/*----------------------------*/
	/* Constantes                 */
	/*----------------------------*/
	private static final String CHEMIN_PLATEAU = "plateau.txt";

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private FenetreConception fenetre;
	private PanneauGrille     panneauGrille;
	private BarreStatut       barreStatut;
	private Graphe            graphe;
	private String            typeCourant;
	private int               nbZonesActives;
	private int               tailleCase;

	/*----------------------------*/
	/* Constructeurs              */
	/*----------------------------*/
	public ControleurConception()
	{
		this(7, 7, 4, 4);
	}

	public ControleurConception(int largeur, int hauteur, int nbCouleur, int nbZone)
	{
		this.nbZonesActives = nbZone;
		this.typeCourant    = "HOPITAL";
		this.tailleCase     = 60;
		this.graphe         = new Graphe(hauteur, largeur);
		chargerPlateau();
	}

	/*----------------------------*/
	/* Lancement de l'éditeur     */
	/*----------------------------*/
	public void lancerEditeur()
	{
		this.fenetre = new FenetreConception(this);
	}

	/*----------------------------*/
	/* Accesseurs                 */
	/*----------------------------*/
	public Graphe getGraphe()
	{
		return this.graphe;
	}

	public int getTailleCase()
	{
		return this.tailleCase;
	}

	/*----------------------------*/
	/* Modificateurs              */
	/*----------------------------*/
	public void setVues(PanneauGrille grille, BarreStatut statut)
	{
		this.panneauGrille = grille;
		this.barreStatut   = statut;
		if (this.panneauGrille != null)
		{
			this.panneauGrille.setGraphe(this.graphe);
			this.panneauGrille.setTailleCase(this.tailleCase);
		}
	}

	public void setTypeCourant(String type)
	{
		this.typeCourant = type;
	}

	public void setNbZonesActives(int nb)
	{
		this.nbZonesActives = nb;
		if (panneauGrille != null)
		{
			panneauGrille.mettreAJourIcons();
		}
	}

	public void setMessage(String msg)
	{
		if (barreStatut != null)
		{
			barreStatut.setMessage(msg);
		}
	}

	/*----------------------------*/
	/* Gestion des événements     */
	/*----------------------------*/
	public void CelluleCliquee(int ligne, int colonne)
	{
		if (!graphe.estCaseAutorisee(ligne, colonne))
		{
			setMessage("Cette zone n'est pas active !");
			return;
		}

		Sommet s = graphe.getSommet(ligne, colonne);

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
		{
			barreStatut.setPosition(ligne, colonne);
		}
	}

	/*----------------------------*/
	/* Gestion du plateau         */
	/*----------------------------*/
	public void chargerPlateau()
	{
		if (this.graphe == null)
		{
			this.graphe = new Graphe();
		}

		if (panneauGrille != null)
		{
			panneauGrille.setGraphe(graphe);
		}
		setMessage("Génération d'un plateau vierge par défaut.");
	}

	public void sauvegarderPlateau()
	{
		setMessage("Configuration enregistrée localement.");
	}

	public void reinitialiser()
	{
		this.graphe = new Graphe();
		this.tailleCase = 60;
		if (panneauGrille != null)
		{
			panneauGrille.setGraphe(graphe);
			panneauGrille.setTailleCase(this.tailleCase);
		}
		setMessage("Grille effacée.");
	}

	public void redimensionnerPlateau(int hauteur, int largeur, int tailleCase)
	{
		this.tailleCase = tailleCase;
		this.graphe = new Graphe(hauteur, largeur);
		if (panneauGrille != null)
		{
			panneauGrille.setGraphe(graphe);
			panneauGrille.setTailleCase(tailleCase);
		}
		setMessage("Plateau rafraîchi : " + largeur + " × " + hauteur + " | Case : " + tailleCase + "px");
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
		if (fenetre != null)
		{
			fenetre.dispose();
		}
		System.exit(0);
	}

	/*----------------------------*/
	/* Lancement du jeu           */
	/*----------------------------*/
	public void lancerJeu()
	{
		if (this.graphe != null)
		{
			this.graphe.remonterAretes();
		}

		if (this.fenetre != null)
		{
			this.fenetre.dispose();
		}

		final Graphe g = this.graphe;
		final int    tc = this.tailleCase;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				jeu.controleur.ControleurJeu ctrlJeu = new jeu.controleur.ControleurJeu(g, tc);
				ctrlJeu.lancerJeu();
			}
		});
	}

	/*----------------------------*/
	/* MAIN - Point d'entrée      */
	/*----------------------------*/
	public static void main(String[] args)
	{
		ControleurConception ctrl = new ControleurConception(7, 7, 4, 4);
		ctrl.lancerEditeur();
	}
}