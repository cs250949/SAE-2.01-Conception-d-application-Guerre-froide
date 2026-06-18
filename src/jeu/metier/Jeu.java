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

	/* États de l'automate */
	private static final int ETAT_ANCRAGE  = 0;
	private static final int ETAT_ATTENTE  = 1;
	private static final int ETAT_DEPLACER = 2;
	private static final int ETAT_ERREUR   = 3;

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private int                mancheCourante;
	private boolean            partieTerminee;
	private int                etatCourant;

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
		this.etatCourant      = ETAT_ANCRAGE;
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
		this.etatCourant = ETAT_ANCRAGE;

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

		if (etatCourant != ETAT_ATTENTE)
		{
			notifierMessage("Ancrez-vous d'abord sur une BASE_DEPART !");
			return;
		}

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
			etatCourant = ETAT_ERREUR;
			notifierMessage("Case vide !");
			return false;
		}

		switch (etatCourant)
		{
			case ETAT_ANCRAGE:
				return gererAncrage(cible);

			case ETAT_ATTENTE:
				return gererDeplacement(cible);

			case ETAT_ERREUR:
				etatCourant = ETAT_ATTENTE;
				return gererDeplacement(cible);

			default:
				return false;
		}
	}

	/*----------------------------*/
	/* Gestion de l'ancrage       */
	/*----------------------------*/
	private boolean gererAncrage(Sommet cible)
	{
		if (!cible.getType().equals("BASE_DEPART"))
		{
			etatCourant = ETAT_ERREUR;
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

		etatCourant = ETAT_ATTENTE;
		notifierMessage("Ancrage réussi ! Piochez une carte.");
		notifierRefresh();
		return true;
	}

	/*----------------------------*/
	/* Gestion du déplacement     */
	/*----------------------------*/
	private boolean gererDeplacement(Sommet cible)
	{
		if (carteActive == null)
		{
			etatCourant = ETAT_ERREUR;
			notifierMessage("Piochez d'abord une carte !");
			return false;
		}

		if (!sontReliesParArete(positionActuelle, cible))
		{
			etatCourant = ETAT_ERREUR;
			notifierMessage("Ces deux sommets ne sont pas reliés par une arête !");
			return false;
		}

		if (!carteActiveEstJoker && !cible.getType().equals(carteActive))
		{
			etatCourant = ETAT_ERREUR;
			notifierMessage("L'infrastructure ne correspond pas à votre carte (" + carteActive + ") !");
			return false;
		}

		if (detecterCroisement(positionActuelle, cible))
		{
			etatCourant = ETAT_ERREUR;
			notifierMessage("Ce tracé croise un tracé existant !");
			return false;
		}

		Arete nouvelleArete = new Arete(positionActuelle, cible);
		if (lignesTracees.contains(nouvelleArete))
		{
			etatCourant = ETAT_ERREUR;
			notifierMessage("Ce tracé existe déjà !");
			return false;
		}

		lignesTracees.add(nouvelleArete);
		for (Ecouteur e : ecouteurs) { e.LigneTracee(nouvelleArete); }

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

		carteActive = null;
		carteActiveEstJoker = false;
		positionActuelle = cible;
		calculerScore();

		etatCourant = ETAT_ATTENTE;
		notifierMessage("Déplacement réussi ! Piochez une nouvelle carte.");
		notifierRefresh();
		return true;
	}

	/*----------------------------*/
	/* Vérification arête         */
	/*----------------------------*/
	private boolean sontReliesParArete(Sommet a, Sommet b)
	{
		if (a == null || b == null) return false;
		if (a == b) return false;

		for (Arete arete : graphe.getAretes())
		{
			if ((arete.getDepart() == a && arete.getArrivee() == b) ||
				(arete.getDepart() == b && arete.getArrivee() == a))
			{
				return true;
			}
		}
		return false;
	}

	/*----------------------------*/
	/* Passer le tour             */
	/*----------------------------*/
	public void passerTour()
	{
		if (partieTerminee) return;

		positionActuelle = null;
		carteActive = null;
		carteActiveEstJoker = false;
		etatCourant = ETAT_ANCRAGE;

		if (pioche.isEmpty())
		{
			mancheCourante++;

			if (mancheCourante > NB_MANCHES)
			{
				partieTerminee = true;

				int nbSommets = sommetsVisites.size();
				int nbZones   = zonesVisitees.size();

				String classement = "═══════════════════════════════\n" +
					"  FIN DE PARTIE\n" +
					"═══════════════════════════════\n" +
					"  Sommets visités : " + nbSommets + "\n" +
					"  Zones visitées  : " + nbZones + "\n" +
					"  Calcul : " + nbSommets + " × " + nbZones + " = " + score + " pts\n" +
					"═══════════════════════════════";

				for (Ecouteur e : ecouteurs) { e.Fin(classement); }
				return;
			}
			else
			{
				initialiserPioche();
				for (Ecouteur e : ecouteurs) { e.MancheChange(mancheCourante); }
				notifierMessage("Manche " + mancheCourante + " / " + NB_MANCHES + " ! Ancrez-vous.");
			}
		}

		notifierRefresh();
	}

	/*----------------------------*/
	/* Calcul du score            */
	/*----------------------------*/
	private void calculerScore()
	{
		int nbSommets = this.sommetsVisites.size();
		int nbZones   = this.zonesVisitees.size();

		this.score = nbSommets * nbZones;

		for (Ecouteur e : ecouteurs)
		{
			e.ScoreChange(this.score);
		}
	}

	/*----------------------------*/
	/* Détection croisement       */
	/*----------------------------*/
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
		etatCourant = ETAT_ATTENTE;
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
	public int            getEtatCourant()      { return this.etatCourant;         }
}