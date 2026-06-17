package jeu.vue;

import jeu.controleur.ControleurJeu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

public class FenetreJeu extends JFrame
{
	/*----------------------------*/
	/* Constantes                 */
	/*----------------------------*/
	private static final Color COULEUR_FOND      = new Color(33, 37, 43);
	private static final Color COULEUR_PANNEAU   = new Color(40, 44, 52);
	private static final Color COULEUR_BOUTON    = new Color(45, 95, 135);
	private static final Color COULEUR_BOUTON2   = new Color(55, 60, 72);
	private static final Color COULEUR_QUITTER   = new Color(145, 55, 55);

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private ControleurJeu              ctrl;
	private PanneauPlateau             panneauPlateau;
	private PanneauInfo                panneauInfo;
	private JPanel                     panneauControles;

	private JLabel                     lblImageCarte;
	private HashMap<String, ImageIcon> cacheCartesInfrastructures;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public FenetreJeu(ControleurJeu ctrl)
	{
		this.ctrl = ctrl;
		this.cacheCartesInfrastructures = new HashMap<String, ImageIcon>();

		chargerImagesCartesInfrastructures();

		this.panneauPlateau = ctrl.getPanneauPlateau();
		this.panneauInfo    = ctrl.getPanneauInfo();

		this.setTitle("Opération Réseau Rouge");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension taille = ctrl.getSizeFenetre();
		this.setMinimumSize(new Dimension(taille.width, taille.height));
		this.setPreferredSize(new Dimension(taille.width, taille.height));
		this.setLocationRelativeTo(null);

		this.setLayout(new BorderLayout(5, 5));
		this.getContentPane().setBackground(COULEUR_FOND);

		this.construireBarreControles();
		this.construireBarreMenu();

		JScrollPane scrollPlateau = new JScrollPane(this.panneauPlateau);
		scrollPlateau.setBorder(BorderFactory.createEmptyBorder());
		scrollPlateau.setBackground(COULEUR_FOND);
		scrollPlateau.getViewport().setBackground(COULEUR_FOND);

		this.add(scrollPlateau, BorderLayout.CENTER);

		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new BoxLayout(panneauDroite, BoxLayout.Y_AXIS));
		panneauDroite.setBackground(COULEUR_FOND);

		if (this.panneauControles != null)
		{
			panneauDroite.add(this.panneauControles);
		}

		if (this.panneauInfo != null)
		{
			panneauDroite.add(Box.createVerticalStrut(10));
			panneauDroite.add(this.panneauInfo);
		}

		JScrollPane scrollDroite = new JScrollPane(panneauDroite);
		scrollDroite.setPreferredSize(new Dimension(280, taille.height));
		scrollDroite.setBorder(BorderFactory.createEmptyBorder());
		scrollDroite.setBackground(COULEUR_FOND);
		scrollDroite.getViewport().setBackground(COULEUR_FOND);

		this.add(scrollDroite, BorderLayout.EAST);

		this.pack();
		this.setVisible(true);
	}

	/*----------------------------*/
	/* Chargement des images      */
	/*----------------------------*/
	private void chargerImagesCartesInfrastructures()
	{
		String[][] correspondance = {
			{"HOPITAL",    "hopital.png"},
			{"PORT",       "port.png"},
			{"USINE",      "usine.png"},
			{"CHAR",       "char.png"},
			{"FER",        "ferme.png"},
			{"FERME",      "ferme.png"},
			{"PET",        "petrolier.png"},
			{"PETROLIER",  "petrolier.png"},
			{"TAN",        "tank.png"},
			{"TANK",       "tank.png"},
			{"BAS",        "base.png"},
			{"BASE_DEPART","base.png"},
			{"VIDE",       "vide.png"},
			{"JOKER",      "joker.png"}
		};

		for (int i = 0; i < correspondance.length; i++)
		{
			String type   = correspondance[i][0];
			String fichier = correspondance[i][1];

			try
			{
				java.net.URL imgUrl = getClass().getResource("/images/" + fichier);
				if (imgUrl == null)
				{
					imgUrl = getClass().getResource("/" + fichier);
				}

				if (imgUrl != null)
				{
					ImageIcon iconOriginal = new ImageIcon(imgUrl);
					Image imgRedim = iconOriginal.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
					cacheCartesInfrastructures.put(type, new ImageIcon(imgRedim));
				}
			}
			catch (Exception e)
			{
				System.out.println("Erreur chargement image : " + fichier);
			}
		}
	}

	/*----------------------------*/
	/* Barre de menu              */
	/*----------------------------*/
	private void construireBarreMenu()
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu menuPartie = new JMenu("Partie");

		JMenuItem miNouvelle = new JMenuItem("Nouvelle partie");
		miNouvelle.addActionListener(e -> {
			this.dispose();
		});

		JMenuItem miQuitter = new JMenuItem("Quitter");
		miQuitter.addActionListener(e -> ctrl.quitter());

		menuPartie.add(miNouvelle);
		menuPartie.addSeparator();
		menuPartie.add(miQuitter);

		JMenu menuAide = new JMenu("Aide");
		JMenuItem miRegles = new JMenuItem("Règles du jeu");
		miRegles.addActionListener(e -> afficherRegles());

		menuAide.add(miRegles);

		menuBar.add(menuPartie);
		menuBar.add(menuAide);

		this.setJMenuBar(menuBar);
	}

	/*----------------------------*/
	/* Panneau de contrôles       */
	/*----------------------------*/
	private void construireBarreControles()
	{
		this.panneauControles = new JPanel();
		this.panneauControles.setPreferredSize(new Dimension(260, 300));
		this.panneauControles.setMaximumSize(new Dimension(260, 350));
		this.panneauControles.setBackground(COULEUR_PANNEAU);
		this.panneauControles.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		this.panneauControles.setLayout(new BoxLayout(this.panneauControles, BoxLayout.Y_AXIS));

		JLabel lblTitre = new JLabel("CONTRÔLES");
		lblTitre.setFont(new Font("Arial", Font.BOLD, 14));
		lblTitre.setForeground(Color.WHITE);
		lblTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.panneauControles.add(lblTitre);

		this.panneauControles.add(Box.createVerticalStrut(15));

		/* Bouton Piocher */
		JButton btnPiocher = new JButton("Piocher une Carte");
		btnPiocher.setMaximumSize(new Dimension(230, 40));
		btnPiocher.setBackground(COULEUR_BOUTON);
		btnPiocher.setForeground(Color.WHITE);
		btnPiocher.setFocusPainted(false);
		btnPiocher.setFont(new Font("Arial", Font.BOLD, 12));
		btnPiocher.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPiocher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnPiocher.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ctrl.piocherCarte();

				String carteActive = ctrl.getJeu().getCarteActive();
				if (carteActive != null && !carteActive.isEmpty())
				{
					ImageIcon iconCarte = cacheCartesInfrastructures.get(carteActive);
					if (iconCarte != null)
					{
						lblImageCarte.setIcon(iconCarte);
						lblImageCarte.setText("");
					}
					else
					{
						lblImageCarte.setIcon(null);
						lblImageCarte.setText("[" + carteActive + "]");
						lblImageCarte.setForeground(Color.WHITE);
					}
				}

				FenetreJeu.this.revalidate();
				FenetreJeu.this.repaint();
			}
		});

		this.panneauControles.add(btnPiocher);
		this.panneauControles.add(Box.createVerticalStrut(10));

		/* Bouton Passer le Tour */
		JButton btnPasserTour = new JButton("Passer le Tour");
		btnPasserTour.setMaximumSize(new Dimension(230, 40));
		btnPasserTour.setBackground(COULEUR_BOUTON2);
		btnPasserTour.setForeground(Color.WHITE);
		btnPasserTour.setFocusPainted(false);
		btnPasserTour.setFont(new Font("Arial", Font.BOLD, 12));
		btnPasserTour.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPasserTour.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnPasserTour.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ctrl.passerTour();

				lblImageCarte.setIcon(null);
				lblImageCarte.setText("Tour passé");
				lblImageCarte.setForeground(Color.LIGHT_GRAY);

				FenetreJeu.this.revalidate();
				FenetreJeu.this.repaint();
			}
		});

		this.panneauControles.add(btnPasserTour);
		this.panneauControles.add(Box.createVerticalStrut(20));

		/* Zone d'affichage de la carte */
		this.lblImageCarte = new JLabel("Pas de carte", SwingConstants.CENTER);
		this.lblImageCarte.setFont(new Font("Arial", Font.ITALIC, 11));
		this.lblImageCarte.setForeground(Color.LIGHT_GRAY);
		this.lblImageCarte.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.lblImageCarte.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblImageCarte.setVerticalAlignment(SwingConstants.CENTER);
		this.lblImageCarte.setPreferredSize(new Dimension(150, 150));
		this.lblImageCarte.setMaximumSize(new Dimension(150, 150));
		this.lblImageCarte.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1));
		this.panneauControles.add(this.lblImageCarte);

		this.panneauControles.add(Box.createVerticalGlue());

		/* Bouton Quitter */
		JButton btnQuitter = new JButton("Quitter");
		btnQuitter.setMaximumSize(new Dimension(230, 40));
		btnQuitter.setBackground(COULEUR_QUITTER);
		btnQuitter.setForeground(Color.WHITE);
		btnQuitter.setFocusPainted(false);
		btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
		btnQuitter.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnQuitter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnQuitter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ctrl.quitter();
			}
		});

		this.panneauControles.add(btnQuitter);
	}

	/*----------------------------*/
	/* Affichage des règles       */
	/*----------------------------*/
	private void afficherRegles()
	{
		JOptionPane.showMessageDialog(
			this,
			"OPÉRATION RÉSEAU ROUGE - RÈGLES\n\n" +
			"1. Chaque joueur contrôle un réseau d'agents.\n" +
			"2. Ancrez-vous d'abord sur une BASE_DEPART.\n" +
			"3. Piochez une carte Infrastructure.\n" +
			"4. Cliquez sur une case adjacente correspondant à la carte piochée.\n" +
			"5. Les tracés ne peuvent pas se croiser.\n" +
			"6. Marquez des points en visitant des infrastructures et des zones.\n" +
			"7. La partie se joue en 4 manches.",
			"Règles du jeu",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	/*----------------------------*/
	/* Rafraîchir la fenêtre      */
	/*----------------------------*/
	public void rafraichirFenetre()
	{
		if (this.panneauPlateau != null)
		{
			this.panneauPlateau.mettreAJourAffichage();
		}

		if (this.panneauInfo != null)
		{
			this.panneauInfo.rafraichir();
		}
	}
}