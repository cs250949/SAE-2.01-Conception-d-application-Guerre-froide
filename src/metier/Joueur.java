package metier;

public class Joueur 
{
	private int    idJoueur;
	private String nom;
	private Graphe plateauIndividuel;

	public Joueur(int idJoueur, String nom) 
	{
		this.idJoueur = idJoueur;
		this.nom = nom;
		this.plateauIndividuel = new Graphe(); 
		
		this.plateauIndividuel.chargerDepuisFichier("src/images/plateau_conception.txt"); 
	}

	public Graphe getPlateau() 
	{
		return this.plateauIndividuel;
	}

	public String getNom() 
	{
		return this.nom;
	}

	public int getIdJoueur() 
	{
		return this.idJoueur;
	}
}