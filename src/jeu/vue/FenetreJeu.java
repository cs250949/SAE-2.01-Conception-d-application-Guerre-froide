package jeu.vue;

import commun.Graphe;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

public class FenetreJeu extends JFrame
{
	private Graphe                     graphe;
	private PanneauPlateau             panneauPlateau;
	private JPanel                     panneauControles;
	
	private JLabel                     lblImageCarte;
	private HashMap<String, ImageIcon> cacheCartesInfrastructures;
	
	private int                        largeurFenetre = 680;
	private int                        hauteurFenetre = 450;

	public FenetreJeu(Graphe graphe)
	{
		this.graphe = graphe;
		this.cacheCartesInfrastructures = new HashMap<String, ImageIcon>();
		
		chargerImagesCartesInfrastructures();
		
		this.setTitle("Opération Réseau Rouge");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(largeurFenetre, hauteurFenetre));
		this.setLocationRelativeTo(null); 
		
		this.setLayout(new BorderLayout(5, 5));
		this.getContentPane().setBackground(new Color(33, 37, 43));

		this.panneauPlateau = new PanneauPlateau(this.graphe, new PanneauPlateau.CelluleListener() {
			public void CelluleCliquee(int ligne, int colonne) {
				System.out.println("Clic en jeu : [" + ligne + ", " + colonne + "]");
			}
			public void CelluleSurvolee(int ligne, int colonne) {}
		});

		this.construireBarreControles();

		this.add(this.panneauPlateau, BorderLayout.CENTER);
		this.add(this.panneauControles, BorderLayout.EAST);

		this.pack();
		this.setVisible(true);
	}

	private void chargerImagesCartesInfrastructures()
	{
		// Association directe des types de l'éditeur vers le dossiers images 
		String[][] correspondance = {
			{"HOPITAL", "hopital.png"},
			{"PORT",    "port.png"},
			{"USINE",   "tank.png"},     
			{"CHAR",    "tank.png"},
			{"FER",     "ferme.png"},
			{"PET",     "petrolier.png"},
			{"TAN",     "tank.png"},
			{"BAS",     "joker.png"}
		};
		
		int nb = correspondance.length;
		for (int i = 0; i < nb; i++)
		{
			String type = correspondance[i][0];
			String fichier = correspondance[i][1];
			try {
				java.net.URL imgUrl = getClass().getResource("/images/" + fichier);
				if (imgUrl == null) {
					imgUrl = getClass().getResource("/" + fichier);
				}
				
				if (imgUrl != null) {
					ImageIcon iconOriginal = new ImageIcon(imgUrl);
					
					Image imgRedim = iconOriginal.getImage().getScaledInstance(140, 140, 4);
					cacheCartesInfrastructures.put(type, new ImageIcon(imgRedim));
				}
			} catch (Exception e) {
				System.out.println("Erreur image : " + fichier);
			}
		}
	}

	private void construireBarreControles()
	{
		this.panneauControles = new JPanel();
		this.panneauControles.setPreferredSize(new Dimension(220, hauteurFenetre));
		this.panneauControles.setBackground(new Color(40, 44, 52));
		this.panneauControles.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		this.panneauControles.setLayout(new BoxLayout(this.panneauControles, BoxLayout.Y_AXIS));

		JLabel lblTitre = new JLabel("CONTROLES");
		lblTitre.setFont(new Font("Arial", Font.BOLD, 14));
		lblTitre.setForeground(Color.WHITE);
		lblTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.panneauControles.add(lblTitre);
		
		this.panneauControles.add(Box.createVerticalStrut(15));

		// --- BOUTON PIOCHER ---
		JButton btnPiocher = new JButton("Piocher une Carte");
		btnPiocher.setMaximumSize(new Dimension(200, 35));
		btnPiocher.setBackground(new Color(45, 95, 135));
		btnPiocher.setForeground(Color.WHITE);
		btnPiocher.setFocusPainted(false);
		btnPiocher.setFont(new Font("Arial", Font.BOLD, 12));
		btnPiocher.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		btnPiocher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] typesSommets = {"HOPITAL", "PORT", "CHAR", "FER", "PET", "TAN", "BAS"};
				int indexAleatoire = (int)(Math.random() * typesSommets.length);
				String carteSommetPiochee = typesSommets[indexAleatoire];
				
				ImageIcon iconCarte = cacheCartesInfrastructures.get(carteSommetPiochee);
				if (iconCarte != null) {
					lblImageCarte.setIcon(iconCarte);
					lblImageCarte.setText(""); 
				} else {
					lblImageCarte.setIcon(null);
					lblImageCarte.setText("[" + carteSommetPiochee + "]"); 
					lblImageCarte.setForeground(Color.WHITE);
				}
				
				FenetreJeu.this.validate();
				FenetreJeu.this.repaint();
			}
		});
		this.panneauControles.add(btnPiocher);

		this.panneauControles.add(Box.createVerticalStrut(10));

		// --- BOUTON PASSER LE TOUR ---
		JButton btnPasserTour = new JButton("Passer le Tour");
		btnPasserTour.setMaximumSize(new Dimension(200, 35));
		btnPasserTour.setBackground(new Color(55, 60, 72));
		btnPasserTour.setForeground(Color.WHITE);
		btnPasserTour.setFocusPainted(false);
		btnPasserTour.setFont(new Font("Arial", Font.BOLD, 12));
		btnPasserTour.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.panneauControles.add(btnPasserTour);

		this.panneauControles.add(Box.createVerticalStrut(20));

		// --- ZONE D'AFFICHAGE DES IMAGES ---
		this.lblImageCarte = new JLabel("Pas de carte");
		this.lblImageCarte.setFont(new Font("Arial", Font.ITALIC, 11));
		this.lblImageCarte.setForeground(Color.LIGHT_GRAY);
		this.lblImageCarte.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.lblImageCarte.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblImageCarte.setPreferredSize(new Dimension(150, 150));
		this.lblImageCarte.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1));
		this.panneauControles.add(this.lblImageCarte);

		this.panneauControles.add(Box.createVerticalGlue());

		// --- BOUTON QUITTER ---
		JButton btnQuitter = new JButton("Quitter");
		btnQuitter.setMaximumSize(new Dimension(200, 35));
		btnQuitter.setBackground(new Color(145, 55, 55));
		btnQuitter.setForeground(Color.WHITE);
		btnQuitter.setFocusPainted(false);
		btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
		btnQuitter.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		btnQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FenetreJeu.this.dispose();
			}
		});
		this.panneauControles.add(btnQuitter);
	}

	public void rafraichirFenetre()
	{
		if (this.panneauPlateau != null)
			this.panneauPlateau.mettreAJourAffichage();
	}
}