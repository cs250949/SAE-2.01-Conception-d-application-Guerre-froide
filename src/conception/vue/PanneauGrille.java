package conception.vue;

import commun.Graphe;
import commun.Sommet;
import conception.controleur.ControleurConception;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PanneauGrille extends JPanel
{
	public interface CelluleListener
	{
		void CelluleCliquee(int ligne, int colonne);
		void CelluleSurvolee(int ligne, int colonne);
	}

	private ControleurConception controleur;
	private CelluleListener      listener;
	private Graphe               graphe;
	private JButton[][]          boutons;
	private int                  survolLigne   = -1;
	private int                  survolColonne = -1;

	private Image imgHopital;
	private Image imgFerme;
	private Image imgPetrolier;
	private Image imgPort;
	private Image imgTank;
	private Image imgBase;

	private static final Color COULEUR_FRONTIERE = new Color(220, 200, 80);
	private static final Color COULEUR_BORD_BASE = new Color(60,  60,  75);

	public PanneauGrille(ControleurConception controleur, CelluleListener listener)
	{
		this.controleur = controleur;
		this.listener   = listener;
		this.graphe     = new Graphe();

		chargerImages();
		setBackground(new Color(35, 35, 45));
		construireBoutons();
	}

	private void construireBoutons()
	{
		this.removeAll();

		int nbL = graphe.getNbLignes();
		int nbC = graphe.getNbColonnes();

		this.boutons = new JButton[nbL][nbC];
		setLayout(new GridLayout(nbL, nbC, 2, 2));

		for (int i = 0; i < nbL; i++)
		{
			for (int j = 0; j < nbC; j++)
			{
				final int li = i;
				final int co = j;

				JButton btn = new JButton();
				btn.setFocusPainted     (false);
				btn.setContentAreaFilled(false);
				btn.setOpaque           (true);
				btn.setBorder           (BorderFactory.createLineBorder(COULEUR_BORD_BASE, 1));

				btn.addActionListener(e -> listener.CelluleCliquee(li, co));

				btn.addMouseListener(new MouseAdapter()
				{
					
					public void mouseEntered(MouseEvent e)
					{
						survolLigne   = li;
						survolColonne = co;
						listener.CelluleSurvolee(li, co);
						repaint();
					}

					
					public void mouseExited(MouseEvent e)
					{
						survolLigne   = -1;
						survolColonne = -1;
						listener.CelluleSurvolee(-1, -1);
						repaint();
					}
				});

				boutons[i][j] = btn;
				add(btn);
			}
		}
	}

	public void setGraphe(Graphe g)
	{
		boolean changeDimension =
			this.graphe.getNbLignes()   != g.getNbLignes() ||
			this.graphe.getNbColonnes() != g.getNbColonnes();

		this.graphe = g;

		if (changeDimension)
			construireBoutons();

		mettreAJourIcons();
		revalidate();
		repaint();
	}

	public Graphe getGraphe() { return graphe; }

	public void mettreAJourIcons()
	{
		int nbL = graphe.getNbLignes();
		int nbC = graphe.getNbColonnes();

		for (int i = 0; i < nbL; i++)
		{
			for (int j = 0; j < nbC; j++)
			{
				Sommet  s   = graphe.getSommet(i, j);
				JButton btn = boutons[i][j];
				if (s == null) continue;

				if (!graphe.estCaseAutorisee(i, j))
				{
					btn.setBackground(new Color(20, 20, 25));
					btn.setEnabled(false);
				}
				else
				{
					btn.setEnabled(true);
					switch (s.getBloc())
					{
						case "OUEST":      btn.setBackground(new Color(45, 55, 80)); break;
						case "EST":        btn.setBackground(new Color(80, 50, 45)); break;
						case "NON_ALIGNE": btn.setBackground(new Color(75, 75, 45)); break;
						case "CHINOIS":    btn.setBackground(new Color(80, 45, 65)); break;
						case "CENTRE":     btn.setBackground(new Color(55, 55, 60)); break;
						default:           btn.setBackground(new Color(40, 40, 50)); break;
					}
				}

				appliquerBordureZone(btn, i, j, nbL, nbC);

				Image img = null;
				switch (s.getType())
				{
					case "HOPITAL":     img  = imgHopital;   break;
					case "FERME":       img  = imgFerme;     break;
					case "PETROLIER":   img  = imgPetrolier; break;
					case "PORT":        img  = imgPort;      break;
					case "TANK":        img  = imgTank;      break;
					case "BASE_DEPART": img  = imgBase;      break;
				}

				if (img != null)
				{
					btn.setIcon(new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
					btn.setText("");
				}
				else
				{
					btn.setIcon(null);
					if (!s.getType().equals("VIDE"))
					{
						btn.setText(s.getType().substring(0, Math.min(3, s.getType().length())));
						btn.setForeground(Color.WHITE);
					}
					else
					{
						btn.setText("");
					}
				}
			}
		}
	}

	private void appliquerBordureZone(JButton btn, int i, int j, int nbL, int nbC)
	{
		Sommet courant = graphe.getSommet(i, j);
		if (courant == null)
		{
			btn.setBorder(BorderFactory.createLineBorder(COULEUR_BORD_BASE, 1));
			return;
		}
		String blocCourant = courant.getBloc();

		int eN = 1, eS = 1, eW = 1, eE = 1;

		if (i > 0)
		{
			Sommet voisin = graphe.getSommet(i - 1, j);
			if (voisin != null && !voisin.getBloc().equals(blocCourant)) eN = 3;
		}
		if (i < nbL - 1)
		{
			Sommet voisin = graphe.getSommet(i + 1, j);
			if (voisin != null && !voisin.getBloc().equals(blocCourant)) eS = 3;
		}
		if (j > 0)
		{
			Sommet voisin = graphe.getSommet(i, j - 1);
			if (voisin != null && !voisin.getBloc().equals(blocCourant)) eW = 3;
		}
		if (j < nbC - 1)
		{
			Sommet voisin = graphe.getSommet(i, j + 1);
			if (voisin != null && !voisin.getBloc().equals(blocCourant)) eE = 3;
		}

		if (eN == 3 || eS == 3 || eW == 3 || eE == 3)
		{
			btn.setBorder(BorderFactory.createMatteBorder(eN, eW, eS, eE, COULEUR_FRONTIERE));
		}
		else
		{
			btn.setBorder(BorderFactory.createLineBorder(COULEUR_BORD_BASE, 1));
		}
	}

	private void chargerImages()
	{
		try
		{
			File f;
			f = new File("images/hopital.png");     if (f.exists()) imgHopital   = ImageIO.read(f);
			f = new File("images/ferme.png");       if (f.exists()) imgFerme     = ImageIO.read(f);
			f = new File("images/petrolier.png");   if (f.exists()) imgPetrolier = ImageIO.read(f);
			f = new File("images/port.png");        if (f.exists()) imgPort      = ImageIO.read(f);
			f = new File("images/tank.png");        if (f.exists()) imgTank      = ImageIO.read(f);
			f = new File("images/base_depart.png"); if (f.exists()) imgBase      = ImageIO.read(f);
		}
		catch (Exception ignored) {}
	}
	
	
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);

		int nbL = graphe.getNbLignes();
		int nbC = graphe.getNbColonnes();

		if (survolLigne   >= 0 && survolLigne   < nbL &&
			survolColonne >= 0 && survolColonne < nbC)
		{
			JButton    btn = boutons[survolLigne][survolColonne];
			Graphics2D g2  = (Graphics2D) g;
			g2.setColor(new Color(255, 255, 255, 60));
			g2.setStroke(new BasicStroke(3f));
			g2.drawRect(btn.getX() + 1, btn.getY() + 1,
			            btn.getWidth() - 2, btn.getHeight() - 2);
		}
	}
}