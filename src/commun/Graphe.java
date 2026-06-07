package commun;

import java.io.*;
import java.util.*;

public class Graphe
{
	private List<Sommet> sommets;
	private List<Arete>  aretes;
	private int          largeur;
	private int          hauteur;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public Graphe()
	{
		this.sommets = new ArrayList<>();
		this.aretes  = new ArrayList<>();
		this.largeur = 800;
		this.hauteur = 600;
	}

	/*--------------------------------*/
	/* Gestion des sommets et arêtes  */
	/*--------------------------------*/
	public void ajouterSommet(Sommet s)
	{
		if (!this.sommets.contains(s)) this.sommets.add(s);
	}

	public void supprimerSommet(int id)
	{
		Sommet aSupprimer = null;
		for (Sommet s : this.sommets)
		{
			if (s.getId() == id)
			{
				aSupprimer = s;
				break;
			}
		}
		if (aSupprimer != null) this.sommets.remove(aSupprimer);

		List<Arete> aretesASupprimer = new ArrayList<>();
		for (Arete a : this.aretes)
		{
			if (a.contient(id)) aretesASupprimer.add(a);
		}
		this.aretes.removeAll(aretesASupprimer);
	}

	public Sommet trouverSommet(int id)
	{
		for (Sommet s : this.sommets)
		{
			if (s.getId() == id) return s;
		}
		return null;
	}

	public List<Sommet> getSommets()
	{
		return new ArrayList<>(this.sommets);
	}

	public void ajouterArete(Arete a)
	{
		if (!this.aretes.contains(a)) this.aretes.add(a);
	}

	public void supprimerArete(Arete a)
	{
		this.aretes.remove(a);
	}

	public boolean areteExiste(int id1, int id2)
	{
		return this.aretes.contains(new Arete(id1, id2));
	}

	public List<Arete> getAretes()
	{
		return new ArrayList<>(this.aretes);
	}

	public int getDegre(int id)
	{
		int degre = 0;
		for (Arete a : this.aretes)
		{
			if (a.contient(id)) degre++;
		}
		return degre;
	}

	/*-------------------------------*/
	/* Chargement depuis un fichier   */
	/*-------------------------------*/
	public void chargerDepuisFichier(String cheminFichier) throws IOException
	{
		this.sommets.clear();
		this.aretes.clear();
		BufferedReader br = new BufferedReader(new FileReader(cheminFichier));
		String ligne;

		while ((ligne = br.readLine()) != null)
		{
			ligne = ligne.trim();
			if (ligne.startsWith("#") || ligne.isEmpty()) continue;

			String[] parts = ligne.split(";");
			if (parts.length >= 4)
			{
				int id = Integer.parseInt(parts[0]);
				int x  = Integer.parseInt(parts[1]);
				int y  = Integer.parseInt(parts[2]);
				String typeZone = parts[3];
				this.ajouterSommet(new Sommet(id, x, y, typeZone));
			}
		}
		br.close();
	}

	public int getLargeur()
	{
		return this.largeur;
	}

	public void setLargeur(int largeur)
	{
		this.largeur = largeur;
	}

	public int getHauteur()
	{
		return this.hauteur;
	}

	public void setHauteur(int hauteur)
	{
		this.hauteur = hauteur;
	}

	public int getNbSommets()
	{
		return this.sommets.size();
	}
}