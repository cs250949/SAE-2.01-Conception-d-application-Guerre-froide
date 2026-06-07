package jeu.controleur;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ControleurJeu
{
	private static final int NB_MANCHES = 4;
	private static final String[] TYPES_ZONES = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK"};
	private static final Color[] COULEURS_JOUEURS = {new Color(220,60,60), new Color(60,100,220), new Color(60,180,60), new Color(220,200,40)};
	private static final Color[] COULEURS_CARTES = {new Color(220,60,60), new Color(60,180,60), new Color(40,40,40), new Color(60,120,220), new Color(160,100,40), new Color(200,200,60)};

	private List<String> nomsJoueurs;
	private int mancheCourante, joueurCourantIdx;
	private boolean partieTerminee;
	private List<List<Arete>> cheminsParJoueur;
	private List<Set<Integer>> visitesParJoueur;
	private List<Set<String>> zonesVisitesParJoueur;
	private int[] scores;
	private List<String> piocheCartes;
	private List<String> cartesRetournees;
	private int nbCartesPiochees;
	private String carteActive;
	private boolean carteActiveEstJoker;
	private Graphe plateau;
	private int sommetSelectionne;
	private Map<Integer, String> idToZone;
	private JFrame frame;
	private PanneauPlateau panneauPlateau;
	private JLabel labelManche, labelJoueur, labelCarteActive, labelMessage;
	private JTextArea zoneScores, zoneZonesVisites;
	private JPanel panneauCartes;
	private Image fondImage;
	private Map<String, Image> imagesCartes;

	public ControleurJeu()
	{
		this.mancheCourante = 0;
		this.partieTerminee = false;
		this.sommetSelectionne = -1;
		this.carteActive = "";
		this.carteActiveEstJoker = false;
		this.idToZone = new HashMap<>();
		this.imagesCartes = new HashMap<>();
		this.chargerImages();
		this.chargerFond();
	}

	private void chargerImages()
	{
		String[] fichiers = {"hopital.png", "ferme.png", "petrolier.png", "port.png", "tank.png", "joker.png"};
		String[] noms = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK", "JOKER"};
		for (int i = 0; i < fichiers.length; i++)
		{
			try
			{
				File f = new File("images/" + fichiers[i]);
				if (f.exists())
				{
					this.imagesCartes.put(noms[i], ImageIO.read(f));
					System.out.println("Image chargée: images/" + fichiers[i]);
				}
				else
				{
					System.out.println("Image non trouvée: images/" + fichiers[i]);
				}
			}
			catch (Exception e)
			{
				System.out.println("Erreur chargement: images/" + fichiers[i]);
			}
		}
	}

	private void chargerFond()
	{
		try
		{
			File f = new File("fond_principale.png");
			if (f.exists())
			{
				this.fondImage = ImageIO.read(f);
				System.out.println("Fond chargé: fond_principale.png");
			}
			else
			{
				System.out.println("Fond non trouvé: fond_principale.png");
			}
		}
		catch (Exception e)
		{
			this.fondImage = null;
		}
	}

	public void lancerPartie(List<String> nomsJoueurs, Graphe plateau)
	{
		this.nomsJoueurs = nomsJoueurs;
		this.plateau = plateau;
		for (Sommet s : this.plateau.getSommets())
		{
			this.idToZone.put(s.getId(), s.getTypeZone());
		}

		this.scores = new int[nomsJoueurs.size()];
		this.cheminsParJoueur = new ArrayList<>();
		this.visitesParJoueur = new ArrayList<>();
		this.zonesVisitesParJoueur = new ArrayList<>();

		for (int i = 0; i < nomsJoueurs.size(); i++)
		{
			this.cheminsParJoueur.add(new ArrayList<>());
			this.visitesParJoueur.add(new HashSet<>());
			this.zonesVisitesParJoueur.add(new HashSet<>());
		}

		this.construireInterface();
		this.demarrerManche();
	}

	private void initialiserPioche()
	{
		this.piocheCartes = new ArrayList<>();
		for (String type : TYPES_ZONES)
		{
			for (int i = 0; i < 2; i++)
			{
				this.piocheCartes.add(type);
			}
		}
		this.piocheCartes.add("JOKER");
		Collections.shuffle(this.piocheCartes);
		this.cartesRetournees = new ArrayList<>();
		this.nbCartesPiochees = 0;
		System.out.println("Pioche initialisée avec " + this.piocheCartes.size() + " cartes mélangées");
	}

	private void demarrerManche()
	{
		this.mancheCourante++;
		if (this.mancheCourante > NB_MANCHES)
		{
			this.terminerPartie();
			return;
		}

		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			this.cheminsParJoueur.get(i).clear();
			this.visitesParJoueur.get(i).clear();
			this.zonesVisitesParJoueur.get(i).clear();
		}

		this.joueurCourantIdx = 0;
		this.sommetSelectionne = -1;
		this.initialiserPioche();
		this.piocherCarte();
		this.afficherMessage("MANCHE " + this.mancheCourante + " - Connectez votre réseau !");
		this.mettreAJourInterface();
	}

	private void piocherCarte()
	{
		if (this.piocheCartes.isEmpty())
		{
			this.finirManche();
			return;
		}

		this.carteActive = this.piocheCartes.remove(0);
		this.cartesRetournees.add(this.carteActive);
		this.nbCartesPiochees++;
		this.carteActiveEstJoker = this.carteActive.equals("JOKER");
		this.afficherMessage("Carte piochée: " + (this.carteActiveEstJoker ? "🎴 JOKER !" : this.carteActive));
		this.mettreAJourInterface();
	}

	public void passerTour()
	{
		this.sommetSelectionne = -1;
		this.joueurCourantIdx = (this.joueurCourantIdx + 1) % this.nomsJoueurs.size();

		if (this.joueurCourantIdx == 0)
		{
			if (this.nbCartesPiochees >= 11)
			{
				this.finirManche();
				return;
			}
			this.piocherCarte();
		}

		this.afficherMessage("Tour de " + this.nomsJoueurs.get(this.joueurCourantIdx));
		this.mettreAJourInterface();
	}

	private void finirManche()
	{
		String recap = "=== FIN MANCHE " + this.mancheCourante + " ===\n";
		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			int score = this.calculerScoreManche(i);
			this.scores[i] += score;
			String zones = "";
			for (String z : this.zonesVisitesParJoueur.get(i))
			{
				zones = zones + z.substring(0,1) + " ";
			}
			recap = recap + this.nomsJoueurs.get(i) + " [zones:" + zones + "] +" + score + " = " + this.scores[i] + " pts\n";
		}
		JOptionPane.showMessageDialog(this.frame, recap, "Résultats manche " + this.mancheCourante, JOptionPane.INFORMATION_MESSAGE);
		this.demarrerManche();
	}

	private int calculerScoreManche(int idxJoueur)
	{
		List<Arete> chemin = this.cheminsParJoueur.get(idxJoueur);
		if (chemin.isEmpty()) return 0;

		Set<String> zones = this.zonesVisitesParJoueur.get(idxJoueur);
		if (zones.isEmpty()) return 0;

		Map<String, Integer> stationsParZone = new HashMap<>();
		Set<Integer> sommetsVisites = this.visitesParJoueur.get(idxJoueur);

		for (int id : sommetsVisites)
		{
			String zone = this.idToZone.get(id);
			if (zone != null)
			{
				stationsParZone.put(zone, stationsParZone.getOrDefault(zone, 0) + 1);
			}
		}

		int nbZones = zones.size();
		int maxStations = 0;
		for (int nb : stationsParZone.values())
		{
			if (nb > maxStations) maxStations = nb;
		}
		return nbZones * maxStations;
	}

	private void terminerPartie()
	{
		this.partieTerminee = true;

		Integer[] indices = new Integer[this.nomsJoueurs.size()];
		for (int i = 0; i < indices.length; i++) indices[i] = i;
		Arrays.sort(indices, (a, b) -> this.scores[b] - this.scores[a]);

		String classement = "🏆 CLASSEMENT FINAL 🏆\n\n";
		int rang = 1;
		for (int idx : indices)
		{
			classement = classement + rang++ + ". " + this.nomsJoueurs.get(idx) + " — " + this.scores[idx] + " points\n";
		}
		JOptionPane.showMessageDialog(this.frame, classement, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
		this.afficherMessage("Partie terminée !");
	}

	public boolean tenterTracerRoute(int idSrc, int idCible)
	{
		if (this.partieTerminee)
		{
			this.afficherMessage("Partie terminée");
			return false;
		}
		if (idSrc == idCible) return false;

		List<Arete> chemin = this.cheminsParJoueur.get(this.joueurCourantIdx);
		Set<Integer> visites = this.visitesParJoueur.get(this.joueurCourantIdx);
		Sommet sCible = this.plateau.trouverSommet(idCible);
		String zoneCible = this.idToZone.get(idCible);

		if (!this.carteActiveEstJoker && sCible != null && !this.carteActive.equals(zoneCible))
		{
			this.afficherMessage("Vous n'avez pas la carte pour " + zoneCible);
			return false;
		}

		if (!chemin.isEmpty() && visites.contains(idCible))
		{
			this.afficherMessage("Station déjà visitée");
			return false;
		}

		Arete nouvelleArete = new Arete(idSrc, idCible);
		if (chemin.contains(nouvelleArete))
		{
			this.afficherMessage("Câble déjà existant");
			return false;
		}

		if (!chemin.isEmpty())
		{
			int[] extremites = this.trouverExtremites(chemin);
			if (extremites == null)
			{
				this.afficherMessage("Erreur de chemin");
				return false;
			}

			boolean srcExtremite = (idSrc == extremites[0] || idSrc == extremites[1]);
			boolean cibleExtremite = (idCible == extremites[0] || idCible == extremites[1]);

			if (!srcExtremite && !cibleExtremite)
			{
				this.afficherMessage("Le câble doit partir d'une extrémité");
				return false;
			}

			if (cibleExtremite && !srcExtremite)
			{
				int tmp = idSrc;
				idSrc = idCible;
				idCible = tmp;
				nouvelleArete = new Arete(idSrc, idCible);
			}
		}

		if (this.getDegreDansChemin(idSrc, chemin) >= 2)
		{
			this.afficherMessage("Cette station a déjà 2 connexions");
			return false;
		}
		if (this.getDegreDansChemin(idCible, chemin) >= 2)
		{
			this.afficherMessage("Cette station a déjà 2 connexions");
			return false;
		}

		if (this.sectionCroise(idSrc, idCible, chemin))
		{
			this.afficherMessage("Les câbles ne doivent pas se croiser");
			return false;
		}
		if (!this.areteExisteDansGraphe(idSrc, idCible))
		{
			this.afficherMessage("Ces stations ne sont pas reliables");
			return false;
		}

		chemin.add(nouvelleArete);

		if (visites.isEmpty())
		{
			visites.add(idSrc);
			String zoneSrc = this.idToZone.get(idSrc);
			if (zoneSrc != null) this.zonesVisitesParJoueur.get(this.joueurCourantIdx).add(zoneSrc);
		}
		visites.add(idCible);
		if (zoneCible != null) this.zonesVisitesParJoueur.get(this.joueurCourantIdx).add(zoneCible);

		this.sommetSelectionne = -1;
		int nbZones = this.zonesVisitesParJoueur.get(this.joueurCourantIdx).size();
		this.afficherMessage("Câble connecté ! Zones visitées: " + nbZones + "/5");
		this.mettreAJourInterface();
		return true;
	}

	private int getDegreDansChemin(int id, List<Arete> chemin)
	{
		int d = 0;
		for (Arete a : chemin)
		{
			if (a.contient(id)) d++;
		}
		return d;
	}

	private int[] trouverExtremites(List<Arete> chemin)
	{
		if (chemin.isEmpty()) return null;

		Map<Integer, Integer> degres = new HashMap<>();
		for (Arete a : chemin)
		{
			degres.put(a.getSrc(), degres.getOrDefault(a.getSrc(), 0) + 1);
			degres.put(a.getCible(), degres.getOrDefault(a.getCible(), 0) + 1);
		}

		List<Integer> extremites = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : degres.entrySet())
		{
			if (e.getValue() == 1) extremites.add(e.getKey());
		}

		if (extremites.size() == 2) return new int[]{extremites.get(0), extremites.get(1)};
		if (chemin.size() == 1) return new int[]{chemin.get(0).getSrc(), chemin.get(0).getCible()};
		return null;
	}

	private boolean sectionCroise(int idSrc, int idCible, List<Arete> chemin)
	{
		Sommet sSrc = this.plateau.trouverSommet(idSrc);
		Sommet sCible = this.plateau.trouverSommet(idCible);
		if (sSrc == null || sCible == null) return false;

		double x1 = sSrc.getX(), y1 = sSrc.getY();
		double x2 = sCible.getX(), y2 = sCible.getY();

		for (Arete a : chemin)
		{
			if (a.contient(idSrc) || a.contient(idCible)) continue;

			Sommet sA = this.plateau.trouverSommet(a.getSrc());
			Sommet sB = this.plateau.trouverSommet(a.getCible());
			if (sA == null || sB == null) continue;

			double x3 = sA.getX(), y3 = sA.getY();
			double x4 = sB.getX(), y4 = sB.getY();

			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) return true;
		}
		return false;
	}

	private boolean areteExisteDansGraphe(int idSrc, int idCible)
	{
		for (Arete a : this.plateau.getAretes())
		{
			if (a.contient(idSrc) && a.contient(idCible)) return true;
		}
		return false;
	}

	private void construireInterface()
	{
		this.frame = new JFrame("Opération Réseau Rouge");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panneauPrincipal = new JPanel(new BorderLayout(8, 8));
		panneauPrincipal.setBackground(new Color(30, 30, 40));
		panneauPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

		this.panneauPlateau = new PanneauPlateau();
		JPanel panneauInfo = this.creerPanneauInfo();

		this.labelMessage = new JLabel(" ");
		this.labelMessage.setForeground(new Color(200, 200, 180));
		this.labelMessage.setFont(new Font("Serif", Font.ITALIC, 13));

		panneauPrincipal.add(this.panneauPlateau, BorderLayout.CENTER);
		panneauPrincipal.add(panneauInfo, BorderLayout.EAST);
		panneauPrincipal.add(this.labelMessage, BorderLayout.SOUTH);

		this.frame.setContentPane(panneauPrincipal);
		this.frame.setSize(1200, 800);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}

	private JPanel creerPanneauInfo()
	{
		JPanel panneau = new JPanel();
		panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
		panneau.setBackground(new Color(40, 40, 50));
		panneau.setBorder(new EmptyBorder(10, 10, 10, 10));
		panneau.setPreferredSize(new Dimension(280, 0));

		JLabel titre = new JLabel("🔴 OPÉRATION RÉSEAU ROUGE");
		titre.setForeground(new Color(220, 80, 80));
		titre.setFont(new Font("Serif", Font.BOLD, 14));
		titre.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.labelManche = new JLabel("Manche 1 / " + NB_MANCHES);
		this.labelManche.setForeground(new Color(200, 200, 160));

		this.labelJoueur = new JLabel("Agent: —");
		this.labelJoueur.setForeground(Color.WHITE);

		this.labelCarteActive = new JLabel("Carte: AUCUNE");
		this.labelCarteActive.setForeground(new Color(180, 220, 180));
		this.labelCarteActive.setFont(new Font("SansSerif", Font.BOLD, 13));

		this.panneauCartes = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		this.panneauCartes.setBackground(new Color(40, 40, 50));
		this.panneauCartes.setBorder(BorderFactory.createTitledBorder("Cartes piochées"));
		this.panneauCartes.setMaximumSize(new Dimension(260, 120));

		JLabel lblScores = new JLabel("SCORES");
		lblScores.setForeground(new Color(220, 180, 80));

		this.zoneScores = new JTextArea(4, 18);
		this.zoneScores.setEditable(false);
		this.zoneScores.setBackground(new Color(30, 30, 40));
		this.zoneScores.setForeground(new Color(200, 200, 180));

		JLabel lblZones = new JLabel("ZONES VISITÉES");
		lblZones.setForeground(new Color(220, 180, 80));

		this.zoneZonesVisites = new JTextArea(5, 18);
		this.zoneZonesVisites.setEditable(false);
		this.zoneZonesVisites.setBackground(new Color(30, 30, 40));
		this.zoneZonesVisites.setForeground(new Color(200, 200, 180));

		JButton btnPasser = this.creerBouton("⏭ Passer le tour", new Color(100, 70, 50));
		JButton btnAnnuler = this.creerBouton("↩ Annuler sélection", new Color(120, 60, 60));

		panneau.add(titre);
		panneau.add(Box.createVerticalStrut(12));
		panneau.add(this.labelManche);
		panneau.add(Box.createVerticalStrut(6));
		panneau.add(this.labelJoueur);
		panneau.add(Box.createVerticalStrut(10));
		panneau.add(this.labelCarteActive);
		panneau.add(Box.createVerticalStrut(6));
		panneau.add(this.panneauCartes);
		panneau.add(Box.createVerticalStrut(10));
		panneau.add(lblScores);
		panneau.add(new JScrollPane(this.zoneScores));
		panneau.add(Box.createVerticalStrut(10));
		panneau.add(lblZones);
		panneau.add(new JScrollPane(this.zoneZonesVisites));
		panneau.add(Box.createVerticalStrut(10));
		panneau.add(btnPasser);
		panneau.add(Box.createVerticalStrut(6));
		panneau.add(btnAnnuler);

		btnPasser.addActionListener(e -> this.passerTour());
		btnAnnuler.addActionListener(e ->
		{
			this.sommetSelectionne = -1;
			this.afficherMessage("Sélection annulée");
			this.mettreAJourInterface();
		});

		return panneau;
	}

	private JButton creerBouton(String texte, Color couleur)
	{
		JButton btn = new JButton(texte);
		btn.setBackground(couleur);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(new EmptyBorder(8, 12, 8, 12));
		btn.setFont(new Font("SansSerif", Font.BOLD, 12));
		return btn;
	}

	private void mettreAJourInterface()
	{
		if (this.frame == null) return;

		this.labelManche.setText("Manche " + this.mancheCourante + " / " + NB_MANCHES);

		String nomJoueur = (this.joueurCourantIdx < this.nomsJoueurs.size()) ? this.nomsJoueurs.get(this.joueurCourantIdx) : "—";
		this.labelJoueur.setText("Agent: " + nomJoueur);
		this.labelJoueur.setForeground(COULEURS_JOUEURS[Math.min(this.joueurCourantIdx, COULEURS_JOUEURS.length - 1)]);

		String nomCarte = this.carteActiveEstJoker ? "🎴 JOKER !" : this.carteActive;
		this.labelCarteActive.setText("🎴 Carte: " + (this.carteActive.isEmpty() ? "AUCUNE" : nomCarte));

		this.panneauCartes.removeAll();
		for (String c : this.cartesRetournees)
		{
			JLabel carteLabel = new JLabel();
			carteLabel.setOpaque(true);
			int idx = 0;
			for (int i = 0; i < TYPES_ZONES.length; i++)
			{
				if (TYPES_ZONES[i].equals(c)) idx = i;
			}
			if (c.equals("JOKER")) idx = 5;

			carteLabel.setBackground(COULEURS_CARTES[idx]);
			carteLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
			carteLabel.setPreferredSize(new Dimension(45, 55));
			carteLabel.setHorizontalAlignment(JLabel.CENTER);
			carteLabel.setForeground(Color.WHITE);
			carteLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			String aff = c.equals("JOKER") ? "🎴" : c.substring(0,1);
			carteLabel.setText(aff);
			carteLabel.setToolTipText(c);
			this.panneauCartes.add(carteLabel);
		}
		this.panneauCartes.revalidate();
		this.panneauCartes.repaint();

		String texteScores = "";
		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			texteScores = texteScores + this.nomsJoueurs.get(i) + " : " + this.scores[i] + " pts\n";
		}
		this.zoneScores.setText(texteScores);

		if (this.joueurCourantIdx < this.zonesVisitesParJoueur.size())
		{
			Set<String> zones = this.zonesVisitesParJoueur.get(this.joueurCourantIdx);
			String texteZones = "Agent courant:\n";
			if (zones.isEmpty())
			{
				texteZones = texteZones + "  Aucune zone visitée\n";
			}
			else
			{
				for (String z : zones) texteZones = texteZones + "  ✓ " + z + "\n";
			}
			texteZones = texteZones + "\nObjectif: visiter les 5 zones !";
			this.zoneZonesVisites.setText(texteZones);
		}

		this.panneauPlateau.repaint();
	}

	private void afficherMessage(String msg)
	{
		if (this.labelMessage != null) this.labelMessage.setText(msg);
		System.out.println(msg);
	}

	private class PanneauPlateau extends JPanel
	{
		private final int RAYON = 25, SEUIL = 30;

		public PanneauPlateau()
		{
			this.setBackground(new Color(30,30,40));
			this.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					gererClic(e.getX(), e.getY());
				}
			});
		}

		private void gererClic(int mx, int my)
		{
			if (partieTerminee) return;

			int idClique = -1;
			double distMin = Double.MAX_VALUE;

			for (Sommet s : plateau.getSommets())
			{
				double dist = Math.hypot(s.getX() - mx, s.getY() - my);
				if (dist < SEUIL && dist < distMin)
				{
					distMin = dist;
					idClique = s.getId();
				}
			}

			if (idClique == -1) return;

			if (sommetSelectionne == -1)
			{
				sommetSelectionne = idClique;
				afficherMessage("Station " + idClique + " sélectionnée");
				repaint();
			}
			else
			{
				int src = sommetSelectionne, cible = idClique;
				sommetSelectionne = -1;
				tenterTracerRoute(src, cible);
			}
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (fondImage != null)
			{
				g2.drawImage(fondImage, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g2.setColor(new Color(40,40,50));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}

			g2.setColor(new Color(100,90,80));
			g2.setStroke(new BasicStroke(2f));
			for (Arete a : plateau.getAretes())
			{
				Sommet s1 = plateau.trouverSommet(a.getSrc());
				Sommet s2 = plateau.trouverSommet(a.getCible());
				if (s1 != null && s2 != null) g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
			}

			for (int i = 0; i < cheminsParJoueur.size(); i++)
			{
				Color couleur = COULEURS_JOUEURS[Math.min(i, COULEURS_JOUEURS.length-1)];
				g2.setColor(couleur);
				g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				for (Arete a : cheminsParJoueur.get(i))
				{
					Sommet s1 = plateau.trouverSommet(a.getSrc());
					Sommet s2 = plateau.trouverSommet(a.getCible());
					if (s1 != null && s2 != null) g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
				}
			}

			for (Sommet s : plateau.getSommets())
			{
				Color couleur = Sommet.getCouleurParType(s.getTypeZone());

				if (s.getId() == sommetSelectionne)
				{
					g2.setColor(new Color(255,255,150,100));
					g2.fillOval(s.getX()-RAYON-5, s.getY()-RAYON-5, (RAYON+5)*2, (RAYON+5)*2);
				}

				g2.setColor(couleur);
				g2.fillOval(s.getX()-RAYON, s.getY()-RAYON, RAYON*2, RAYON*2);

				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 14));
				String lettre = s.getPremiereLettre();
				int textX = s.getX() - g2.getFontMetrics().stringWidth(lettre)/2;
				g2.drawString(lettre, textX, s.getY()+6);

				g2.setColor(new Color(255,255,255,80));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawOval(s.getX()-RAYON, s.getY()-RAYON, RAYON*2, RAYON*2);

				g2.setColor(new Color(200,200,200,150));
				g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
				g2.drawString(String.valueOf(s.getId()), s.getX()-4, s.getY()-RAYON-3);
			}
		}
	}
}