package commun;

import java.util.ArrayList;

public class Sommet
{
	private int               ligne;
	private int               colonne;
	private String            type;
	private String            bloc;
	private int               visite;
	private ArrayList<Sommet> voisins;
	
	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public Sommet(int ligne, int colonne, String type, String bloc)
	{
		this.ligne   = ligne;
		this.colonne = colonne;
		this.type    = type;
		this.bloc    = bloc;
		this.visite  = 0; // 0 signifie "false"
		this.voisins = new ArrayList<Sommet>();
	}
	
	/*-------------------------------*/
	/* Getters                       */
	/*-------------------------------*/
	public int getLigne()   { return this.ligne;   }
	public int getColonne() { return this.colonne; }
	public String getType() { return this.type;    } 
	public String getBloc() { return this.bloc;    }
	
	// Alias commode pour correspondre à l'appel s.getZone() fait dans PanneauPlateau
	public String getZone() { return this.bloc;    }
	
	public boolean isVisite() { 
		return this.visite == 1; 
	}
	
	public ArrayList<Sommet> getVoisins() { 
		return this.voisins; 
	}

	/*------------------------------- */
	/* Setters                        */
	/*------------------------------- */
	public void setLigne   (int ligne)   { this.ligne = ligne;     }
	public void setColonne (int colonne) { this.colonne = colonne; }
	public void setType    (String type) { this.type = type;       }
	public void setBloc    (String bloc) { this.bloc = bloc;       }
	
	public void setVisite(boolean visite) { 
		this.visite = visite ? 1 : 0; 
	}
	
	/*---------------------------------*/
	/* Gestion des liens de voisinage  */
	/*---------------------------------*/
	public void ajouterVoisin(Sommet s)
	{
		if (s != null && !this.voisins.contains(s))
		{
			this.voisins.add(s);
		}
	}
	
	/*-------------------------------*/
	/* Vérifications                 */
	/*-------------------------------*/
	public boolean estVide()
	{
		if (this.type == null) return true;
		return this.type.equals("VIDE");
	}
	
	public String toString()
	{
		return this.ligne + ";" + this.colonne + ";" + this.type + ";" + this.bloc;
	}
}