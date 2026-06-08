package conception.controleur;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ControleurConception
{
	private static final String   CHEMIN_PLATEAU    = "data/plateau.txt";
	private static final String[] TYPES_ZONES       = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK"};
	private static final String[] NOMS_JOUEURS      = {"Agent Rouge", "Agent Bleu", "Agent Vert", "Agent Jaune"};
	private static final int      NB_JOUEURS_MIN    = 2;
	private static final int      NB_JOUEURS_MAX    = 4;

	private JFrame         frame;
	private JTextField     champLargeur;
	private JTextField     champHauteur;
	private JTextField     champNbLignes;
	private JTextField     champNbColonnes;
	private JSpinner       NbJoueurs;
	private JLabel         labelErreur;
	private Graphe         plateau;
	private PanneauEdition panneauEdition;
	private int            prochainId;
	private String         zoneCourante;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public ControleurConception()
	{
		this.plateau      = new Graphe();
		this.prochainId   = 0;
		this.zoneCourante = "HOPITAL";
		this.construireInterface();
	}

	
	private void construireInterface()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/ 
		this.frame = new JFrame("Opération Réseau Rouge - Configuration");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());

		JPanel panneauPrincipal = new JPanel(new BorderLayout(10, 10));
		panneauPrincipal.setBackground(new Color(30, 30, 40));
		panneauPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

		JPanel panneauFormulaire   = this.creerFormulaire();
		JPanel panneauEditionPanel = this.creerPanneauEdition();
		JPanel panneauBoutons      = this.creerBarreBoutons();

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panneauPrincipal.add(panneauFormulaire, BorderLayout.NORTH);
		panneauPrincipal.add(panneauEditionPanel, BorderLayout.CENTER);
		panneauPrincipal.add(panneauBoutons, BorderLayout.SOUTH);

		/*-------------------------------*/
		/* Activation des composants     */
		/*-------------------------------*/
		this.frame.add(panneauPrincipal);
		this.frame.setSize(1100, 750);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}

	private JPanel creerFormulaire()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/

		JPanel panneau = new JPanel(new GridLayout(6, 2, 10, 10));
		panneau.setBackground(new Color(30, 30, 40));
		panneau.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel labelLargeur = new JLabel("Largeur plateau (px):");
		labelLargeur.setForeground(Color.WHITE);
		this.champLargeur = new JTextField("800");

		JLabel labelHauteur = new JLabel("Hauteur plateau (px):");
		labelHauteur.setForeground(Color.WHITE);
		this.champHauteur = new JTextField("600");

		JLabel labelNbLignes = new JLabel("Nombre de lignes:");
		labelNbLignes.setForeground(Color.WHITE);
		this.champNbLignes = new JTextField("4");

		JLabel labelNbColonnes = new JLabel("Nombre de colonnes:");
		labelNbColonnes.setForeground(Color.WHITE);
		this.champNbColonnes = new JTextField("3");

		JLabel labelNbJoueurs = new JLabel("Nombre d'agents (2-4):");
		labelNbJoueurs.setForeground(Color.WHITE);
		this.NbJoueurs = new JSpinner(new SpinnerNumberModel(2, NB_JOUEURS_MIN, NB_JOUEURS_MAX, 1));

		this.labelErreur = new JLabel(" ");
		this.labelErreur.setForeground(new Color(255, 100, 100));

		JButton btnGenerer = new JButton("Générer la grille");
		btnGenerer.setBackground(new Color(60, 100, 60));
		btnGenerer.setForeground(Color.WHITE);
		btnGenerer.addActionListener(e -> this.genererGrille());

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panneau.add(labelLargeur);
		panneau.add(this.champLargeur);
		panneau.add(labelHauteur);
		panneau.add(this.champHauteur);
		panneau.add(labelNbLignes);
		panneau.add(this.champNbLignes);
		panneau.add(labelNbColonnes);
		panneau.add(this.champNbColonnes);
		panneau.add(labelNbJoueurs);
		panneau.add(this.NbJoueurs);
		panneau.add(btnGenerer);
		panneau.add(this.labelErreur);

		return panneau;
	}

	/*-------------------------------*/
	/* Génère une grille automatique */
	/*-------------------------------*/
	private void genererGrille()
	{
		try
		{
			int largeur    = Integer.parseInt(this.champLargeur   .getText().trim());
			int hauteur    = Integer.parseInt(this.champHauteur   .getText().trim());
			int nbLignes   = Integer.parseInt(this.champNbLignes  .getText().trim());
			int nbColonnes = Integer.parseInt(this.champNbColonnes.getText().trim());

			if (largeur <= 0 || hauteur <= 0 || nbLignes <= 0 || nbColonnes <= 0)
			{
				this.labelErreur.setText("Valeurs invalides");
				return;
			}

		
			this.plateau = new Graphe();
			this.prochainId = 0;

			// Calcul des marges et espacements
			int margeGauche = 60;
			int margeHaut   = 60;
			int margeDroite = 60;
			int margeBas    = 60;

			int largeurDispo = largeur - margeGauche - margeDroite;
			int hauteurDispo = hauteur - margeHaut - margeBas;

			int espaceX = largeurDispo / (nbColonnes - 1);
			int espaceY = hauteurDispo / (nbLignes - 1);

			// Création des sommets
			for (int ligne = 0; ligne < nbLignes; ligne++)
			{
				for (int col = 0; col < nbColonnes; col++)
				{
					int x = margeGauche + col * espaceX;
					int y = margeHaut + ligne * espaceY;
					
					// Alterne les types de zones
					int indexType = (ligne * nbColonnes + col) % TYPES_ZONES.length;
					String type = TYPES_ZONES[indexType];
					
					Sommet sommet = new Sommet(this.prochainId, x, y, type);
					this.plateau.ajouterSommet(sommet);
					this.prochainId++;
				}
			}

			// Génère les arêtes automatiquement
			this.genererAretes();

			this.panneauEdition.setPlateau(this.plateau);
			this.labelErreur.setText("Grille " + nbLignes + "x" + nbColonnes + " générée !");
		}
		catch (NumberFormatException e)
		{
			this.labelErreur.setText("Erreur: valeurs non numériques");
		}
	}

	// Génère les arêtes entre sommets proches 
	private void genererAretes()
	{
		java.util.List<Sommet> sommets = this.plateau.getSommets();
		
		for (int i = 0; i < sommets.size(); i++)
		{
			Sommet s1 = sommets.get(i);
			for (int j = i + 1; j < sommets.size(); j++)
			{
				Sommet s2 = sommets.get(j);
				double distance = Math.hypot(s1.getX() - s2.getX(), s1.getY() - s2.getY());
				
				// Si distance < 150 pixels, on connecte
				if (distance < 150)
				{
					this.plateau.ajouterArete(new Arete(s1.getId(), s2.getId()));
				}
			}
		}
	}

	private JPanel creerPanneauEdition()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/

		JPanel panneau = new JPanel(new BorderLayout());
		panneau.setBackground(new Color(40, 40, 50));
		panneau.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel panneauOutils = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panneauOutils.setBackground(new Color(40, 40, 50));

		JLabel labelZone = new JLabel("Type de station:");
		labelZone.setForeground(Color.WHITE);

		JComboBox<String> comboZones = new JComboBox<>(TYPES_ZONES);
		comboZones.setBackground(new Color(60, 60, 70));
		comboZones.setForeground(Color.WHITE);
		comboZones.addActionListener(e -> this.zoneCourante = (String) comboZones.getSelectedItem());

		JButton btnAjouter = new JButton("+ Ajouter station");
		btnAjouter.setBackground(new Color(60, 100, 60));
		btnAjouter.setForeground(Color.WHITE);
		btnAjouter.addActionListener(e -> this.activerModeAjout());

		JButton btnSupprimer = new JButton("- Supprimer station");
		btnSupprimer.setBackground(new Color(150, 60, 60));
		btnSupprimer.setForeground(Color.WHITE);
		btnSupprimer.addActionListener(e -> this.activerModeSuppression());

		JButton btnCharger = new JButton("Charger depuis fichier");
		btnCharger.setBackground(new Color(60, 60, 120));
		btnCharger.setForeground(Color.WHITE);
		btnCharger.addActionListener(e -> this.chargerPlateau());

		JButton btnSauvegarder = new JButton("Sauvegarder");
		btnSauvegarder.setBackground(new Color(100, 80, 40));
		btnSauvegarder.setForeground(Color.WHITE);
		btnSauvegarder.addActionListener(e -> this.sauvegarderPlateau());

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panneauOutils.add(labelZone);
		panneauOutils.add(comboZones);
		panneauOutils.add(btnAjouter);
		panneauOutils.add(btnSupprimer);
		panneauOutils.add(btnCharger);
		panneauOutils.add(btnSauvegarder);

		this.panneauEdition = new PanneauEdition();
		this.panneauEdition.setBackground(new Color(50, 50, 60));

		panneau.add(panneauOutils, BorderLayout.NORTH);
		panneau.add(new JScrollPane(this.panneauEdition), BorderLayout.CENTER);

		return panneau;
	}

	private JPanel creerBarreBoutons()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		JPanel panneau = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panneau.setBackground(new Color(30, 30, 40));

		JButton btnRegles = new JButton("📖 Règles");
		btnRegles.setBackground(new Color(80, 80, 120));
		btnRegles.setForeground(Color.WHITE);
		btnRegles.addActionListener(e -> this.afficherRegles());

		JButton btnAnnuler = new JButton("✖ Annuler");
		btnAnnuler.setBackground(new Color(120, 60, 60));
		btnAnnuler.setForeground(Color.WHITE);
		btnAnnuler.addActionListener(e -> System.exit(0));

		JButton btnReset = new JButton("↺ Réinitialiser");
		btnReset.setBackground(new Color(100, 80, 40));
		btnReset.setForeground(Color.WHITE);
		btnReset.addActionListener(e -> this.reinitialiserChamps());

		JButton btnValider = new JButton("✔ Valider et lancer");
		btnValider.setBackground(new Color(60, 100, 60));
		btnValider.setForeground(Color.WHITE);
		btnValider.addActionListener(e -> this.validerEtLancer());

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/

		panneau.add(btnRegles);
		panneau.add(btnAnnuler);
		panneau.add(btnReset);
		panneau.add(btnValider);

		return panneau;
	}

	/*-------------------------------*/
	/* Actions des boutons            */
	/*-------------------------------*/
	private void activerModeAjout()
	{
		this.panneauEdition.setModeAjout(true);
		this.panneauEdition.setZoneCourante(this.zoneCourante);
		this.labelErreur.setText("Mode AJOUT - Cliquez pour ajouter une station " + this.zoneCourante);
	}

	private void activerModeSuppression()
	{
		this.panneauEdition.setModeAjout(false);
		this.labelErreur.setText("Mode SUPPRESSION - Cliquez droit sur une station pour la supprimer");
	}

	private void chargerPlateau()
	{
		try
		{
			this.plateau.chargerDepuisFichier(CHEMIN_PLATEAU);
			this.mettreAJourProchainId();
			this.panneauEdition.setPlateau(this.plateau);
			this.labelErreur.setText("Plateau chargé depuis " + CHEMIN_PLATEAU);
		}
		catch (IOException e)
		{
			this.labelErreur.setText("Erreur: fichier non trouvé - " + CHEMIN_PLATEAU);
		}
	}

	private void sauvegarderPlateau()
	{
		try
		{
			File dossier = new File("data");
			if (!dossier.exists()) dossier.mkdir();
			PrintWriter pw = new PrintWriter(new FileWriter(CHEMIN_PLATEAU));
			pw.println("# Format: id;x;y;typeZone");
			pw.println("# Types: HOPITAL, FERME, PETROLIER, PORT, TANK");
			for (Sommet s : this.plateau.getSommets())
			{
				pw.println(s.getId() + ";" + s.getX() + ";" + s.getY() + ";" + s.getTypeZone());
			}
			pw.close();
			this.labelErreur.setText("Plateau sauvegardé !");
		}
		catch (IOException e)
		{
			this.labelErreur.setText("Erreur sauvegarde !");
		}
	}

	private void mettreAJourProchainId()
	{
		int maxId = -1;
		for (Sommet s : this.plateau.getSommets())
		{
			if (s.getId() > maxId) maxId = s.getId();
		}
		this.prochainId = maxId + 1;
		this.panneauEdition.setProchainId(this.prochainId);
	}

	private void reinitialiserChamps()
	{
		this.champLargeur   .setText("800");
		this.champHauteur   .setText("600");
		this.champNbLignes  .setText("4");
		this.champNbColonnes.setText("3");
		this.NbJoueurs      .setValue(2);
		this.labelErreur    .setText("Champs réinitialisés");
	}

	/*-------------------------------*/
	/* Affiche les règles du jeu      */
	/*-------------------------------*/

	private void afficherRegles()
	{
		String texte =
			"═══════════════════════════════════════════\n" +
			"       OPÉRATION RÉSEAU ROUGE - RÈGLES\n" +
			"═══════════════════════════════════════════\n\n" +
			"5 TYPES DE STATIONS:\n" +
			"  🏥 HOPITAL (rouge)  |  🚜 FERME (vert)\n" +
			"  🛢️ PETROLIER (noir) |  ⚓ PORT (bleu)\n" +
			"  🎖️ TANK (marron)\n\n" +
			"CARTES:\n" +
			"  - 2 cartes par type de zone (10 cartes)\n" +
			"  - 1 carte JOKER (peut remplacer n'importe quel type)\n" +
			"  - Total: 11 cartes par manche - PIOCHÉES ALÉATOIREMENT\n\n" +
			"RÈGLES:\n" +
			"  - 4 manches\n" +
			"  - Possédez la carte du type pour connecter une station\n" +
			"  - Le JOKER permet de connecter n'importe quelle station\n" +
			"  - Les câbles ne doivent pas se croiser\n" +
			"  - Max 2 connexions par station\n" +
			"  - Réseau linéaire (pas d'embranchement)\n\n" +
			"SCORE = Nb types de zones traversés × max stations dans une zone\n\n" +
			"Fin de partie: après 4 manches, l'agent avec le plus haut score gagne\n";

		JTextArea zone = new JTextArea(texte);
		zone.setEditable(false);
		zone.setFont(new Font("Monospaced", Font.PLAIN, 12));
		zone.setBackground(new Color(40, 40, 50));
		zone.setForeground(new Color(220, 220, 200));
		JOptionPane.showMessageDialog(this.frame, new JScrollPane(zone), "Règles", JOptionPane.INFORMATION_MESSAGE);
	}

	/*---------------------------------------*/
	/* Valide les paramètres et lance le jeu */
	/*---------------------------------------*/
	private void validerEtLancer()
	{
		try
		{
			int largeur   = Integer.parseInt(this.champLargeur.getText().trim());
			int hauteur   = Integer.parseInt(this.champHauteur.getText().trim());
			int nbJoueurs = (Integer) this.NbJoueurs.getValue();

			this.plateau.setLargeur(largeur);
			this.plateau.setHauteur(hauteur);

			java.util.List<String> noms = new java.util.ArrayList<>();
			for (int i = 0; i < nbJoueurs; i++) noms.add(NOMS_JOUEURS[i]);

			this.frame.dispose();
			jeu.controleur.ControleurJeu controleurJeu = new jeu.controleur.ControleurJeu();
			controleurJeu.lancerPartie(noms, this.plateau);
		}
		catch (NumberFormatException e)
		{
			this.labelErreur.setText("Erreur: valeurs non numériques");
		}
	}

	
	private class PanneauEdition extends JPanel
	{
		private Graphe  plateau;
		private int     rayonSommet = 20;
		private boolean modeAjout;
		private String  zoneCourante;
		private int     prochainId;
		private Sommet  sommetDrag;
		private int     dragX, dragY;
		private Image   fondImage;

		/*-------------------------------*/
		/* Constructeur                  */
		/*-------------------------------*/
		public PanneauEdition()
		{
			this.setPreferredSize(new Dimension(800, 600));
			this.plateau      = new Graphe();
			this.modeAjout    = false;
			this.zoneCourante = "HOPITAL";
			this.prochainId   = 0;

			try { this.fondImage = new ImageIcon("fond_principale.png").getImage(); }
			catch (Exception e) { this.fondImage = null; }

			MouseAdapter adaptateur = new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					int mx = e.getX(), my = e.getY();
					sommetDrag = null;
					for (Sommet s : plateau.getSommets())
					{
						if (s.contientPoint(mx, my))
						{
							sommetDrag = s;
							dragX = mx - s.getX();
							dragY = my - s.getY();
							break;
						}
					}

					if (sommetDrag == null && modeAjout) ajouterSommet(mx, my);
					else if (sommetDrag != null && !modeAjout)
					{
						if (SwingUtilities.isRightMouseButton(e))
						{
							supprimerSommet(sommetDrag.getId());
							sommetDrag = null;
						}
						else setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					}
				}

				public void mouseDragged(MouseEvent e)
				{
					if (sommetDrag != null && !modeAjout)
					{
						int nx = e.getX() - dragX, ny = e.getY() - dragY;
						nx = Math.max(rayonSommet, Math.min(nx, getWidth() - rayonSommet));
						ny = Math.max(rayonSommet, Math.min(ny, getHeight() - rayonSommet));
						sommetDrag.setX(nx);
						sommetDrag.setY(ny);
						
						// Regénère les arêtes après déplacement
						regenererAretes();
						repaint();
					}
				}

				public void mouseReleased(MouseEvent e)
				{
					sommetDrag = null;
					setCursor(Cursor.getDefaultCursor());
				}
			};
			this.addMouseListener(adaptateur);
			this.addMouseMotionListener(adaptateur);
		}

		private void ajouterSommet(int x, int y)
		{
			Sommet nouveau = new Sommet(this.prochainId, x, y, this.zoneCourante);
			this.plateau.ajouterSommet(nouveau);
			this.prochainId++;
			regenererAretes();
			this.repaint();
			labelErreur.setText("Station " + nouveau.getId() + " ajoutée (" + this.zoneCourante + ")");
		}

		private void supprimerSommet(int id)
		{
			this.plateau.supprimerSommet(id);
			regenererAretes();
			this.repaint();
			labelErreur.setText("Station " + id + " supprimée");
		}

		/*-------------------------------*/
		/* Regénère les arêtes auto      */
		/*-------------------------------*/
		private void regenererAretes()
		{
			// Supprime toutes les arêtes
			java.util.List<Arete> anciennesAretes = new java.util.ArrayList<>(this.plateau.getAretes());
			for (Arete a : anciennesAretes)
			{
				this.plateau.supprimerArete(a);
			}

			// Recrée les arêtes entre sommets proches
			java.util.List<Sommet> sommets = this.plateau.getSommets();
			for (int i = 0; i < sommets.size(); i++)
			{
				Sommet s1 = sommets.get(i);
				for (int j = i + 1; j < sommets.size(); j++)
				{
					Sommet s2 = sommets.get(j);
					double distance = Math.hypot(s1.getX() - s2.getX(), s1.getY() - s2.getY());
					if (distance < 150)
					{
						this.plateau.ajouterArete(new Arete(s1.getId(), s2.getId()));
					}
				}
			}
		}

		public void setPlateau(Graphe g)
		{
			this.plateau = g;
			regenererAretes();
			this.repaint();
		}

		public void setModeAjout(boolean mode)
		{
			this.modeAjout = mode;
		}

		public void setZoneCourante(String zone)
		{
			this.zoneCourante = zone;
		}

		public void setProchainId(int id)
		{
			this.prochainId = id;
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (this.fondImage != null)
			{
				g2.drawImage(this.fondImage, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g2.setColor(new Color(50, 50, 60));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}

			// Dessine les arêtes 
			g2.setColor(new Color(200, 180, 100));
			g2.setStroke(new BasicStroke(2.5f));
			for (Arete a : this.plateau.getAretes())
			{
				Sommet s1 = this.plateau.trouverSommet(a.getSrc());
				Sommet s2 = this.plateau.trouverSommet(a.getCible());
				if (s1 != null && s2 != null) g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
			}

			// Dessine les sommets
			for (Sommet s : this.plateau.getSommets())
			{
				Color couleur = Sommet.getCouleurParType(s.getTypeZone());
				g2.setColor(couleur);
				g2.fillOval(s.getX() - this.rayonSommet, s.getY() - this.rayonSommet,
						   this.rayonSommet * 2, this.rayonSommet * 2);

				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 14));
				String lettre = s.getPremiereLettre();
				int textX = s.getX() - g2.getFontMetrics().stringWidth(lettre) / 2;
				g2.drawString(lettre, textX, s.getY() + 5);

				g2.setColor(new Color(255, 255, 255, 100));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawOval(s.getX() - this.rayonSommet, s.getY() - this.rayonSommet,
						   this.rayonSommet * 2, this.rayonSommet * 2);
				
				// Affiche l'ID
				g2.setColor(new Color(200, 200, 200, 150));
				g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
				g2.drawString(String.valueOf(s.getId()), s.getX() - 4, s.getY() - this.rayonSommet - 3);
			}
		}
	}

	public static void main(String[] args)
	{
		ControleurConception fenetre = new ControleurConception();
	}
}