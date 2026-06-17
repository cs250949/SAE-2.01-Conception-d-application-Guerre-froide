package jeu.vue;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import jeu.controleur.ControleurJeu;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;

public class PanneauPlateau extends JPanel
{
	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private ControleurJeu              ctrl;
	private Graphe                     graphe;
	private int                        tailleCase;
	private HashMap<String, ImageIcon> cacheImages;
	private int                        hoveredLig;
	private int                        hoveredCol;

	/*----------------------------*/
	/* Interface Listener         */
	/*----------------------------*/
	public interface CelluleListener
	{
		void CelluleCliquee(int ligne, int colonne);
		void CelluleSurvolee(int ligne, int colonne);
	}

	private CelluleListener listener;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public PanneauPlateau(ControleurJeu ctrl, Graphe graphe, int tailleCase, CelluleListener listener)
	{
		this.ctrl        = ctrl;
		this.graphe      = graphe;
		this.tailleCase  = tailleCase;
		this.listener    = listener;
		this.hoveredLig  = -1;
		this.hoveredCol  = -1;
		this.cacheImages = new HashMap<String, ImageIcon>();

		chargerImagesInfrastructures();

		int largeurTotale = graphe.getNbColonnes() * this.tailleCase;
		int hauteurTotale = graphe.getNbLignes()  * this.tailleCase;

		this.setPreferredSize(new Dimension(largeurTotale, hauteurTotale));
		this.setBackground(new Color(33, 37, 43));

		/* Gestion des clics et survols */
		GereSouris gereSouris = new GereSouris();
		this.addMouseListener(gereSouris);
		this.addMouseMotionListener(gereSouris);
	}

	/*----------------------------*/
	/* Chargement des images      */
	/*----------------------------*/
	private void chargerImagesInfrastructures()
	{
		String[] types = {"HOPITAL", "PORT", "USINE", "CHAR", "FER", "PET", "TAN", "BAS", "BASE_DEPART", "FERME", "PETROLIER", "TANK"};

		int tailleIcone = this.tailleCase - 10;
		if (tailleIcone < 16) { tailleIcone = 16; }

		for (String type : types)
		{
			try
			{
				String nomFichier = type.toLowerCase() + ".png";
				java.net.URL imgUrl = getClass().getResource("/images/" + nomFichier);

				if (imgUrl == null)
				{
					System.out.println("Image introuvable : /images/" + nomFichier);
				}
				else
				{
					ImageIcon icon = new ImageIcon(imgUrl);
					Image img = icon.getImage().getScaledInstance(tailleIcone, tailleIcone, Image.SCALE_SMOOTH);
					cacheImages.put(type, new ImageIcon(img));
				}
			}
			catch (Exception e)
			{
				System.out.println("Erreur chargement : " + type);
			}
		}
	}

	/*----------------------------*/
	/* Dessin du plateau          */
	/*----------------------------*/
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (graphe == null) return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int nbLignes = graphe.getNbLignes();
		int nbCols   = graphe.getNbColonnes();

		/* 1. DESSINER LES CASES (zones) */
		for (int lig = 0; lig < nbLignes; lig++)
		{
			for (int col = 0; col < nbCols; col++)
			{
				int x = col * this.tailleCase;
				int y = lig * this.tailleCase;

				Sommet s = graphe.getSommet(lig, col);

				/* Couleur de fond selon la faction/zone */
				Color couleurFond = new Color(48, 52, 63);

				if (s != null && s.getZone() != null)
				{
					String zone = s.getZone();
					if (zone.contains("OUEST"))       { couleurFond = new Color(45, 60, 85);  }
					else if (zone.contains("EST"))    { couleurFond = new Color(90, 50, 50);  }
					else if (zone.contains("CHINOIS")) { couleurFond = new Color(50, 80, 55);  }
					else if (zone.contains("NON"))    { couleurFond = new Color(65, 65, 50);  }
				}

				g.setColor(couleurFond);
				g.fillRect(x, y, this.tailleCase, this.tailleCase);

				/* Bordure de la case */
				g.setColor(new Color(90, 95, 105, 60));
				g.drawRect(x, y, this.tailleCase, this.tailleCase);

				/* Surbrillance si survolée */
				if (lig == hoveredLig && col == hoveredCol)
				{
					g.setColor(new Color(100, 150, 255, 80));
					g.fillRect(x, y, this.tailleCase, this.tailleCase);
				}

				/* Icône de l'infrastructure */
				if (s != null && s.getType() != null && !s.getType().equals("VIDE"))
				{
					ImageIcon icon = cacheImages.get(s.getType());
					if (icon != null)
					{
						int marge = 5;
						if (this.tailleCase < 40) { marge = 2; }
						int imgX = x + marge;
						int imgY = y + marge;
						icon.paintIcon(this, g, imgX, imgY);
					}
					else
					{
						g.setColor(Color.WHITE);
						int taillePolice = Math.max(8, this.tailleCase / 5);
						g.setFont(new Font("Arial", Font.BOLD, taillePolice));
						String txt = s.getType().substring(0, Math.min(s.getType().length(), 4));
						g.drawString(txt, x + 4, y + this.tailleCase/2);
					}

					/* Indicateur si le sommet est visité */
					if (s.isVisite())
					{
						g.setColor(new Color(255, 255, 100, 150));
						g.fillOval(x + this.tailleCase - 12, y + 2, 10, 10);
					}
				}

				/* Point bleu pour les cases vides */
				if (s != null && s.getType() != null && s.getType().equals("VIDE"))
				{
					g.setColor(new Color(110, 185, 255));
					int taillePoint = Math.max(4, this.tailleCase / 10);
					g.fillOval(x + this.tailleCase/2 - taillePoint/2, y + this.tailleCase/2 - taillePoint/2, taillePoint, taillePoint);
				}
			}
		}

		/* 2. DESSINER LES ARÊTES DU GRAPHE (lignes de visibilité) */
		List<Arete> aretes = graphe.getAretes();
		if (aretes != null)
		{
			for (Arete a : aretes)
			{
				Sommet dep = a.getDepart();
				Sommet arr = a.getArrivee();

				if (dep != null && arr != null)
				{
					int x1 = dep.getColonne() * this.tailleCase + this.tailleCase/2;
					int y1 = dep.getLigne()   * this.tailleCase + this.tailleCase/2;
					int x2 = arr.getColonne() * this.tailleCase + this.tailleCase/2;
					int y2 = arr.getLigne()   * this.tailleCase + this.tailleCase/2;

					g2.setColor(new Color(255, 255, 255, 30));
					g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					g2.drawLine(x1, y1, x2, y2);
				}
			}
		}

		/* 3. DESSINER LES LIAISONS TRACÉES PAR LE JOUEUR */
		if (ctrl != null && ctrl.getJeu() != null)
		{
			List<Arete> lignesTracees = ctrl.getJeu().getLignesTracees();
			if (lignesTracees != null && !lignesTracees.isEmpty())
			{
				g2.setColor(new Color(255, 80, 80, 220));
				g2.setStroke(new BasicStroke(Math.max(2.5f, this.tailleCase/20.0f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				for (Arete a : lignesTracees)
				{
					Sommet dep = a.getDepart();
					Sommet arr = a.getArrivee();

					if (dep != null && arr != null)
					{
						int x1 = dep.getColonne() * this.tailleCase + this.tailleCase/2;
						int y1 = dep.getLigne()   * this.tailleCase + this.tailleCase/2;
						int x2 = arr.getColonne() * this.tailleCase + this.tailleCase/2;
						int y2 = arr.getLigne()   * this.tailleCase + this.tailleCase/2;

						g2.drawLine(x1, y1, x2, y2);
					}
				}

				/* Dessiner un petit cercle sur la position actuelle */
				if (!lignesTracees.isEmpty())
				{
					Arete derniere = lignesTracees.get(lignesTracees.size() - 1);
					Sommet pos = derniere.getArrivee();
					if (pos != null)
					{
						int cx = pos.getColonne() * this.tailleCase + this.tailleCase/2;
						int cy = pos.getLigne()   * this.tailleCase + this.tailleCase/2;
						int rayon = Math.max(4, this.tailleCase / 10);

						g2.setColor(new Color(255, 255, 100, 200));
						g2.fillOval(cx - rayon, cy - rayon, rayon * 2, rayon * 2);
					}
				}
			}
		}
	}

	/*----------------------------*/
	/* Mise à jour                */
	/*----------------------------*/
	public void mettreAJourAffichage()
	{
		this.repaint();
	}

	public void setHovered(int lig, int col)
	{
		this.hoveredLig = lig;
		this.hoveredCol = col;
	}

	/*----------------------------*/
	/* Classe interne GereSouris  */
	/*----------------------------*/
	private class GereSouris extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			int col = e.getX() / tailleCase;
			int lig = e.getY() / tailleCase;

			if (lig >= 0 && lig < graphe.getNbLignes() && col >= 0 && col < graphe.getNbColonnes())
			{
				if (listener != null)
				{
					listener.CelluleCliquee(lig, col);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			int col = e.getX() / tailleCase;
			int lig = e.getY() / tailleCase;

			if (lig >= 0 && lig < graphe.getNbLignes() && col >= 0 && col < graphe.getNbColonnes())
			{
				setHovered(lig, col);
			}
			else
			{
				setHovered(-1, -1);
			}

			if (listener != null)
			{
				listener.CelluleSurvolee(lig, col);
			}

			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			setHovered(-1, -1);
			repaint();
		}
	}
}