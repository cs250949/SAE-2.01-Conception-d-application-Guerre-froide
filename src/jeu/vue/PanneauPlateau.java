package jeu.vue;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

public class PanneauPlateau extends JPanel
{
	private Graphe                     graphe;
	private CelluleListener            listener;
	
	private JButton[][]                boutonsCases;
	private int                        tailleCase = 50;
	private HashMap<String, ImageIcon> cacheImages;

	public interface CelluleListener 
	{
		void CelluleCliquee(int ligne, int colonne);
		void CelluleSurvolee(int ligne, int colonne);
	}

	public PanneauPlateau(Graphe graphe, CelluleListener listener)
	{
		this.graphe = graphe;
		this.listener = listener;
		this.cacheImages = new HashMap<String, ImageIcon>();
		
		
		chargerImagesInfrastructures();

		int nbLignes = Graphe.NB_LIGNES;
		int nbCols   = Graphe.NB_COLONNES;
		if (this.graphe != null)
		{
			nbLignes = this.graphe.getNbLignes();
			nbCols   = this.graphe.getNbColonnes();
		}

		this.setLayout(new GridLayout(nbLignes, nbCols, 1, 1));
		this.setBackground(new Color(33, 37, 43)); 

		this.boutonsCases = new JButton[nbLignes][nbCols];

		for (int l = 0; l < nbLignes; l++)
		{
			for (int c = 0; c < nbCols; c++)
			{
				JButton btn = new JButton();
				btn.setPreferredSize(new Dimension(tailleCase, tailleCase));
				btn.setFocusPainted(false);
				btn.setBorder(BorderFactory.createLineBorder(new Color(90, 95, 105, 60), 1));
				
				final int lig = l;
				final int col = c;

				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (PanneauPlateau.this.listener != null) {
							PanneauPlateau.this.listener.CelluleCliquee(lig, col);
						}
					}
				});

				this.boutonsCases[l][c] = btn;
				this.add(btn);
			}
		}

		this.mettreAJourAffichage();
	}

	private void chargerImagesInfrastructures()
	{
		// Liste de tes fichiers d'icônes 
		String[] types = {"HOPITAL", "PORT", "USINE", "CHAR", "FER", "PET", "TAN", "BAS"};
		int nbTypes = types.length;
		
		for (int i = 0; i < nbTypes; i++)
		{
			String type = types[i];
			try {
				// Tente de charger depuis le dossier racine ou les ressources
				java.net.URL imgUrl = getClass().getResource("/" + type + ".png");
				if (imgUrl == null) {
					imgUrl = getClass().getResource("/images/" + type + ".png");
				}
				
				if (imgUrl != null) {
					ImageIcon iconOriginal = new ImageIcon(imgUrl);
					Image imgRedimensionnee = iconOriginal.getImage().getScaledInstance(35, 35, 4);
					cacheImages.put(type, new ImageIcon(imgRedimensionnee));
				}
			} catch (Exception e) {
				// Évite de faire planter l'application si une image manque
			}
		}
	}

	protected void drawLine(int lineIndex, Graphics g, int x, int y)
	{
		if (this.graphe == null || this.graphe.getAretes() == null) return;
		
		if (lineIndex >= 0 && lineIndex < this.graphe.getAretes().size())
		{
			Arete a = this.graphe.getAretes().get(lineIndex);
			Sommet s1 = a.getDepart();
			Sommet s2 = a.getArrivee();
			
			if (s1 != null && s2 != null)
			{
				int x1 = s1.getColonne() * tailleCase + (tailleCase / 2);
				int y1 = s1.getLigne()   * tailleCase + (tailleCase / 2);
				int x2 = s2.getColonne() * tailleCase + (tailleCase / 2);
				int y2 = s2.getLigne()   * tailleCase + (tailleCase / 2);
				
				Graphics2D g2 = (Graphics2D) g;
				
				if (a.isEmprunteeParJoueur()) 
				{
					g2.setColor(new Color(255, 75, 75, 240));
					g2.setStroke(new BasicStroke(3.0f));       
				}
				else
				{
					g2.setColor(new Color(255, 255, 255, 120)); 
					g2.setStroke(new BasicStroke(1.8f));
				}
				
				g2.drawLine(x1, y1, x2, y2);
			}
		}
	}

	protected void paintChildren(Graphics g)
	{
		// Dessine d'abord les boutons d'infrastructure
		super.paintChildren(g);
		
		// Force le dessin des lignes du graphe au premier plan par-dessus les boutons
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (this.graphe != null && this.graphe.getAretes() != null)
		{
			int nbAretes = this.graphe.getAretes().size();
			for (int i = 0; i < nbAretes; i++)
			{
				this.drawLine(i, g2, 0, 0);
			}
		}
	}

	public void mettreAJourAffichage()
	{
		if (this.graphe == null) return;

		int lignes = this.graphe.getNbLignes();
		int cols   = this.graphe.getNbColonnes();

		for (int l = 0; l < lignes; l++)
		{
			for (int c = 0; c < cols; c++)
			{
				JButton btn = this.boutonsCases[l][c];
				Sommet s = this.graphe.getSommet(l, c);

				if (s == null)
				{
					btn.setBackground(new Color(40, 44, 52));
					btn.setIcon(null);
					btn.setText("");
				}
				else
				{
					// Gestion de la couleur de fond des factions
					if (s.getZone() != null) { 
						if (s.getZone().contains("OUEST")) btn.setBackground(new Color(45, 60, 85)); 
						else if (s.getZone().contains("EST")) btn.setBackground(new Color(90, 50, 50)); 
						else if (s.getZone().contains("CHINOIS")) btn.setBackground(new Color(50, 80, 55)); 
						else btn.setBackground(new Color(65, 65, 50)); 
					} else {
						btn.setBackground(new Color(50, 56, 66));
					}

					// Gestion du contenu 
					if (s.getType() != null && !s.getType().equals("VIDE"))
					{
						ImageIcon icon = cacheImages.get(s.getType());
						if (icon != null) {
							btn.setIcon(icon);
							btn.setText(""); 
						} else {
							// Si l'image ne charge pas, on écrit le texte en gros pour voir la case
							btn.setIcon(null);
							String txt = s.getType().substring(0, Math.min(s.getType().length(), 4));
							btn.setText(txt.toUpperCase());
							btn.setForeground(Color.WHITE);
							btn.setFont(new Font("Arial", Font.BOLD, 10));
						}
					}
					else
					{
						// Nœud de passage vide
						btn.setIcon(null);
						btn.setText("•");
						btn.setForeground(new Color(110, 185, 255));
						btn.setFont(new Font("Arial", Font.BOLD, 16));
					}
				}
			}
		}
		
		this.validate();
		this.repaint();
	}

	public void setSurvol(int ligne, int colonne) {}
}
