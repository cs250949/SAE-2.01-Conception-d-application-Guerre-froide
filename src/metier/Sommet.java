package metier;

public class Sommet 
{
    private int id;
    private int x; 
    private int y; 
    private String typeZone; 

    public Sommet(int id, int x, int y, String typeZone) 
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.typeZone = typeZone;
    }

    public int getId() 
    { 
        return this.id; 
    }

    public int getX() 
    { 
        return this.x; 
    }

    public int getY() 
    { 
        return this.y; 
    }

    public String getTypeZone() 
    { 
        return this.typeZone; 
    }
}