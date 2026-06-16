package commun;

import java.util.ArrayList;

public class Graphe
{
	public static final int NB_LIGNES   = 7;
	public static final int NB_COLONNES = 7;

	private ArrayList<Sommet> sommets;
	private ArrayList<Arete>  aretes;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public Graphe()
	{
		this.sommets = new ArrayList<Sommet>();
		this.aretes  = new ArrayList<Arete>();
	}

	/*-------------------------------*/
	/* Getters                       */
	/*-------------------------------*/
	public ArrayList<Sommet> getSommets() { return this.sommets; }
	public ArrayList<Arete>  getAretes()  { return this.aretes; }

	public int getNbLignes()   { return NB_LIGNES; }
	public int getNbColonnes() { return NB_COLONNES; }

	/*------------------------------- */
	/* Recherche de Sommet par lig/col*/
	/*-------------------------------*/
	public Sommet getSommet(int ligne, int colonne)
	{
		int taille = this.sommets.size();
		for (int i = 0; i < taille; i++)
		{
			Sommet s = this.sommets.get(i);
			if (s.getLigne() == ligne && s.getColonne() == colonne)
			{
				return s;
			}
		}
		return null;
	}

	/*-------------------------------*/
	/* Sécurités de l'éditeur        */
	/*-------------------------------*/
	public boolean estCaseAutorisee(int ligne, int colonne)
	{
		// Par défaut dans ton éditeur, toutes les cases de la grille 7x7 sont cliquables
		return ligne >= 0 && ligne < NB_LIGNES && colonne >= 0 && colonne < NB_COLONNES;
	}

	public String determinerBloc(int ligne, int colonne)
	{
		// Attribue un bloc ou une zone par défaut selon la position 
		if (colonne < 3 && ligne < 3) return "OUEST";
		if (colonne >= 4 && ligne < 4) return "EST";
		if (ligne >= 4 && colonne < 3) return "CHINOIS";
		return "NON-ALIGNE";
	}

	/*-------------------------------*/
	/* RECONSTRUCTION DES LIENS      */
	/*-------------------------------*/
	
	/**
	 * Parcourt toute la liste des sommets actuels pour recalculer leurs voisins
	 * et recréer les arêtes visuelles à afficher sur le plateau.
	 */
	public void remonterAretes()
	{
		// On vide les anciennes arêtes pour ne pas faire de doublons
		this.aretes.clear();
		
		int taille = this.sommets.size();
		
		// 1. On vide d'abord la liste des voisins de chaque sommet pour repartir à zéro
		for (int i = 0; i < taille; i++)
		{
			this.sommets.get(i).getVoisins().clear();
		}

		// 2. On lance le rayon de recherche pour chaque sommet présent
		for (int i = 0; i < taille; i++)
		{
			Sommet s = this.sommets.get(i);
			this.chercherVoisins(s.getLigne(), s.getColonne(), s);
		}
	}

	/**
	 * Logique par rayons du groupe réparée et convertie en entiers purs
	 */
	private void chercherVoisins(int lig, int col, Sommet sommetCourant)
	{
		
		int[][] directions = {
			{-1, 0},  // 0 : Haut
			{1, 0},   // 1 : Bas
			{0, -1},  // 2 : Gauche
			{0, 1},   // 3 : Droite
			{-1, -1}, // 4 : Haut-Gauche
			{-1, 1},  // 5 : Haut-Droite
			{1, -1},  // 6 : Bas-Gauche
			{1, 1}    // 7 : Bas-Droite
		};
		
		int nbDirections = directions.length;
		
		for (int i = 0; i < nbDirections; i++)
		{
			int dLig = directions[i][0];
			int dCol = directions[i][1]; 
			
			int ligCherche = lig + dLig;
			int colCherche = col + dCol; 
			boolean continuer = true; 

			// On pousse le rayon tant qu'on ne sort pas de la grille 7x7
			while (ligCherche >= 0 && ligCherche < NB_LIGNES &&
			       colCherche >= 0 && colCherche < NB_COLONNES && continuer)
			{
				Sommet sommetTrouve = this.getSommet(ligCherche, colCherche);
				
				if (sommetTrouve != null)
				{
					// Ajoute le sommet trouvé dans la collection de liens du sommet courant
					sommetCourant.ajouterVoisin(sommetTrouve);
					
					// Crée l'arête reliant les deux s'il n'existe pas encore
					Arete nouvelleArete = new Arete(sommetCourant, sommetTrouve);
					
					if (!this.aretes.contains(nouvelleArete)) 
					{
						this.aretes.add(nouvelleArete);
					}
					
					// On a trouvé le premier sommet visible dans cette ligne de mire, on arrête le rayon
					continuer = false;
				}
				
				
				ligCherche += dLig;
				colCherche += dCol;
			}
		}
	}
}
