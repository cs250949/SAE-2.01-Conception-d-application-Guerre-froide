package commun;

import java.util.ArrayList;

public class Graphe
{
	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private int               nbLignes;
	private int               nbColonnes;
	private ArrayList<Sommet> sommets;
	private ArrayList<Arete>  aretes;

	/*----------------------------*/
	/* Constructeurs              */
	/*----------------------------*/
	public Graphe()
	{
		this(7, 7);
	}

	public Graphe(int nbLignes, int nbColonnes)
	{
		this.nbLignes = nbLignes;
		this.nbColonnes = nbColonnes;
		this.sommets = new ArrayList<Sommet>();
		this.aretes  = new ArrayList<Arete>();
	}

	/*----------------------------*/
	/* Getters                    */
	/*----------------------------*/
	public ArrayList<Sommet> getSommets()  { return this.sommets;  }
	public ArrayList<Arete>  getAretes()   { return this.aretes;   }
	public int getNbLignes()               { return this.nbLignes; }
	public int getNbColonnes()             { return this.nbColonnes; }

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

	/*----------------------------*/
	/* Méthodes                   */
	/*----------------------------*/
	public boolean estCaseAutorisee(int ligne, int colonne)
	{
		return ligne >= 0 && ligne < this.nbLignes && colonne >= 0 && colonne < this.nbColonnes;
	}

	public String determinerBloc(int ligne, int colonne)
	{
		int midLig = this.nbLignes / 2;
		int midCol = this.nbColonnes / 2;

		if (colonne < midCol && ligne < midLig)       { return "OUEST";      }
		if (colonne >= midCol && ligne < midLig)      { return "EST";        }
		if (ligne >= midLig && colonne < midCol)      { return "CHINOIS";    }
		return "NON-ALIGNE";
	}

	public void remonterAretes()
	{
		this.aretes.clear();

		int taille = this.sommets.size();

		for (int i = 0; i < taille; i++)
		{
			this.sommets.get(i).getVoisins().clear();
		}

		for (int i = 0; i < taille; i++)
		{
			Sommet s = this.sommets.get(i);
			this.chercherVoisins(s.getLigne(), s.getColonne(), s);
		}
	}

	private void chercherVoisins(int lig, int col, Sommet sommetCourant)
	{
		int[][] directions = {
			{-1,  0}, {1,  0}, {0, -1}, {0,  1},
			{-1, -1}, {-1, 1}, {1, -1}, {1,  1}
		};

		int nbDirections = directions.length;

		for (int i = 0; i < nbDirections; i++)
		{
			int dLig = directions[i][0];
			int dCol = directions[i][1];

			int ligCherche = lig + dLig;
			int colCherche = col + dCol;
			boolean continuer = true;

			while (ligCherche >= 0 && ligCherche < this.nbLignes &&
			       colCherche >= 0 && colCherche < this.nbColonnes && continuer)
			{
				Sommet sommetTrouve = this.getSommet(ligCherche, colCherche);

				if (sommetTrouve != null)
				{
					sommetCourant.ajouterVoisin(sommetTrouve);

					Arete nouvelleArete = new Arete(sommetCourant, sommetTrouve);

					if (!this.aretes.contains(nouvelleArete))
					{
						this.aretes.add(nouvelleArete);
					}

					continuer = false;
				}

				ligCherche += dLig;
				colCherche += dCol;
			}
		}
	}
}