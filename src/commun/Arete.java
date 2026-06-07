package commun;

public class Arete
{
	private int src;
	private int cible;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public Arete(int src, int cible)
	{
		this.src = src;
		this.cible = cible;
	}

	public int getSrc()
	{
		return this.src;
	}

	public int getCible()
	{
		return this.cible;
	}

	/*-------------------------------*/
	/* Vérifie si un sommet est dans */
	/* l'arête                       */
	/*-------------------------------*/
	public boolean contient(int id)
	{
		return (this.src == id || this.cible == id);
	}

	public int getAutre(int id)
	{
		if (this.src == id) return this.cible;
		if (this.cible == id) return this.src;
		return -1;
	}


	/*-------------------------------*/
	/* Bidirectionnel pour éviter    */
	/* les doublons                  */
	/*-------------------------------*/
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;

		Arete autre = (Arete) obj;
		return (this.src == autre.src && this.cible == autre.cible) ||
			   (this.src == autre.cible && this.cible == autre.src);
	}
}