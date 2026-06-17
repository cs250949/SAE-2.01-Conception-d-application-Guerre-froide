package jeu.controleur;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import jeu.metier.Jeu;
import jeu.vue.FenetreJeu;
import jeu.vue.PanneauPlateau;
import jeu.vue.PanneauInfo;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class ControleurJeu
{
	/*----------------------------*/
	/* Constantes                 */
	/*----------------------------*/
	final static int WIDTH_FENETRE  = 900;
	final static int HEIGHT_FENETRE = 650;

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private Jeu            jeu;
	private Graphe         graphe;
	private int            tailleCase;
	private FenetreJeu     fenetreJeu;
	private PanneauPlateau panneauPlateau;
	private PanneauInfo    panneauInfo;
	private boolean        modeDebug;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public ControleurJeu(Graphe graphe)
	{
		this(graphe, 60);
	}

	public ControleurJeu(Graphe graphe, int tailleCase)
	{
		this.graphe     = graphe;
		this.tailleCase = tailleCase;
		this.jeu        = new Jeu();
		this.modeDebug  = false;

		this.jeu.setGraphe(graphe);

		this.jeu.ajouterEcouteur(new Jeu.Ecouteur()
		{
			@Override
			public void Refresh()
			{
				if (panneauPlateau != null) { panneauPlateau.mettreAJourAffichage(); }
				if (panneauInfo    != null) { panneauInfo.rafraichir();             }
			}

			@Override
			public void Message(String msg)
			{
				if (panneauInfo != null) { panneauInfo.setMessage(msg); }
				System.out.println("[Jeu] " + msg);
			}

			@Override
			public void Fin(String classement)
			{
				if (panneauInfo != null)
				{
					panneauInfo.setMessage("Partie terminée !");
					panneauInfo.afficherClassement(classement);
				}
			}

			@Override
			public void CarteChange(String carte, boolean estJoker)
			{
				if (panneauInfo != null) { panneauInfo.mettreAJourCarte(carte, estJoker); }
			}

			@Override
			public void MancheChange(int manche)
			{
				if (panneauInfo != null) { panneauInfo.mettreAJourManche(manche); }
			}

			@Override
			public void LigneTracee(Arete arete)
			{
				if (panneauPlateau != null) { panneauPlateau.mettreAJourAffichage(); }
			}

			@Override
			public void ScoreChange(int score)
			{
				if (panneauInfo != null) { panneauInfo.rafraichir(); }
			}
		});
	}

	/*----------------------------*/
	/* Lancement de l'interface   */
	/*----------------------------*/
	public void lancerJeu()
	{
		this.panneauPlateau = new PanneauPlateau(this, this.graphe, this.tailleCase, new PanneauPlateau.CelluleListener()
		{
			@Override
			public void CelluleCliquee(int ligne, int colonne)
			{
				ControleurJeu.this.CelluleSelectionnee(ligne, colonne);
			}

			@Override
			public void CelluleSurvolee(int ligne, int colonne)
			{
				if (panneauInfo != null)
				{
					panneauInfo.setPosition(ligne, colonne);
				}
			}
		});

		this.panneauInfo = new PanneauInfo(this);
		this.fenetreJeu  = new FenetreJeu(this);

		this.panneauPlateau.mettreAJourAffichage();
		this.panneauInfo.rafraichir();
	}

	/*----------------------------*/
	/* Accesseurs                 */
	/*----------------------------*/
	public Jeu             getJeu()             { return this.jeu;             }
	public Graphe          getGraphe()          { return this.graphe;          }
	public int             getTailleCase()      { return this.tailleCase;      }
	public PanneauPlateau  getPanneauPlateau()  { return this.panneauPlateau;  }
	public PanneauInfo     getPanneauInfo()     { return this.panneauInfo;     }
	public boolean         isModeDebug()        { return this.modeDebug;       }
	public Dimension       getSizeFenetre()     { return new Dimension(WIDTH_FENETRE, HEIGHT_FENETRE); }

	public int getScore()
	{
		return jeu != null ? jeu.getScore() : 0;
	}

	public int getMancheCourante()
	{
		return jeu != null ? jeu.getMancheCourante() : 1;
	}

	public String getCarteActive()
	{
		return jeu != null ? jeu.getCarteActive() : null;
	}

	public boolean isCarteJoker()
	{
		return jeu != null ? jeu.isCarteJoker() : false;
	}

	public String getJoueurCourant()
	{
		return "Agent Solo";
	}

	public int getJoueurCourantIdx()
	{
		return 0;
	}

	public int[] getScores()
	{
		int[] scores = new int[1];
		scores[0] = getScore();
		return scores;
	}

	public List<String> getNomsJoueurs()
	{
		List<String> noms = new ArrayList<String>();
		noms.add("Agent Solo");
		return noms;
	}

	public List<Arete> getLignesTracees()
	{
		return jeu != null ? jeu.getLignesTracees() : new ArrayList<Arete>();
	}

	public int getNbSommetsVisites()
	{
		if (jeu == null) return 0;
		return jeu.getSommetsVisites().size();
	}

	public int getNbZonesVisitees()
	{
		if (jeu == null) return 0;
		return jeu.getZonesVisitees().size();
	}

	public int getNbCartesRestantes()
	{
		return jeu != null ? jeu.getNbCartesRestantes() : 0;
	}

	/*----------------------------*/
	/* Modificateurs              */
	/*----------------------------*/
	public void setModeDebug(boolean actif)
	{
		this.modeDebug = actif;
		if (panneauInfo != null)
		{
			panneauInfo.setMessage(actif ? "MODE DEBUG ACTIVÉ" : "Règles normales");
		}
	}

	/*----------------------------*/
	/* Gestion des actions        */
	/*----------------------------*/
	public void CelluleSelectionnee(int lig, int col)
	{
		if (jeu == null) return;

		boolean coupValide;

		if (modeDebug)
		{
			coupValide = jeu.forcerPlacementCaseDebug(lig, col);
		}
		else
		{
			coupValide = jeu.jouerCoup(lig, col);
		}

		if (panneauPlateau != null)
		{
			panneauPlateau.mettreAJourAffichage();
		}

		if (panneauInfo != null)
		{
			panneauInfo.rafraichir();
		}
	}

	public void piocherCarte()
	{
		if (jeu == null) return;

		if (modeDebug)
		{
			String[] typesSommets = {"HOPITAL", "PORT", "FERME", "PETROLIER", "TANK", "BASE_DEPART"};
			int indexAleatoire = (int)(Math.random() * typesSommets.length);
			String cartePiochee = typesSommets[indexAleatoire];
			jeu.forcerCarteDebug(cartePiochee);
		}
		else
		{
			jeu.piocher();
		}

		if (panneauInfo != null)
		{
			panneauInfo.rafraichir();
		}
	}

	public void passerTour()
	{
		if (jeu == null) return;

		jeu.passerTour();

		if (panneauPlateau != null)
		{
			panneauPlateau.mettreAJourAffichage();
		}

		if (panneauInfo != null)
		{
			panneauInfo.rafraichir();
		}
	}

	public void quitter()
	{
		if (fenetreJeu != null)
		{
			fenetreJeu.dispose();
		}
	}

	/*----------------------------*/
	/* MAIN - Point d'entrée Jeu  */
	/*----------------------------*/
	public static void main(String[] args)
	{
		Graphe g = Graphe.chargerDepuisFichier("data/plateau.txt");

		if (g == null)
		{
			System.out.println("Erreur : plateau.txt introuvable !");
			return;
		}

		ControleurJeu ctrl = new ControleurJeu(g, 60);
		ctrl.lancerJeu();
	}
}