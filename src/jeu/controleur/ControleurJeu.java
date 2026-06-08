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
	private static final int      NB_MANCHES       = 4;
	private static final int      MAX_STATIONS     = 100;
	private static final String[] TYPES_ZONES      = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK"};
	private static final String[] BLOCS            = {"OUEST", "EST", "NON_ALIGNE", "CHINOIS"};
	private static final Color[]  COULEURS_JOUEURS = {new Color(220,60,60), new Color(60,100,220), new Color(60,180,60), new Color(220,200,40)};

	private List<String> nomsJoueurs;
	private int          mancheCourante;
	private int          joueurCourantIdx;
	private boolean      partieTerminee;

	private List<List<Arete>>  cheminsParJoueur;
	private List<Set<Integer>> visitesParJoueur;
	private List<Set<String>>  blocsVisitesParJoueur;
	private int[]              scores;

	private List<String> piocheCartes;
	private List<String> cartesRetournees;
	private int          nbCartesPiochees;
	private String       carteActive;
	private boolean      carteActiveEstJoker;

	private Graphe plateau;
	private int    sommetSelectionne;
	
	
	private String[] zoneParId;
	private String[] blocParId;

	private JFrame         frame;
	private PanneauPlateau panneauPlateau;
	private JLabel         labelManche, labelJoueur, labelCarteActive, labelMessage;
	private JTextArea      zoneScores;
	private JTextArea      zoneBlocsVisites;
	private JPanel         panneauCartes;


	private Image fondImage;
	private Image imageHopital;
	private Image imageFerme;
	private Image imagePetrolier;
	private Image imagePort;
	private Image imageTank;
	private Image imageJoker;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/
	public ControleurJeu()
	{
		this.mancheCourante      = 0;
		this.partieTerminee      = false;
		this.sommetSelectionne   = -1;
		this.carteActive         = "";
		this.carteActiveEstJoker = false;
		this.zoneParId           = new String[MAX_STATIONS];
		this.blocParId           = new String[MAX_STATIONS];
		this.chargerImages();
		this.chargerFond();
	}

	/*-------------------------------*/
	/* Charge les images des cartes  */
	/*-------------------------------*/
	private void chargerImages()
	{
		try
		{
			File f = new File("images/hopital.png");
			if (f.exists()) this.imageHopital = ImageIO.read(f);
			
			f = new File("images/ferme.png");
			if (f.exists()) this.imageFerme = ImageIO.read(f);
			
			f = new File("images/petrolier.png");
			if (f.exists()) this.imagePetrolier = ImageIO.read(f);
			
			f = new File("images/port.png");
			if (f.exists()) this.imagePort = ImageIO.read(f);
			
			f = new File("images/tank.png");
			if (f.exists()) this.imageTank = ImageIO.read(f);
			
			f = new File("images/joker.png");
			if (f.exists()) this.imageJoker = ImageIO.read(f);
		}
		catch (Exception e) { System.out.println("Erreur chargement images"); }
	}

	/*-------------------------------*/
	/* Charge l'image de fond        */
	/*-------------------------------*/
	private void chargerFond()
	{
		try
		{
			File f = new File("images/fond_principale.png");
			if (f.exists()) this.fondImage = ImageIO.read(f);
		}
		catch (Exception e) { this.fondImage = null; }
	}

	/*-------------------------------*/
	/* Recupere l'image d'une carte  */
	/*-------------------------------*/
	private Image getImageParCarte(String typeCarte)
	{
		if (typeCarte.equals("HOPITAL"))   return this.imageHopital;
		if (typeCarte.equals("FERME"))     return this.imageFerme;
		if (typeCarte.equals("PETROLIER")) return this.imagePetrolier;
		if (typeCarte.equals("PORT"))      return this.imagePort;
		if (typeCarte.equals("TANK"))      return this.imageTank;
		if (typeCarte.equals("JOKER"))     return this.imageJoker;
		return null;
	}

	/*-------------------------------*/
	/* Determine le bloc d'un sommet */
	/*-------------------------------*/
	private String getBlocParPosition(int x, int y)
	{
		if (x < 300 && y < 300) return "OUEST";
		if (x > 500 && y < 300) return "EST";
		if (x < 300 && y > 400) return "NON_ALIGNE";
		if (x > 500 && y > 400) return "CHINOIS";
		return "CENTRE";
	}

	/*-------------------------------*/
	/* Lancement de la partie        */
	/*-------------------------------*/
	public void lancerPartie(List<String> nomsJoueurs, Graphe plateau)
	{
		this.nomsJoueurs = nomsJoueurs;
		this.plateau     = plateau;

		// Remplit les tableaux
		for (Sommet s : this.plateau.getSommets())
		{
			int id = s.getId();
			this.zoneParId[id] = s.getTypeZone();
			this.blocParId[id] = this.getBlocParPosition(s.getX(), s.getY());
			System.out.println("Station " + id + " -> " + this.blocParId[id]);
		}

		this.scores                = new int[nomsJoueurs.size()];
		this.cheminsParJoueur      = new ArrayList<>();
		this.visitesParJoueur      = new ArrayList<>();
		this.blocsVisitesParJoueur = new ArrayList<>();

		for (int i = 0; i < nomsJoueurs.size(); i++)
		{
			this.cheminsParJoueur.add(new ArrayList<>());
			this.visitesParJoueur.add(new HashSet<>());
			this.blocsVisitesParJoueur.add(new HashSet<>());
		}

		this.construireInterface();
		this.demarrerManche();
	}

	/*-------------------------------*/
	/* Initialise la pioche          */
	/*-------------------------------*/
	private void initialiserPioche()
	{
		this.piocheCartes = new ArrayList<>();
		for (String type : TYPES_ZONES)
		{
			for (int i = 0; i < 2; i++) this.piocheCartes.add(type);
		}
		this.piocheCartes.add("JOKER");
		Collections.shuffle(this.piocheCartes);
		this.cartesRetournees = new ArrayList<>();
		this.nbCartesPiochees = 0;
	}

	/*-------------------------------*/
	/* Demarre une nouvelle manche   */
	/*-------------------------------*/
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
			this.blocsVisitesParJoueur.get(i).clear();
		}

		this.joueurCourantIdx  = 0;
		this.sommetSelectionne = -1;

		this.initialiserPioche();
		this.piocherCarte();

		this.afficherMessage("MANCHE " + this.mancheCourante);
		this.mettreAJourInterface();
	}

	/*-------------------------------*/
	/* Pioche une carte              */
	/*-------------------------------*/
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

		if (this.carteActive.equals("JOKER"))
		{
			this.carteActiveEstJoker = true;
			this.afficherMessage("JOKER !");
		}
		else
		{
			this.carteActiveEstJoker = false;
			this.afficherMessage("Carte: " + this.carteActive);
		}

		this.mettreAJourInterface();
	}

	/*-------------------------------*/
	/* Passe au joueur suivant       */
	/*-------------------------------*/
	public void passerTour()
	{
		this.sommetSelectionne = -1;
		this.joueurCourantIdx  = (this.joueurCourantIdx + 1) % this.nomsJoueurs.size();

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

	/*-------------------------------*/
	/* Termine la manche             */
	/*-------------------------------*/
	private void finirManche()
	{
		String recap = "=== FIN MANCHE " + this.mancheCourante + " ===\n\n";

		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			int score = this.calculerScoreManche(i);
			this.scores[i] = this.scores[i] + score;

			String blocs = "";
			for (String b : this.blocsVisitesParJoueur.get(i))
			{
				blocs = blocs + " " + b;
			}

			recap = recap + this.nomsJoueurs.get(i) + "\n";
			recap = recap + "   Blocs: " + (blocs.isEmpty() ? "aucun" : blocs) + "\n";
			recap = recap + "   Score: +" + score + "\n";
			recap = recap + "   Total: " + this.scores[i] + " pts\n\n";
		}

		JOptionPane.showMessageDialog(this.frame, recap, "Manche " + this.mancheCourante, JOptionPane.INFORMATION_MESSAGE);
		this.demarrerManche();
	}

	/*-------------------------------*/
	/* Calcule le score              */
	/*-------------------------------*/
	private int calculerScoreManche(int idxJoueur)
	{
		List<Arete> chemin = this.cheminsParJoueur.get(idxJoueur);
		if (chemin.isEmpty()) return 0;

		Set<String> blocs = this.blocsVisitesParJoueur.get(idxJoueur);
		if (blocs.isEmpty()) return 0;

		Map<String, Integer> stationsParBloc = new HashMap<>();
		Set<Integer> sommetsVisites = this.visitesParJoueur.get(idxJoueur);

		for (int id : sommetsVisites)
		{
			String bloc = this.blocParId[id];
			if (bloc != null && !bloc.equals("CENTRE"))
			{
				int nb = stationsParBloc.getOrDefault(bloc, 0);
				stationsParBloc.put(bloc, nb + 1);
			}
		}

		int nbBlocs     = blocs.size();
		int maxStations = 0;
		for (int nb : stationsParBloc.values())
		{
			if (nb > maxStations) maxStations = nb;
		}

		return nbBlocs * maxStations;
	}

	/*-------------------------------*/
	/* Termine la partie             */
	/*-------------------------------*/
	private void terminerPartie()
	{
		this.partieTerminee = true;

		Integer[] indices = new Integer[this.nomsJoueurs.size()];
		for (int i = 0; i < indices.length; i++) indices[i] = i;

		for (int i = 0; i < indices.length - 1; i++)
		{
			for (int j = i + 1; j < indices.length; j++)
			{
				if (this.scores[indices[j]] > this.scores[indices[i]])
				{
					int temp   = indices[i];
					indices[i] = indices[j];
					indices[j] = temp;
				}
			}
		}

		String classement = "🏆 CLASSEMENT FINAL 🏆\n\n";
		int rang = 1;
		for (int idx : indices)
		{
			classement = classement + rang + ". " + this.nomsJoueurs.get(idx) + " — " + this.scores[idx] + " points\n";
			rang++;
		}

		JOptionPane.showMessageDialog(this.frame, classement, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
	}

	/*-------------------------------*/
	/* Tente de tracer une route     */
	/*-------------------------------*/
	public boolean tenterTracerRoute(int idSrc, int idCible)
	{
		if (this.partieTerminee)
		{
			this.afficherMessage("Partie terminee");
			return false;
		}

		if (idSrc == idCible) return false;

		List<Arete> chemin   = this.cheminsParJoueur.get(this.joueurCourantIdx);
		Set<Integer> visites = this.visitesParJoueur.get(this.joueurCourantIdx);

		String zoneCible = this.zoneParId[idCible];

		// Vérifie la carte
		if (!this.carteActiveEstJoker && !this.carteActive.equals(zoneCible))
		{
			this.afficherMessage("Il faut la carte " + zoneCible);
			return false;
		}

		// Vérifie visite
		if (!chemin.isEmpty() && visites.contains(idCible))
		{
			this.afficherMessage("Station deja visitee");
			return false;
		}

		Arete nouvelleArete = new Arete(idSrc, idCible);
		if (chemin.contains(nouvelleArete))
		{
			this.afficherMessage("Cable deja existant");
			return false;
		}

		// Vérifies si linéaire
		if (!chemin.isEmpty())
		{
			int[] extremites = this.trouverExtremites(chemin);
			if (extremites == null)
			{
				this.afficherMessage("Erreur de chemin");
				return false;
			}

			boolean srcExtremite   = (idSrc   == extremites[0] || idSrc   == extremites[1]);
			boolean cibleExtremite = (idCible == extremites[0] || idCible == extremites[1]);

			if (!srcExtremite && !cibleExtremite)
			{
				this.afficherMessage("Le cable doit partir d'une extremite");
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

		// Vérifie degré max
		if (this.getDegreDansChemin(idSrc, chemin) >= 2)
		{
			this.afficherMessage("Cette station a deja 2 connexions");
			return false;
		}
		if (this.getDegreDansChemin(idCible, chemin) >= 2)
		{
			this.afficherMessage("Cette station a deja 2 connexions");
			return false;
		}

		// Vérifie croisement
		if (this.sectionCroise(idSrc, idCible, chemin))
		{
			this.afficherMessage("Les cables ne doivent pas se croiser");
			return false;
		}

		// Vérifie arete
		if (!this.areteExisteDansGraphe(idSrc, idCible))
		{
			this.afficherMessage("Ces stations ne sont pas reliees");
			return false;
		}

		// Ajoute
		chemin.add(nouvelleArete);

		if (visites.isEmpty())
		{
			visites.add(idSrc);
			String blocSrc = this.blocParId[idSrc];
			if (blocSrc != null && !blocSrc.equals("CENTRE"))
			{
				this.blocsVisitesParJoueur.get(this.joueurCourantIdx).add(blocSrc);
			}
		}

		visites.add(idCible);
		String blocCible = this.blocParId[idCible];
		if (blocCible != null && !blocCible.equals("CENTRE"))
		{
			this.blocsVisitesParJoueur.get(this.joueurCourantIdx).add(blocCible);
		}

		this.sommetSelectionne = -1;

		int nbBlocs = this.blocsVisitesParJoueur.get(this.joueurCourantIdx).size();
		this.afficherMessage("Cable connecte ! Blocs: " + nbBlocs + "/4");

		this.mettreAJourInterface();
		return true;
	}

	private int getDegreDansChemin(int id, List<Arete> chemin)
	{
		int d = 0;
		for (Arete a : chemin) if (a.contient(id)) d++;
		return d;
	}

	private int[] trouverExtremites(List<Arete> chemin)
	{
		if (chemin.isEmpty()) return null;

		Map<Integer, Integer> degres = new HashMap<>();
		for (Arete a : chemin)
		{
			degres.put(a.getSrc(),   degres.getOrDefault(a.getSrc(), 0) + 1);
			degres.put(a.getCible(), degres.getOrDefault(a.getCible(), 0) + 1);
		}

		List<Integer> extremites = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : degres.entrySet())
		{
			if (e.getValue() == 1) extremites.add(e.getKey());
		}

		if (extremites.size() == 2) return new int[]{extremites.get(0), extremites.get(1)};
		if (chemin.size()     == 1) return new int[]{chemin.get(0).getSrc(), chemin.get(0).getCible()};
		return null;
	}

	private boolean sectionCroise(int idSrc, int idCible, List<Arete> chemin)
	{
		Sommet sSrc   = this.plateau.trouverSommet(idSrc);
		Sommet sCible = this.plateau.trouverSommet(idCible);

		if (sSrc == null || sCible == null) return false;

		double x1 = sSrc.getX(), y1 = sSrc.getY(), x2 = sCible.getX(), y2 = sCible.getY();

		for (Arete a : chemin)
		{
			if (a.contient(idSrc) || a.contient(idCible)) continue;

			Sommet sA = this.plateau.trouverSommet(a.getSrc());
			Sommet sB = this.plateau.trouverSommet(a.getCible());
			if (sA == null || sB == null) continue;

			double x3 = sA.getX(), y3 = sA.getY(), x4 = sB.getX(), y4 = sB.getY();
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

	/*-------------------------------*/
	/* Interface graphique           */
	/*-------------------------------*/
	private void construireInterface()
	{
		this.frame = new JFrame("Operation Reseau Rouge");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panneauPrincipal = new JPanel(new BorderLayout(8, 8));
		panneauPrincipal.setBackground(new Color(30, 30, 40));
		panneauPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

		this.panneauPlateau = new PanneauPlateau();
		JPanel panneauInfo  = this.creerPanneauInfo();
		this.labelMessage   = new JLabel(" ");
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
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		JPanel panneau = new JPanel();
		panneau.setLayout(new GridLayout(11, 1, 5, 5));  // 11 lignes, 1 colonne, espace 5px
		panneau.setBackground(new Color(40, 40, 50));
		panneau.setBorder(new EmptyBorder(10, 10, 10, 10));
		panneau.setPreferredSize(new Dimension(280, 0));

		JLabel titre = new JLabel("🔴 OPERATION RESEAU ROUGE", JLabel.CENTER);
		titre.setForeground(new Color(220, 80, 80));
		titre.setFont(new Font("Serif", Font.BOLD, 14));

		this.labelManche = new JLabel("Manche 1 / " + NB_MANCHES, JLabel.CENTER);
		this.labelManche.setForeground(new Color(200, 200, 160));

		this.labelJoueur = new JLabel("Agent: —", JLabel.CENTER);
		this.labelJoueur.setForeground(Color.WHITE);

		this.labelCarteActive = new JLabel("Carte: AUCUNE", JLabel.CENTER);
		this.labelCarteActive.setForeground(new Color(180, 220, 180));
		this.labelCarteActive.setFont(new Font("SansSerif", Font.BOLD, 13));

		this.panneauCartes = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		this.panneauCartes.setBackground(new Color(40, 40, 50));
		this.panneauCartes.setBorder(BorderFactory.createTitledBorder("Cartes piochees"));

		JLabel lblScores = new JLabel("SCORES", JLabel.CENTER);
		lblScores.setForeground(new Color(220, 180, 80));
		lblScores.setFont(new Font("Serif", Font.BOLD, 13));

		this.zoneScores = new JTextArea(4, 18);
		this.zoneScores.setEditable(false);
		this.zoneScores.setBackground(new Color(30, 30, 40));
		this.zoneScores.setForeground(new Color(200, 200, 180));
		this.zoneScores.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.zoneScores.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 60)));

		JLabel lblBlocs = new JLabel("BLOCS PARCOURUS", JLabel.CENTER);
		lblBlocs.setForeground(new Color(220, 180, 80));
		lblBlocs.setFont(new Font("Serif", Font.BOLD, 13));

		this.zoneBlocsVisites = new JTextArea(5, 18);
		this.zoneBlocsVisites.setEditable(false);
		this.zoneBlocsVisites.setBackground(new Color(30, 30, 40));
		this.zoneBlocsVisites.setForeground(new Color(200, 200, 180));
		this.zoneBlocsVisites.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.zoneBlocsVisites.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 60)));

		JButton btnPasser  = this.creerBouton("⏭ Passer le tour", new Color(100, 70, 50));
		JButton btnAnnuler = this.creerBouton("↩ Annuler selection", new Color(120, 60, 60));

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panneau.add(titre);
		panneau.add(this.labelManche);
		panneau.add(this.labelJoueur);
		panneau.add(this.labelCarteActive);
		panneau.add(this.panneauCartes);
		panneau.add(lblScores);
		panneau.add(new JScrollPane(this.zoneScores));
		panneau.add(lblBlocs);
		panneau.add(new JScrollPane(this.zoneBlocsVisites));
		panneau.add(btnPasser);
		panneau.add(btnAnnuler);

		/*-------------------------------*/
		/* Activation des composants     */
		/*-------------------------------*/
		btnPasser.addActionListener(e -> this.passerTour());
		btnAnnuler.addActionListener(e -> {
			this.sommetSelectionne = -1;
			this.afficherMessage("Selection annulee");
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
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setMaximumSize(new Dimension(240, 40));
		return btn;
	}

	private void mettreAJourInterface()
	{
		if (this.frame == null) return;

		this.labelManche.setText("Manche " + this.mancheCourante + " / " + NB_MANCHES);

		String nomJoueur = (this.joueurCourantIdx < this.nomsJoueurs.size()) ? this.nomsJoueurs.get(this.joueurCourantIdx) : "—";
		this.labelJoueur.setText("Agent: " + nomJoueur);
		this.labelJoueur.setForeground(COULEURS_JOUEURS[Math.min(this.joueurCourantIdx, COULEURS_JOUEURS.length - 1)]);

		String nomCarte = this.carteActiveEstJoker ? "🎴 JOKER" : this.carteActive;
		this.labelCarteActive.setText("Carte: " + (this.carteActive.isEmpty() ? "AUCUNE" : nomCarte));

		// Cartes
		this.panneauCartes.removeAll();
		for (String carte : this.cartesRetournees)
		{
			Image img = this.getImageParCarte(carte);
			JLabel label = new JLabel();
			if (img != null)
			{
				Image imgRedim = img.getScaledInstance(50, 60, Image.SCALE_SMOOTH);
				label.setIcon(new ImageIcon(imgRedim));
			}
			else
			{
				label.setOpaque(true);
				label.setBackground(Color.GRAY);
				label.setPreferredSize(new Dimension(50, 60));
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setForeground(Color.WHITE);
				label.setText(carte.equals("JOKER") ? "🎴" : carte.substring(0,1));
			}
			label.setToolTipText(carte);
			this.panneauCartes.add(label);
		}
		this.panneauCartes.revalidate();
		this.panneauCartes.repaint();

		// Scores
		String texteScores = "";
		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			texteScores = texteScores + this.nomsJoueurs.get(i) + " : " + this.scores[i] + " pts\n";
		}
		this.zoneScores.setText(texteScores);

		// Blocs
		if (this.joueurCourantIdx < this.blocsVisitesParJoueur.size())
		{
			Set<String> blocs = this.blocsVisitesParJoueur.get(this.joueurCourantIdx);
			String texteBlocs = "Agent courant:\n";
			if (blocs.isEmpty())
			{
				texteBlocs = texteBlocs + "  Aucun bloc\n";
			}
			else
			{
				for (String b : blocs) texteBlocs = texteBlocs + "  ✓ " + b + "\n";
			}
			texteBlocs = texteBlocs + "\nObjectif: OUEST, EST, NON_ALIGNE, CHINOIS";
			this.zoneBlocsVisites.setText(texteBlocs);
		}

		this.panneauPlateau.repaint();
	}

	private void afficherMessage(String msg)
	{
		if (this.labelMessage != null) this.labelMessage.setText(msg);
		System.out.println(msg);
	}

	/*-------------------------------*/
	/* Panneau de dessin             */
	/*-------------------------------*/
	private class PanneauPlateau extends JPanel
	{
		private final int RAYON = 25, SEUIL = 30;

		public PanneauPlateau()
		{
			this.setBackground(new Color(30, 30, 40));
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
					distMin  = dist;
					idClique = s.getId();
				}
			}

			if (idClique == -1) return;

			if (sommetSelectionne == -1)
			{
				sommetSelectionne = idClique;
				String bloc = blocParId[idClique];
				afficherMessage("Station " + idClique + " (" + bloc + ")");
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
				g2.setColor(new Color(40, 40, 50));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}

			// Aretes possibles
			g2.setColor(new Color(200, 180, 100));
			g2.setStroke(new BasicStroke(2f));
			for (Arete a : plateau.getAretes())
			{
				Sommet s1 = plateau.trouverSommet(a.getSrc());
				Sommet s2 = plateau.trouverSommet(a.getCible());
				if (s1 != null && s2 != null) g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
			}

			// Chemins
			for (int i = 0; i < cheminsParJoueur.size(); i++)
			{
				Color couleur = COULEURS_JOUEURS[Math.min(i, COULEURS_JOUEURS.length - 1)];
				g2.setColor(couleur);
				g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				for (Arete a : cheminsParJoueur.get(i))
				{
					Sommet s1 = plateau.trouverSommet(a.getSrc());
					Sommet s2 = plateau.trouverSommet(a.getCible());
					if (s1 != null && s2 != null) g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
				}
			}

	
			// Sommets
			for (Sommet s : plateau.getSommets())
			{
				int id = s.getId();
	
				// Récupère le type de zone 
				String typeZone = ControleurJeu.this.zoneParId[id];
				if (typeZone == null) typeZone = "HOPITAL";  // Valeur par défaut
				Color couleur = Sommet.getCouleurParType(typeZone);

				if (id == sommetSelectionne)
				{
					g2.setColor(new Color(255, 255, 150, 100));
					g2.fillOval(s.getX() - RAYON - 5, s.getY() - RAYON - 5, (RAYON + 5) * 2, (RAYON + 5) * 2);
				}

				g2.setColor(couleur);
				g2.fillOval(s.getX() - RAYON, s.getY() - RAYON, RAYON * 2, RAYON * 2);

				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 14));
				String lettre = s.getPremiereLettre();
				int textX = s.getX() - g2.getFontMetrics().stringWidth(lettre) / 2;
				g2.drawString(lettre, textX, s.getY() + 6);

				g2.setColor(new Color(255, 255, 255, 80));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawOval(s.getX() - RAYON, s.getY() - RAYON, RAYON * 2, RAYON * 2);
			}
		}
	}
}