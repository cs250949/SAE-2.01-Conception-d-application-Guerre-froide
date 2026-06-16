package commun;

public class Arete
{
	private Sommet  depart;
	private Sommet  arrivee;
	private int     emprunteeParJoueur; 
	
	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public Arete(Sommet depart, Sommet arrivee)
	{
		this.depart = depart;
		this.arrivee = arrivee;
		this.emprunteeParJoueur = 0;
	}
	
	/*-------------------------------*/
	/* Getters et Setters            */
	/*-------------------------------*/
	public Sommet getDepart()  { return this.depart; }
	public Sommet getArrivee() { return this.arrivee; }
	
	public boolean isEmprunteeParJoueur() { 
		return this.emprunteeParJoueur == 1; 
	}
	
	public void setEmprunteeParJoueur(int valeur) 
	{ 
		this.emprunteeParJoueur = valeur; 
	}
	
	/*-------------------------------*/
	/* Vérifications                 */
	/*-------------------------------*/
	public boolean contient(Sommet s)
	{
		return this.depart == s || this.arrivee == s;
	}
	
	public Sommet getAutre(Sommet s)
	{
		if (this.depart == s)  return this.arrivee;
		if (this.arrivee == s) return this.depart;
		return null;
	}
	
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Arete autre = (Arete) obj;
		return (this.depart == autre.depart && this.arrivee == autre.arrivee) ||
		       (this.depart == autre.arrivee && this.arrivee == autre.depart);
	}
}
