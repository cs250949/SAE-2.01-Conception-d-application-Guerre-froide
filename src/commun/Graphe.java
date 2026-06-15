package commun;

import java.util.ArrayList;

public class Graphe
{
	private int nbLignes;
	private int nbColonnes;

	private ArrayList<Sommet> sommets;
	private ArrayList<Arete>  aretes;

	public Graphe()
	{
		this(7, 7); // Taille par défaut
	}

	public Graphe(int lignes, int colonnes)
	{
		this.nbLignes   = lignes;
		this.nbColonnes = colonnes;
		this.sommets    = new ArrayList<Sommet>();
		this.aretes     = new ArrayList<Arete>();
		
		// Génération automatique des Sommets (Cases) avec répartition par Zones
		for (int l = 0; l < lignes; l++) 
		{
			for (int c = 0; c < colonnes; c++) 
			{
				// Attribution factice d'un bloc/zone géographique pour le visuel
				String zone = "NEUTRE";
				if (l < lignes / 2 && c < colonnes / 2) zone = "OUEST";
				else if (l < lignes / 2) zone = "EST";
				else if (c < colonnes / 2) zone = "CHINOIS";
				
				this.sommets.add(new Sommet(l, c, "VIDE", zone));
			}
		}

		// Maillage automatique des Arêtes
		genererLiaisonsAutomatiques();
	}

	/**
	 *  Relie chaque case à ses voisins directs et diagonaux
	 */
	private void genererLiaisonsAutomatiques()
	{
		int[][] directions = {
			{-1, 0},  // Haut
			{1, 0},   // Bas
			{0, -1},  // Gauche
			{0, 1},   // Droite
			{-1, -1}, // Haut-Gauche
			{-1, 1},  // Haut-Droite
			{1, -1},  // Bas-Gauche
			{1, 1}    // Bas-Droite
		};

		for (Sommet s : this.sommets)
		{
			int lig = s.getLigne();
			int col = s.getColonne();

			for (int[] dir : directions)
			{
				int vLig = lig + dir[0];
				int vCol = col + dir[1];

				// Si le voisin  est bien dans les limites de la grille configurée
				if (vLig >= 0 && vLig < nbLignes && vCol >= 0 && vCol < nbColonnes)
				{
					Sommet voisin = this.getSommet(vLig, vCol);
					if (voisin != null)
					{
						// On déclare le lien de voisinage bidirectionnel
						s.ajouterVoisin(voisin);

						// On crée l'arête physique si elle n'existe pas déjà dans l'autre sens
						if (!areteExisteDeja(s, voisin))
						{
							this.aretes.add(new Arete(s, voisin));
						}
					}
				}
			}
		}
	}

	private boolean areteExisteDeja(Sommet s1, Sommet s2)
	{
		for (Arete a : this.aretes)
		{
			if (a.contient(s1) && a.contient(s2))
			{
				return true;
			}
		}
		return false;
	}

	public ArrayList<Sommet> getSommets() { return this.sommets; }
	public ArrayList<Arete>  getAretes()  { return this.aretes; }
	public int getNbLignes()   { return this.nbLignes; }
	public int getNbColonnes() { return this.nbColonnes; }

	public Sommet getSommet(int ligne, int colonne)
	{
		for (Sommet s : this.sommets)
		{
			if (s.getLigne() == ligne && s.getColonne() == colonne)
			{
				return s;
			}
		}
		return null;
	}

	public void designerSommet(int lig, int col, String type)
	{
		Sommet s = this.getSommet(lig, col);
		if (s != null)
		{
			s.setType(type);
		}
	}
}
