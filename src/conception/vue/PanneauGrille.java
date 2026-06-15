package conception.vue;

import commun.Graphe;
import commun.Sommet;
import conception.controleur.ControleurConception;
import java.awt.*;
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

	public PanneauGrille(ControleurConception controleur, CelluleListener listener)
	{
		this.controleur = controleur;
		this.listener   = listener;
		setBackground(new Color(35, 35, 45));
	}

	public void setGraphe(Graphe nouveauGraphe)
	{
		this.graphe = nouveauGraphe;
		if (this.graphe == null) return;

		int nbLignes = this.graphe.getNbLignes();
		int nbCols   = this.graphe.getNbColonnes();

		// Nettoyage de l'ancienne grille de boutons
		this.removeAll();
		this.setLayout(new GridLayout(nbLignes, nbCols, 2, 2));
		this.boutons = new JButton[nbLignes][nbCols];

		// Génération de la nouvelle matrice de boutons
		for (int i = 0; i < nbLignes; i++)
		{
			for (int j = 0; j < nbCols; j++)
			{
				final int lig = i;
				final int col = j;

				JButton btn = new JButton();
				btn.setBackground(new Color(48, 52, 63));
				btn.setFocusPainted(false);
				btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 75), 1));

				btn.addActionListener(e -> {
					if (listener != null) listener.CelluleCliquee(lig, col);
				});

				btn.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseEntered(java.awt.event.MouseEvent e) {
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
					if (!s.getType().equals("VIDE"))
					{
						String txt = s.getType().substring(0, Math.min(s.getType().length(), 4)).toUpperCase();
						boutons[i][j].setText(txt);
						boutons[i][j].setBackground(Color.WHITE);
						boutons[i][j].setForeground(Color.BLACK);
					}
					else
					{
						boutons[i][j].setText("");
						boutons[i][j].setBackground(new Color(48, 52, 63));
					}
				}
			}
		}
	}
}
