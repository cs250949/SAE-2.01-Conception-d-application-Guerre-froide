package metier;

public class Sommet 
{
	private int id;
	private int x, y;
	private String nom;

	public Sommet(int id, int x, int y, String nom) 
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.nom = nom;
	}

	public int getId() { return this.id; }
	public String getNom() { return this.nom; }
}