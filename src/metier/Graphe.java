package metier;


public class Graphe 
{
	private static final int NB_CASES = 49; // 7x7
	private Sommet[] listeSommets;
	private boolean[][] matriceAdjacence; // true = un lien existe, false = pas de lien

	public Graphe() 
	{
		this.listeSommets = new Sommet[NB_CASES];
		this.matriceAdjacence = new boolean[NB_CASES][NB_CASES];
		
		// Initialisation des sommets
		int id = 0;
		for (int i = 0; i < 7; i++) 
		{
			for (int j = 0; j < 7; j++) 
			{
				this.listeSommets[id] = new Sommet(id, i, j, "Case_" + id);
				id++;
			}
		}
	}

	// Méthode pour créer un lien (une arête) entre deux cases
	public void ajouterLien(int idSource, int idCible) 
	{
		this.matriceAdjacence[idSource][idCible] = true;
		this.matriceAdjacence[idCible][idSource] = true; // Graphe non-orienté (le lien va dans les deux sens)
		System.out.println(" Lien créé entre " + idSource + " et " + idCible);
	}

	// Vérifier si deux cases sont connectées
	public boolean sontConnectes(int idSource, int idCible) 
	{
		return this.matriceAdjacence[idSource][idCible];
	}
}