package conception.vue;

import commun.Graphe;
import commun.Sommet;
import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;

public class PanneauGrille extends JPanel
{
	/*----------------------------*/
	/* Interface Listener         */
	/*----------------------------*/
	public interface CelluleListener
	{
		void CelluleCliquee(int ligne, int colonne);
		void CelluleSurvolee(int ligne, int colonne);
	}

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private ControleurConception controleur;
	private CelluleListener      listener;
	private Graphe               graphe;
	private int                  tailleCase;
	private JButton[][]          boutons;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public PanneauGrille(ControleurConception controleur, CelluleListener listener)
	{
		this.controleur = controleur;
		this.listener   = listener;
		this.tailleCase = 60;
		setBackground(new Color(35, 35, 45));
	}

	/*----------------------------*/
	/* Modificateurs              */
	/*----------------------------*/
	public void setTailleCase(int tailleCase)
	{
		this.tailleCase = tailleCase;
		if (this.boutons != null)
		{
			mettreAJourTailleBoutons();
		}
	}

	public void setGraphe(Graphe nouveauGraphe)
	{
		this.graphe = nouveauGraphe;
		if (this.graphe == null) return;

		int nbLignes = this.graphe.getNbLignes();
		int nbCols   = this.graphe.getNbColonnes();

		this.removeAll();
		this.setLayout(new GridLayout(nbLignes, nbCols, 2, 2));
		this.boutons = new JButton[nbLignes][nbCols];

		int taillePolice = Math.max(8, this.tailleCase / 5);

		for (int i = 0; i < nbLignes; i++)
		{
			for (int j = 0; j < nbCols; j++)
			{
				final int lig = i;
				final int col = j;

				JButton btn = new JButton();
				btn.setPreferredSize(new Dimension(this.tailleCase, this.tailleCase));
				btn.setMinimumSize(new Dimension(this.tailleCase, this.tailleCase));
				btn.setBackground(new Color(48, 52, 63));
				btn.setFocusPainted(false);
				btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 75), 1));
				btn.setFont(new Font("Arial", Font.BOLD, taillePolice));
				btn.setMargin(new Insets(0, 0, 0, 0));

				btn.addActionListener(e -> {
					if (listener != null) listener.CelluleCliquee(lig, col);
				});

				btn.addMouseListener(new java.awt.event.MouseAdapter()
				{
					public void mouseEntered(java.awt.event.MouseEvent e)
					{
						if (listener != null) listener.CelluleSurvolee(lig, col);
					}
				});

				this.boutons[i][j] = btn;
				this.add(btn);
			}
		}

		mettreAJourIcons();
		this.revalidate();
		this.repaint();
	}

	/*----------------------------*/
	/* Mise à jour des icônes     */
	/*----------------------------*/
	public void mettreAJourIcons()
	{
		if (graphe == null || boutons == null) return;

		int ligs = graphe.getNbLignes();
		int cols = graphe.getNbColonnes();

		for (int i = 0; i < ligs; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				Sommet s = graphe.getSommet(i, j);
				if (s != null && boutons[i][j] != null)
				{
					String zone = s.getZone();
					if (zone != null)
					{
						if (zone.contains("ROUGE"))       { boutons[i][j].setBackground(new Color(90, 40, 40));  }
						else if (zone.contains("BLEU"))   { boutons[i][j].setBackground(new Color(40, 50, 90));  }
						else if (zone.contains("OUEST"))  { boutons[i][j].setBackground(new Color(45, 60, 85));  }
						else if (zone.contains("EST"))    { boutons[i][j].setBackground(new Color(90, 50, 50));  }
						else if (zone.contains("CHINOIS")){ boutons[i][j].setBackground(new Color(50, 80, 55));  }
						else if (zone.contains("NON"))    { boutons[i][j].setBackground(new Color(65, 65, 50));  }
						else                              { boutons[i][j].setBackground(new Color(48, 52, 63));  }
					}

					if (!s.getType().equals("VIDE"))
					{
						String txt = s.getType().substring(0, Math.min(s.getType().length(), 4)).toUpperCase();
						boutons[i][j].setText(txt);
						boutons[i][j].setForeground(Color.WHITE);

						if (s.getType().equals("BASE_DEPART"))
						{
							boutons[i][j].setBackground(new Color(100, 70, 30));
							boutons[i][j].setForeground(new Color(255, 200, 100));
						}
					}
					else
					{
						boutons[i][j].setText("·");
						boutons[i][j].setForeground(new Color(110, 185, 255));
					}
				}
			}
		}
	}

	/*----------------------------*/
	/* Mise à jour taille boutons */
	/*----------------------------*/
	private void mettreAJourTailleBoutons()
	{
		if (this.boutons == null) return;

		int taillePolice = Math.max(8, this.tailleCase / 5);

		for (int i = 0; i < this.boutons.length; i++)
		{
			for (int j = 0; j < this.boutons[i].length; j++)
			{
				if (this.boutons[i][j] != null)
				{
					this.boutons[i][j].setPreferredSize(new Dimension(this.tailleCase, this.tailleCase));
					this.boutons[i][j].setMinimumSize(new Dimension(this.tailleCase, this.tailleCase));
					this.boutons[i][j].setFont(new Font("Arial", Font.BOLD, taillePolice));
				}
			}
		}
		this.revalidate();
		this.repaint();
	}

	/*----------------------------*/
	/* Accesseur                   */
	/*----------------------------*/
	public int getTailleCase()
	{
		return this.tailleCase;
	}
}