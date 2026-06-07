package commun;

public class Sommet
{
	private int id;
	private int x;
	private int y;
	private String typeZone;

	public Sommet(int id, int x, int y, String typeZone)
	{
		this.id = id;
		this.x  = x;
		this.y  = y;
		this.typeZone = typeZone;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getX()
	{
		return this.x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return this.y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public String getTypeZone()
	{
		return this.typeZone;
	}

	public void setTypeZone(String typeZone)
	{
		this.typeZone = typeZone;
	}

	public boolean contientPoint(int px, int py)
	{
		int dx = px - this.x;
		int dy = py - this.y;
		return (dx * dx + dy * dy) <= 400;
	}

	public String getPremiereLettre()
	{
		if (this.typeZone == null) return "?";
		if (this.typeZone.equals("HOPITAL"))   return "H";
		if (this.typeZone.equals("FERME"))     return "F";
		if (this.typeZone.equals("PETROLIER")) return "P";
		if (this.typeZone.equals("PORT"))      return "Po";
		if (this.typeZone.equals("TANK"))      return "T";
		return this.typeZone.substring(0, 1);
	}

	public static java.awt.Color getCouleurParType(String type)
	{
		if (type.equals("HOPITAL"  ))   return new java.awt.Color(220, 60, 60, 200);
		if (type.equals("FERME"    ))   return new java.awt.Color(60, 180, 60, 200);
		if (type.equals("PETROLIER"))   return new java.awt.Color(40, 40, 40, 200);
		if (type.equals("PORT"     ))   return new java.awt.Color(60, 120, 220, 200);
		if (type.equals("TANK"     ))   return new java.awt.Color(160, 100, 40, 200);
		if (type.equals("JOKER"    ))   return new java.awt.Color(200, 200, 60, 200);
		return new java.awt.Color(100, 100, 100, 200);
	}
}