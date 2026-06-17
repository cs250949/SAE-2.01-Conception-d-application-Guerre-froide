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
		this(9, 8);
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

	/*----------------------------*/
	/* Chargement depuis fichier  */
	/*----------------------------*/
	public static Graphe chargerDepuisFichier(String cheminFichier)
	{
		Graphe graphe = null;

		try
		{
			java.io.BufferedReader lecteur = new java.io.BufferedReader(
				new java.io.FileReader(cheminFichier));

			String ligne = lecteur.readLine();
			if (ligne != null)
			{
				String[] dimensions = ligne.split(" ");
				int nbLignes   = Integer.parseInt(dimensions[0]);
				int nbColonnes = Integer.parseInt(dimensions[1]);

				graphe = new Graphe(nbLignes, nbColonnes);

				while ((ligne = lecteur.readLine()) != null)
				{
					ligne = ligne.trim();
					if (ligne.isEmpty()) continue;

					String[] parts = ligne.split(" ");
					if (parts.length >= 4)
					{
						int    lig  = Integer.parseInt(parts[0]);
						int    col  = Integer.parseInt(parts[1]);
						String type = parts[2];
						String zone = parts[3];

						Sommet s = new Sommet(lig, col, type, zone);
						graphe.getSommets().add(s);
					}
				}
			}

			lecteur.close();

			if (graphe != null)
			{
				graphe.remonterAretes();
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur chargement plateau : " + e.getMessage());
			return null;
		}

		return graphe;
	}
}