package jeu.metier;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import java.util.*;

public class Jeu
{
	/*----------------------------*/
	/* Constantes                 */
	/*----------------------------*/
	public static final int NB_MANCHES = 4;

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private int                mancheCourante;
	private boolean            partieTerminee;

	private List<Arete>        lignesTracees;
	private List<Sommet>       sommetsVisites;
	private List<String>       zonesVisitees;
	private int                score;

	private List<String>       pioche;
	private String             carteActive;
	private boolean            carteActiveEstJoker;

	private Graphe             graphe;
	private Sommet             positionActuelle;

	private List<Ecouteur>     ecouteurs;

	/*----------------------------*/
	/* Interface Écouteur         */
	/*----------------------------*/
	public interface Ecouteur
	{
		void Refresh();
		void Message(String msg);
		void Fin(String classement);
		void CarteChange(String carte, boolean estJoker);
		void MancheChange(int manche);
		void LigneTracee(Arete arete);
		void ScoreChange(int score);
	}

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public Jeu()
	{
		this.mancheCourante   = 1;
		this.partieTerminee   = false;
		this.ecouteurs        = new ArrayList<Ecouteur>();
		this.lignesTracees    = new ArrayList<Arete>();
		this.sommetsVisites   = new ArrayList<Sommet>();
		this.zonesVisitees    = new ArrayList<String>();
		this.score            = 0;
		this.pioche           = new ArrayList<String>();
		this.carteActive      = null;
		this.carteActiveEstJoker = false;
		this.positionActuelle = null;

		initialiserPioche();
	}

	/*----------------------------*/
	/* Initialisation pioche      */
	/*----------------------------*/
	private void initialiserPioche()
	{
		this.pioche.clear();

		String[] types = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK", "JOKER"};
		for (String t : types)
		{
			for (int i = 0; i < 3; i++)
			{
				this.pioche.add(t);
			}
		}
		Collections.shuffle(this.pioche);

		this.carteActive = null;
	}

	/*----------------------------*/
	/* Écouteurs                  */
	/*----------------------------*/
	public void ajouterEcouteur(Ecouteur e)
	{
		this.ecouteurs.add(e);
	}

	private void notifierRefresh()
	{
		for (Ecouteur e : ecouteurs) { e.Refresh(); }
	}

	private void notifierMessage(String msg)
	{
		for (Ecouteur e : ecouteurs) { e.Message(msg); }
	}

	/*----------------------------*/
	/* Graphe                      */
	/*----------------------------*/
	public void setGraphe(Graphe g)
	{
		this.graphe = g;
		this.lignesTracees.clear();
		this.sommetsVisites.clear();
		this.zonesVisitees.clear();
		this.score = 0;
		this.positionActuelle = null;
		this.mancheCourante = 1;
		this.partieTerminee = false;

		for (Sommet s : g.getSommets())
		{
			s.setVisite(false);
		}

		initialiserPioche();
		notifierRefresh();
	}

	/*----------------------------*/
	/* Pioche une carte           */
	/*----------------------------*/
	public void piocher()
	{
		if (partieTerminee) return;

		if (carteActive != null)
		{
			notifierMessage("Vous avez déjà une carte en main !");
			return;
		}

		if (pioche.isEmpty())
		{
			notifierMessage("Pioche vide ! Passez le tour.");
			return;
		}

		this.carteActive = this.pioche.remove(0);
		this.carteActiveEstJoker = this.carteActive.equals("JOKER");

		for (Ecouteur e : ecouteurs)
		{
			e.CarteChange(carteActive, carteActiveEstJoker);
		}
		notifierMessage("Carte piochée : " + carteActive);
		notifierRefresh();
	}

	/*----------------------------*/
	/* Jouer un coup              */
	/*----------------------------*/
	public boolean jouerCoup(int lig, int col)
	{
		if (partieTerminee) return false;

		Sommet cible = graphe.getSommet(lig, col);
		if (cible == null)
		{
			notifierMessage("Case vide !");
			return false;
		}

		/* PREMIER COUP : ancrage sur une BASE_DEPART */
		if (positionActuelle == null)
		{
			if (!cible.getType().equals("BASE_DEPART"))
			{
				notifierMessage("Commencez par vous ancrer sur une BASE_DEPART !");
				return false;
			}

			cible.setVisite(true);
			if (!sommetsVisites.contains(cible))
			{
				sommetsVisites.add(cible);
			}

			String zone = cible.getZone();
			if (zone != null && !zonesVisitees.contains(zone))
			{
				zonesVisitees.add(zone);
			}

			positionActuelle = cible;
			calculerScore();
			notifierMessage("Ancrage réussi sur la base en (" + lig + "," + col + ") !");
			notifierRefresh();
			return true;
		}

		/* COUPS SUIVANTS */
		if (carteActive == null)
		{
			notifierMessage("Piochez d'abord une carte !");
			return false;
		}

		if (!sontAdjacents(positionActuelle, cible))
		{
			notifierMessage("Cette case n'est pas adjacente à votre position !");
			return false;
		}

		if (!carteActiveEstJoker && !cible.getType().equals(carteActive))
		{
			notifierMessage("L'infrastructure ne correspond pas à votre carte (" + carteActive + ") !");
			return false;
		}

		if (detecterCroisement(positionActuelle, cible))
		{
			notifierMessage("Ce tracé croise un tracé existant !");
			return false;
		}

		/* Créer l'arête */
		Arete nouvelleArete = new Arete(positionActuelle, cible);
		if (lignesTracees.contains(nouvelleArete))
		{
			notifierMessage("Ce tracé existe déjà !");
			return false;
		}

		lignesTracees.add(nouvelleArete);
		for (Ecouteur e : ecouteurs) { e.LigneTracee(nouvelleArete); }

		/* Marquer le sommet visité */
		cible.setVisite(true);
		if (!sommetsVisites.contains(cible))
		{
			sommetsVisites.add(cible);
		}

		String zone = cible.getZone();
		if (zone != null && !zonesVisitees.contains(zone))
		{
			zonesVisitees.add(zone);
		}

		/* Consommer la carte */
		carteActive = null;
		carteActiveEstJoker = false;

		/* Mettre à jour la position */
		positionActuelle = cible;

		/* Calculer le score */
		calculerScore();

		notifierMessage("Coup joué en (" + lig + "," + col + ") !");
		notifierRefresh();
		return true;
	}

	/*----------------------------*/
	/* Passer le tour             */
	/*----------------------------*/
	public void passerTour()
	{
		if (partieTerminee) return;

		/* Réinitialiser la position pour le prochain tour */
		positionActuelle = null;
		carteActive = null;
		carteActiveEstJoker = false;

		/* Vérifier si la pioche est vide */
		if (pioche.isEmpty())
		{
			mancheCourante++;

			if (mancheCourante > NB_MANCHES)
			{
				partieTerminee = true;
				String classement = "SCORE FINAL : " + score + " points\n" +
					"Infrastructures visitées : " + sommetsVisites.size() + "\n" +
					"Zones visitées : " + zonesVisitees.size();
				for (Ecouteur e : ecouteurs) { e.Fin(classement); }
				return;
			}
			else
			{
				initialiserPioche();
				for (Ecouteur e : ecouteurs) { e.MancheChange(mancheCourante); }
				notifierMessage("Manche " + mancheCourante + " / " + NB_MANCHES + " !");
			}
		}

		notifierRefresh();
	}

	/*----------------------------*/
	/* Calcul du score            */
	/*----------------------------*/
	private void calculerScore()
	{
		this.score = sommetsVisites.size() * 2 + zonesVisitees.size() * 5;

		for (Ecouteur e : ecouteurs)
		{
			e.ScoreChange(this.score);
		}
	}

	/*----------------------------*/
	/* Vérifications              */
	/*----------------------------*/
	private boolean sontAdjacents(Sommet a, Sommet b)
	{
		int dl = Math.abs(a.getLigne() - b.getLigne());
		int dc = Math.abs(a.getColonne() - b.getColonne());
		return (dl <= 1 && dc <= 1) && !(dl == 0 && dc == 0);
	}

	private boolean detecterCroisement(Sommet a, Sommet b)
	{
		if (Math.abs(a.getLigne() - b.getLigne()) == 1 &&
			Math.abs(a.getColonne() - b.getColonne()) == 1)
		{
			Sommet opp1 = graphe.getSommet(a.getLigne(), b.getColonne());
			Sommet opp2 = graphe.getSommet(b.getLigne(), a.getColonne());

			if (opp1 != null && opp2 != null)
			{
				Arete contreDiagonale = new Arete(opp1, opp2);
				return lignesTracees.contains(contreDiagonale);
			}
		}
		return false;
	}

	/*----------------------------*/
	/* Mode debug                 */
	/*----------------------------*/
	public boolean forcerPlacementCaseDebug(int lig, int col)
	{
		Sommet cible = graphe.getSommet(lig, col);
		if (cible == null) return false;

		if (positionActuelle != null)
		{
			Arete liaisonDirecte = new Arete(positionActuelle, cible);
			lignesTracees.add(liaisonDirecte);
			for (Ecouteur e : ecouteurs) { e.LigneTracee(liaisonDirecte); }
		}

		cible.setVisite(true);
		if (!sommetsVisites.contains(cible))
		{
			sommetsVisites.add(cible);
		}

		String zone = cible.getZone();
		if (zone != null && !zonesVisitees.contains(zone))
		{
			zonesVisitees.add(zone);
		}

		positionActuelle = cible;
		carteActive = null;
		calculerScore();
		notifierRefresh();
		return true;
	}

	public void forcerCarteDebug(String typeCarte)
	{
		this.carteActive = typeCarte;
		this.carteActiveEstJoker = typeCarte.equals("JOKER");
		for (Ecouteur e : ecouteurs)
		{
			e.CarteChange(carteActive, carteActiveEstJoker);
		}
		notifierRefresh();
	}

	/*----------------------------*/
	/* Getters                    */
	/*----------------------------*/
	public int            getMancheCourante()   { return this.mancheCourante;      }
	public boolean        estTerminee()         { return this.partieTerminee;      }
	public int            getScore()            { return this.score;               }
	public String         getCarteActive()      { return this.carteActive;         }
	public boolean        isCarteJoker()        { return this.carteActiveEstJoker; }
	public List<Arete>    getLignesTracees()    { return this.lignesTracees;       }
	public List<Sommet>   getSommetsVisites()   { return this.sommetsVisites;      }
	public List<String>   getZonesVisitees()    { return this.zonesVisitees;       }
	public int            getNbCartesRestantes(){ return this.pioche.size();       }
}