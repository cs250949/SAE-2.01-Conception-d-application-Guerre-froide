package jeu.metier;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import java.util.*;

public class Jeu
{
	public static final int NB_MANCHES = 4;
	
	private List<String>       nomsJoueurs;
	private int                mancheCourante;
	private int                joueurCourantIdx;
	private boolean            partieTerminee;

	private List<List<Arete>>  lignesTracees;
	private List<List<Sommet>> sommetsVisites;
	private List<List<String>> blocsVisites;
	private List<String>       blocsDeDepart;
	private int[]              scores;
	
	private List<String>       pioche;
	private List<String>       cartesPiochees;
	private String             carteActive;
	private boolean            carteActiveEstJoker;
	
	private Graphe             graphe;
	private Sommet             positionActuelle;
	
	private List<Ecouteur>     ecouteurs;
	
	public interface Ecouteur
	{
		void Refresh     ();
		void Message     (String msg);
		void Fin         (String classement);
		void CarteChange (String carte, boolean estJoker);
		void TourChange  (String joueur, int idx);
		void MancheChange(int manche);
		void LigneTracee (Arete arete);
	}
	
	public Jeu()
	{
		this.mancheCourante   = 1;
		this.joueurCourantIdx = 0;
		this.partieTerminee   = false;
		this.ecouteurs        = new ArrayList<>();
		
		this.lignesTracees   = new ArrayList<>();
		this.sommetsVisites  = new ArrayList<>();
		this.blocsVisites    = new ArrayList<>();
		this.blocsDeDepart   = new ArrayList<>();
		this.scores          = new int[4];
		
		this.nomsJoueurs = Arrays.asList("ALPHA", "BRAVO", "CHARLIE", "DELTA");
		
		for (int i = 0; i < 4; i++)
		{
			this.lignesTracees.add(new ArrayList<>());
			this.sommetsVisites.add(new ArrayList<>());
			this.blocsVisites.add(new ArrayList<>());
			this.blocsDeDepart.add(null);
		}
		
		this.pioche = new ArrayList<>();
		this.cartesPiochees = new ArrayList<>();
		this.initialiserPiocheFormelle();
		this.piox();
	}
	
	private void initialiserPiocheFormelle()
	{
		String[] types = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK", "JOKER"};
		for (String t : types)
		{
			for (int i = 0; i < 3; i++)
			{
				pioche.add(t);
			}
		}
		Collections.shuffle(pioche);
	}
	
	public void ajouterEcouteur(Ecouteur e)
	{
		this.ecouteurs.add(e);
	}
	
	public void setGraphe(Graphe g)
	{
		this.graphe = g;
		
		this.lignesTracees.clear();
		this.sommetsVisites.clear();
		this.blocsVisites.clear();
		this.blocsDeDepart.clear();
		
		for (int i = 0; i < 4; i++)
		{
			this.lignesTracees.add(new ArrayList<>());
			this.sommetsVisites.add(new ArrayList<>());
			this.blocsVisites.add(new ArrayList<>());
			this.blocsDeDepart.add(null);
		}
		
		for (Sommet s : g.getSommets())
		{
			s.setVisite(false);
		}
		
		this.positionActuelle = null;
		this.mancheCourante   = 1;
		this.joueurCourantIdx = 0;
		this.partieTerminee   = false;
		
		this.notifierRefresh();
	}
	
	public boolean jouerCoup(int lig, int col)
	{
		if (partieTerminee) return false;
		
		Sommet cible = graphe.getSommet(lig, col);
		if (cible == null) return false;
		
		// PREMIER COUP DE L'ANCRAGE D'UNE MANCHE
		if (positionActuelle == null)
		{
			if (!cible.getType().equals("BASE_DEPART"))
			{
				for (Ecouteur e : ecouteurs) 
					e.Message("Vous devez commencer sur une BASE_DEPART du plateau !");
				return false;
			}
			
			blocsDeDepart.set(joueurCourantIdx, cible.getBloc());
			if (!sommetsVisites.get(joueurCourantIdx).contains(cible))
				sommetsVisites.get(joueurCourantIdx).add(cible);
			
			if (!blocsVisites.get(joueurCourantIdx).contains(cible.getBloc()))
				blocsVisites.get(joueurCourantIdx).add(cible.getBloc());
				
			cible.setVisite(true);
			positionActuelle = cible;
			
			for (Ecouteur e : ecouteurs) 
				e.Message("Ancrage réussi sur la base en (" + lig + "," + col + ")");
			
			notifierRefresh();
			return true;
		}
		
		if (!sontAdjacents(positionActuelle, cible))
		{
			for (Ecouteur e : ecouteurs) e.Message("Les cases ne sont pas géométriquement adjacentes.");
			return false;
		}
		
		if (!carteActiveEstJoker && !cible.getType().equals(carteActive))
		{
			for (Ecouteur e : ecouteurs) e.Message("L'infrastructure ne correspond pas à la carte piochée.");
			return false;
		}
		
		if (detecterCroisementDiagonale(positionActuelle, cible))
		{
			for (Ecouteur e : ecouteurs) e.Message("Mouvement interdit : Les tracés tactiques ne peuvent pas se croiser.");
			return false;
		}
		
		Arete nouvelleArete = new Arete(positionActuelle, cible);
		if (lignesTracees.get(joueurCourantIdx).contains(nouvelleArete))
		{
			for (Ecouteur e : ecouteurs) e.Message("Ligne déjà capturée.");
			return false;
		}
		
		lignesTracees.get(joueurCourantIdx).add(nouvelleArete);
		for (Ecouteur e : ecouteurs) e.LigneTracee(nouvelleArete);
		
		cible.setVisite(true);
		if (!sommetsVisites.get(joueurCourantIdx).contains(cible))
			sommetsVisites.get(joueurCourantIdx).add(cible);
			
		if (!blocsVisites.get(joueurCourantIdx).contains(cible.getBloc()))
			blocsVisites.get(joueurCourantIdx).add(cible.getBloc());
		
		positionActuelle = cible;
		passerTour();
		return true;
	}
	
	public void passerTour()
	{
		this.positionActuelle = null;
		
		joueurCourantIdx = (joueurCourantIdx + 1) % 4;
		if (joueurCourantIdx == 0)
		{
			piox();
		}
		
		for (Ecouteur e : ecouteurs)
		{
			e.TourChange(nomsJoueurs.get(joueurCourantIdx), joueurCourantIdx);
			e.Message("Au tour de l'Agent : " + nomsJoueurs.get(joueurCourantIdx));
		}
		notifierRefresh();
	}
	
	private void piox()
	{
		if (pioche.isEmpty())
		{
			mancheCourante++;
			if (mancheCourante > NB_MANCHES)
			{
				partieTerminee = true;
				calculerScoresFinaux();
				for (Ecouteur e : ecouteurs) e.Fin(getClassement());
				return;
			}
			else
			{
				initialiserPiocheFormelle();
				for (Ecouteur e : ecouteurs) e.MancheChange(mancheCourante);
			}
		}
		
		carteActive = pioche.remove(0);
		cartesPiochees.add(carteActive);
		carteActiveEstJoker = carteActive.equals("JOKER");
		
		for (Ecouteur e : ecouteurs) e.CarteChange(carteActive, carteActiveEstJoker);
	}
	
	private boolean sontAdjacents(Sommet a, Sommet b)
	{
		int dl = Math.abs(a.getLigne() - b.getLigne());
		int dc = Math.abs(a.getColonne() - b.getColonne());
		return (dl <= 1 && dc <= 1) && !(dl == 0 && dc == 0);
	}
	
	private boolean detecterCroisementDiagonale(Sommet a, Sommet b)
	{
		if (Math.abs(a.getLigne() - b.getLigne()) == 1 && Math.abs(a.getColonne() - b.getColonne()) == 1)
		{
			Sommet opp1 = graphe.getSommet(a.getLigne(), b.getColonne());
			Sommet opp2 = graphe.getSommet(b.getLigne(), a.getColonne());
			if (opp1 != null && opp2 != null)
			{
				Arete contreDiagonale = new Arete(opp1, opp2);
				return lignesTracees.get(joueurCourantIdx).contains(contreDiagonale);
			}
		}
		return false;
	}
	
	public boolean forcerPlacementCaseDebug(int lig, int col)
	{
		Sommet cible = graphe.getSommet(lig, col);
		if (cible == null || positionActuelle == null) return false;
		
		Arete liaisonDirecte = new Arete(positionActuelle, cible);
		lignesTracees.get(joueurCourantIdx).add(liaisonDirecte);
		for (Ecouteur e : ecouteurs) e.LigneTracee(liaisonDirecte);
		
		cible.setVisite(true);
		if (!sommetsVisites.get(joueurCourantIdx).contains(cible))
			sommetsVisites.get(joueurCourantIdx).add(cible);
		if (!blocsVisites.get(joueurCourantIdx).contains(cible.getBloc()))
			blocsVisites.get(joueurCourantIdx).add(cible.getBloc());
		
		positionActuelle = cible;
		notifierRefresh();
		return true;
	}
	
	public void forcerCarteDebug(String typeCarte)
	{
		this.carteActive = typeCarte;
		this.carteActiveEstJoker = typeCarte.equals("JOKER");
		for (Ecouteur e : ecouteurs) e.CarteChange(carteActive, carteActiveEstJoker);
	}

	private void calculerScoresFinaux()
	{
		for (int i = 0; i < 4; i++)
		{
			scores[i] = sommetsVisites.get(i).size() * 2 + blocsVisites.get(i).size() * 5;
		}
	}
	
	private String getClassement()
	{
		String res = "FIN DE LA SOUTENANCE\n\n";
		for (int i = 0; i < 4; i++)
		{
			res += nomsJoueurs.get(i) + " : " + scores[i] + " points\n";
		}
		return res;
	}
	
	private void notifierRefresh()
	{
		for (Ecouteur e : ecouteurs) e.Refresh();
	}
	
	/*-------------------------------------------------*/
	/* Getters Techniques                              */
	/*-------------------------------------------------*/
	public List<List<Arete>>  getLignesTracees()   { return lignesTracees; }
	public List<List<Sommet>> getSommetsVisites()  { return sommetsVisites; }
	public List<List<String>> getBlocsVisites()    { return blocsVisites; }
	public List<String>       getBlocsDeDepart()   { return blocsDeDepart; }
	public List<String>       getNomsJoueurs()     { return nomsJoueurs; }
	public int                getJoueurCourantIdx(){ return joueurCourantIdx; }
	public int                getMancheCourante()  { return mancheCourante; }
	public String             getCarteActive()     { return carteActive; }
	public int[]              getScores()          { return scores; }
}