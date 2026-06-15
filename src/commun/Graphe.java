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
		this(7, 7); // Taille par défaut au premier lancement
	}

	public Graphe(int lignes, int colonnes)
	{
		this.nbLignes   = lignes;
		this.nbColonnes = colonnes;
		this.sommets    = new ArrayList<Sommet>();
		this.aretes     = new ArrayList<Arete>();
		
		// Initialisation automatique de la grille avec des sommets vides édités
		for (int l = 0; l < lignes; l++) 
		{
			for (int c = 0; c < colonnes; c++) 
			{
				String zone = "NEUTRE";
				if (l < lignes / 2 && c < colonnes / 2) zone = "OUEST";
				else if (l < lignes / 2) zone = "EST";
				else if (c < colonnes / 2) zone = "CHINOIS";
				
				this.sommets.add(new Sommet(l, c, "VIDE", zone));
			}
		}
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
