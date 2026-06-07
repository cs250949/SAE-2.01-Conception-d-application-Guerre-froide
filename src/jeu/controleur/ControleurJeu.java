package jeu.controleur;

import commun.Arete;
import commun.Graphe;
import commun.Sommet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

/*-------------------------------*/
/* Contrôleur principal du jeu   */
/*-------------------------------*/
public class ControleurJeu
{
	private static final int NB_MANCHES = 4;
	private static final String[] TYPES_ZONES = {"HOPITAL", "FERME", "PETROLIER", "PORT", "TANK"};
	private static final Color[] COULEURS_JOUEURS = {new Color(220,60,60), new Color(60,100,220), new Color(60,180,60), new Color(220,200,40)};

	private List<String> nomsJoueurs;
	private int          mancheCourante, joueurCourantIdx;
	private boolean      partieTerminee;

	private List<List<Arete>>   cheminsParJoueur;
	private List<Set<Integer>>  visitesParJoueur;
	private List<Set<String>>   zonesVisitesParJoueur;
	private int[]               scores;

	private List<String> piocheCartes;
	private List<String> cartesRetournees;
	private int          nbCartesPiochees;
	private String       carteActive;
	private boolean      carteActiveEstJoker;

	private Graphe               plateau;
	private int                  sommetSelectionne;
	private Map<Integer, String> idToZone;

	private JFrame         frame;
	private PanneauPlateau panneauPlateau;
	private JLabel         labelManche, labelJoueur, labelCarteActive, labelMessage;
	private JTextArea      zoneScores;
	private JTextArea      zoneZonesVisites; 
	private JPanel         panneauCartes;
	private Image          fondImage;

	
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
		this.idToZone            = new HashMap<>();
		this.chargerImages();
		this.chargerFond();
	}

	/*-------------------------------*/
	/* Charger les images des cartes  */
	/*-------------------------------*/
	private void chargerImages()
	{
		try
		{
			File fichierHopital = new File("images/hopital.png");
			if (fichierHopital.exists())
			{
				this.imageHopital = ImageIO.read(fichierHopital);
				System.out.println("Image hopital chargée");
			}

			File fichierFerme = new File("images/ferme.png");
			if (fichierFerme.exists())
			{
				this.imageFerme = ImageIO.read(fichierFerme);
				System.out.println("Image ferme chargée");
			}

			File fichierPetrolier = new File("images/petrolier.png");
			if (fichierPetrolier.exists())
			{
				this.imagePetrolier = ImageIO.read(fichierPetrolier);
				System.out.println("Image petrolier chargée");
			}

			File fichierPort = new File("images/port.png");
			if (fichierPort.exists())
			{
				this.imagePort = ImageIO.read(fichierPort);
				System.out.println("Image port chargée");
			}

			File fichierTank = new File("images/tank.png");
			if (fichierTank.exists())
			{
				this.imageTank = ImageIO.read(fichierTank);
				System.out.println("Image tank chargée");
			}

			File fichierJoker = new File("images/joker.png");
			if (fichierJoker.exists())
			{
				this.imageJoker = ImageIO.read(fichierJoker);
				System.out.println("Image joker chargée");
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur chargement images: " + e.getMessage());
		}
	}

	/*-------------------------------*/
	/* Charger l'image de fond        */
	/*-------------------------------*/
	private void chargerFond()
	{
		try
		{
			File fichierFond = new File("images/fond_principale.png");
			if (fichierFond.exists())
			{
				this.fondImage = ImageIO.read(fichierFond);
				System.out.println("Fond charge");
			}
		}
		catch (Exception e)
		{
			this.fondImage = null;
		}
	}

	/*-------------------------------*/
	/* Recupere l'image d'une carte  */
	/* selon son type                */
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
	/* Lancement de la partie        */
	/*-------------------------------*/
	public void lancerPartie(List<String> nomsJoueurs, Graphe plateau)
	{
		this.nomsJoueurs = nomsJoueurs;
		this.plateau     = plateau;

		for (Sommet s : this.plateau.getSommets())
		{
			this.idToZone.put(s.getId(), s.getTypeZone());
		}

		this.scores           = new int[nomsJoueurs.size()];
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

	/*-------------------------------*/
	/* Initialise la pioche          */
	/*-------------------------------*/
	private void initialiserPioche()
	{
		this.piocheCartes = new ArrayList<>();

		// Ajoute 2 cartes de chaque type
		for (String type : TYPES_ZONES)
		{
			for (int i = 0; i < 2; i++)
			{
				this.piocheCartes.add(type);
			}
		}

		// Ajoute 1 carte JOKER
		this.piocheCartes.add("JOKER");

		// Melange la pioche
		Collections.shuffle(this.piocheCartes);

		this.cartesRetournees = new ArrayList<>();
		this.nbCartesPiochees = 0;

		System.out.println("Pioche cree avec " + this.piocheCartes.size() + " cartes");
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

		// Vide les chemins et visites des joueurs
		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			this.cheminsParJoueur.get(i).clear();
			this.visitesParJoueur.get(i).clear();
			this.zonesVisitesParJoueur.get(i).clear();
		}

		this.joueurCourantIdx  = 0;
		this.sommetSelectionne = -1;

		this.initialiserPioche();
		this.piocherCarte();

		this.afficherMessage("MANCHE " + this.mancheCourante + " - Connectez votre réseau !");
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
			this.afficherMessage("CARTE JOKER ! Vous pouvez connecter n'importe quelle station !");
		}
		else
		{
			this.carteActiveEstJoker = false;
			this.afficherMessage("Carte piochee: " + this.carteActive);
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
			if (this.nbCartesPiochees >= 11)  // 10 cartes + 1 joker
			{
				this.finirManche();
				return;
			}
			this.piocherCarte();
		}

		this.afficherMessage("Tour de " + this.nomsJoueurs.get(this.joueurCourantIdx));
		this.mettreAJourInterface();
	}

	//Fin de la manche: calcule les scores, affiche un recap et demarre la manche suivante
	private void finirManche()
	{
		String recap = "=== FIN MANCHE " + this.mancheCourante + " ===\n\n";

		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			int score = this.calculerScoreManche(i);
			this.scores[i] = this.scores[i] + score;

			String zones = "";
			for (String z : this.zonesVisitesParJoueur.get(i))
			{
				zones = zones + " " + z;
			}

			recap = recap + this.nomsJoueurs.get(i) + "\n";
			recap = recap + "   Zones visitees:" + zones + "\n";
			recap = recap + "   Score de la manche: +" + score + "\n";
			recap = recap + "   Total: " + this.scores[i] + " points\n\n";
		}

		JOptionPane.showMessageDialog(this.frame, recap, "Résultats manche " + this.mancheCourante, JOptionPane.INFORMATION_MESSAGE);
		this.demarrerManche();
	}

	
	// Calcule le score d'un joueur  pour la manche
	private int calculerScoreManche(int idxJoueur)
	{
		List<Arete> chemin = this.cheminsParJoueur.get(idxJoueur);

		if (chemin.isEmpty())
		{
			return 0;
		}

		Set<String> zones = this.zonesVisitesParJoueur.get(idxJoueur);

		if (zones.isEmpty())
		{
			return 0;
		}

		Map<String, Integer> stationsParZone = new HashMap<>();
		Set<Integer> sommetsVisites = this.visitesParJoueur.get(idxJoueur);

		for (int id : sommetsVisites)
		{
			String zone = this.idToZone.get(id);
			if (zone != null)
			{
				int nbActuel = 0;
				if (stationsParZone.containsKey(zone))
				{
					nbActuel = stationsParZone.get(zone);
				}
				stationsParZone.put(zone, nbActuel + 1);
			}
		}

		int nbZones = zones.size();

		int maxStations = 0;
		for (int nb : stationsParZone.values())
		{
			if (nb > maxStations)
			{
				maxStations = nb;
			}
		}

		return nbZones * maxStations;
	}

	
	//Termine la partie
	private void terminerPartie()
	{
		this.partieTerminee = true;

		// Trie les joueurs par score
		Integer[] indices = new Integer[this.nomsJoueurs.size()];
		for (int i = 0; i < indices.length; i++)
		{
			indices[i] = i;
		}

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
			rang = rang + 1;
		}

		JOptionPane.showMessageDialog(this.frame, classement, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
		this.afficherMessage("Partie terminee !");
	}

	
	//Tente de tracer une route     
	public boolean tenterTracerRoute(int idSrc, int idCible)
	{
		// Verifications de base
		if (this.partieTerminee)
		{
			this.afficherMessage("Partie terminee");
			return false;
		}

		if (idSrc == idCible)
		{
			return false;
		}

		List<Arete> chemin   = this.cheminsParJoueur.get(this.joueurCourantIdx);
		Set<Integer> visites = this.visitesParJoueur.get(this.joueurCourantIdx);

		Sommet sCible    = this.plateau.trouverSommet(idCible);
		String zoneCible = this.idToZone.get(idCible);

		// Vérifie la carte sauf si c'est un JOKER
		if (!this.carteActiveEstJoker)
		{
			if (sCible != null && !this.carteActive.equals(zoneCible))
			{
				this.afficherMessage("Vous n'avez pas la carte pour " + zoneCible);
				return false;
			}
		}

		// Vérifie qu'on ne repasse pas sur une station deja visitée
		if (!chemin.isEmpty() && visites.contains(idCible))
		{
			this.afficherMessage("Station deja visitée");
			return false;
		}

		Arete nouvelleArete = new Arete(idSrc, idCible);

		// Vérifie que l'arete n'éxiste pas deja
		if (chemin.contains(nouvelleArete))
		{
			this.afficherMessage("Cable deja existant");
			return false;
		}

		// Vérifie la linearite (pas d'embranchement)
		if (!chemin.isEmpty())
		{
			int[] extremites = this.trouverExtremites(chemin);
			if (extremites == null)
			{
				this.afficherMessage("Erreur de chemin");
				return false;
			}

			boolean srcExtremite   = (idSrc == extremites[0] || idSrc == extremites[1]);
			boolean cibleExtremite = (idCible == extremites[0] || idCible == extremites[1]);

			if (!srcExtremite && !cibleExtremite)
			{
				this.afficherMessage("Le cable doit partir d'une extremite");
				return false;
			}

			// Ajuste l'ordre si necessaire
			if (cibleExtremite && !srcExtremite)
			{
				int tmp = idSrc;
				idSrc   = idCible;
				idCible = tmp;
				nouvelleArete = new Arete(idSrc, idCible);
			}
		}

		// Vérifie le degré max 
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

		// Vérifie qu'il n'y a pas de croisement
		if (this.sectionCroise(idSrc, idCible, chemin))
		{
			this.afficherMessage("Les cables ne doivent pas se croiser");
			return false;
		}

		// Verifie que l'arete existe dans le graphe
		if (!this.areteExisteDansGraphe(idSrc, idCible))
		{
			this.afficherMessage("Ces stations ne sont pas reliables");
			return false;
		}

		// Ajoute l'arete
		chemin.add(nouvelleArete);

		// Marque les sommets comme visites
		if (visites.isEmpty())
		{
			visites.add(idSrc);
			String zoneSrc = this.idToZone.get(idSrc);
			if (zoneSrc != null)
			{
				this.zonesVisitesParJoueur.get(this.joueurCourantIdx).add(zoneSrc);
			}
		}

		visites.add(idCible);

		if (zoneCible != null)
		{
			this.zonesVisitesParJoueur.get(this.joueurCourantIdx).add(zoneCible);
		}

		this.sommetSelectionne = -1;

		int nbZones = this.zonesVisitesParJoueur.get(this.joueurCourantIdx).size();
		this.afficherMessage("Cable connecte ! Zones visitees: " + nbZones + "/5");

		this.mettreAJourInterface();
		return true;
	}

	// Calcule le degre dans le chemin
	private int getDegreDansChemin(int id, List<Arete> chemin)
	{
		int d = 0;
		for (Arete a : chemin)
		{
			if (a.contient(id))
			{
				d++;
			}
		}
		return d;
	}

	
	// Trouve les extremites du chemin
	private int[] trouverExtremites(List<Arete> chemin)
	{
		if (chemin.isEmpty())
		{
			return null;
		}

		Map<Integer, Integer> degres = new HashMap<>();

		for (Arete a : chemin)
		{
			int srcDeg = 0;
			if (degres.containsKey(a.getSrc()))
			{
				srcDeg = degres.get(a.getSrc());
			}
			degres.put(a.getSrc(), srcDeg + 1);

			int cibleDeg = 0;
			if (degres.containsKey(a.getCible()))
			{
				cibleDeg = degres.get(a.getCible());
			}
			degres.put(a.getCible(), cibleDeg + 1);
		}

		List<Integer> extremites = new ArrayList<>();

		for (Map.Entry<Integer, Integer> entry : degres.entrySet())
		{
			if (entry.getValue() == 1)
			{
				extremites.add(entry.getKey());
			}
		}

		if (extremites.size() == 2)
		{
			int[] result = new int[2];
			result[0]    = extremites.get(0);
			result[1]    = extremites.get(1);
			return result;
		}

		if (chemin.size() == 1)
		{
			int[] result  = new int[2];
			result[0]     = chemin.get(0).getSrc();
			result[1]     = chemin.get(0).getCible();
			return result;
		}

		return null;
	}

	
	//Verifie si deux segments se croisent
	private boolean sectionCroise(int idSrc, int idCible, List<Arete> chemin)
	{
		Sommet sSrc   = this.plateau.trouverSommet(idSrc);
		Sommet sCible = this.plateau.trouverSommet(idCible);

		if (sSrc == null || sCible == null)
		{
			return false;
		}

		double x1 = sSrc.getX();
		double y1 = sSrc.getY();
		double x2 = sCible.getX();
		double y2 = sCible.getY();

		for (Arete a : chemin)
		{
			if (a.contient(idSrc) || a.contient(idCible))
			{
				continue;
			}

			Sommet sA = this.plateau.trouverSommet(a.getSrc());
			Sommet sB = this.plateau.trouverSommet(a.getCible());

			if (sA == null || sB == null)
			{
				continue;
			}

			double x3 = sA.getX();
			double y3 = sA.getY();
			double x4 = sB.getX();
			double y4 = sB.getY();

			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
			{
				return true;
			}
		}

		return false;
	}

	
	// Verifie si l'arete existe  dans le graphe du plateau    
	private boolean areteExisteDansGraphe(int idSrc, int idCible)
	{
		for (Arete a : this.plateau.getAretes())
		{
			if (a.contient(idSrc) && a.contient(idCible))
			{
				return true;
			}
		}
		return false;
	}

	// Construire l'interface graphique
	private void construireInterface()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		this.frame = new JFrame("Operation Reseau Rouge");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panneauPrincipal = new JPanel(new BorderLayout(8, 8));
		panneauPrincipal.setBackground(new Color(30, 30, 40));
		panneauPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

		this.panneauPlateau = new PanneauPlateau();

		JPanel panneauInfo  = this.creerPanneauInfo();

		this.labelMessage = new JLabel(" ");
		this.labelMessage.setForeground(new Color(200, 200, 180));
		this.labelMessage.setFont(new Font("Serif", Font.ITALIC, 13));
		this.labelMessage.setBorder(new EmptyBorder(4, 8, 4, 8));

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panneauPrincipal.add(this.panneauPlateau, BorderLayout.CENTER);
		panneauPrincipal.add(panneauInfo, BorderLayout.EAST);
		panneauPrincipal.add(this.labelMessage, BorderLayout.SOUTH);

		/*-------------------------------*/
		/* Activation des composants     */
		/*-------------------------------*/
		this.frame.setContentPane(panneauPrincipal);
		this.frame.setSize(1200, 800);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}

	
	// Panneau d'informations
	private JPanel creerPanneauInfo()
	{
		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		JPanel panneau = new JPanel();
		panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
		panneau.setBackground(new Color(40, 40, 50));
		panneau.setBorder(new EmptyBorder(10, 10, 10, 10));
		panneau.setPreferredSize(new Dimension(280, 0));

		JLabel titre = new JLabel("🔴 OPERATION RESEAU ROUGE");
		titre.setForeground(new Color(220, 80, 80));
		titre.setFont(new Font("Serif", Font.BOLD, 14));
		titre.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.labelManche = new JLabel("Manche 1 / " + NB_MANCHES);
		this.labelManche.setForeground(new Color(200, 200, 160));
		this.labelManche.setFont(new Font("SansSerif", Font.BOLD, 13));

		this.labelJoueur = new JLabel("Agent: —");
		this.labelJoueur.setForeground(Color.WHITE);
		this.labelJoueur.setFont(new Font("SansSerif", Font.BOLD, 13));

		this.labelCarteActive = new JLabel("Carte: AUCUNE");
		this.labelCarteActive.setForeground(new Color(180, 220, 180));
		this.labelCarteActive.setFont(new Font("SansSerif", Font.BOLD, 13));

		this.panneauCartes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.panneauCartes.setBackground(new Color(40, 40, 50));
		this.panneauCartes.setBorder(BorderFactory.createTitledBorder("Cartes piochées"));
		this.panneauCartes.setMaximumSize(new Dimension(260, 200));
		this.panneauCartes.setPreferredSize(new Dimension(260, 180));

		JLabel lblScores = new JLabel("SCORES");
		lblScores.setForeground(new Color(220, 180, 80));
		lblScores.setFont(new Font("Serif", Font.BOLD, 13));

		this.zoneScores = new JTextArea(4, 18);
		this.zoneScores.setEditable(false);
		this.zoneScores.setBackground(new Color(30, 30, 40));
		this.zoneScores.setForeground(new Color(200, 200, 180));
		this.zoneScores.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.zoneScores.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 60)));

		JLabel lblZones = new JLabel("ZONES VISITEES");
		lblZones.setForeground(new Color(220, 180, 80));
		lblZones.setFont(new Font("Serif", Font.BOLD, 13));

		this.zoneZonesVisites = new JTextArea(5, 18);
		this.zoneZonesVisites.setEditable(false);
		this.zoneZonesVisites.setBackground(new Color(30, 30, 40));
		this.zoneZonesVisites.setForeground(new Color(200, 200, 180));
		this.zoneZonesVisites.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.zoneZonesVisites.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 60)));

		JButton btnPasser = this.creerBouton("⏭ Passer le tour", new Color(100, 70, 50));
		btnPasser.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton btnAnnulerSelection = this.creerBouton(" Annuler selection", new Color(120, 60, 60));
		btnAnnulerSelection.setAlignmentX(Component.CENTER_ALIGNMENT);

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
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
		panneau.add(btnAnnulerSelection);

		/*-------------------------------*/
		/* Activation des composants     */
		/*-------------------------------*/
		btnPasser.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				passerTour();
			}
		});

		btnAnnulerSelection.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sommetSelectionne = -1;
				afficherMessage("Selection annulée");
				mettreAJourInterface();
			}
		});

		return panneau;
	}

	/*-------------------------------*/
	/* Crée un bouton                */
	/*-------------------------------*/
	private JButton creerBouton(String texte, Color couleur)
	{
		JButton btn = new JButton(texte);
		btn.setBackground(couleur);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(new EmptyBorder(8, 12, 8, 12));
		btn.setFont(new Font("SansSerif", Font.BOLD, 12));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setMaximumSize(new Dimension(240, 40));
		return btn;
	}

	/*-------------------------------*/
	/* Met a jour l'interface        */
	/*-------------------------------*/
	private void mettreAJourInterface()
	{
		if (this.frame == null) return;

		// Met a jour les labels
		this.labelManche.setText("Manche " + this.mancheCourante + " / " + NB_MANCHES);

		String nomJoueur = "";
		if (this.joueurCourantIdx < this.nomsJoueurs.size())
		{
			nomJoueur = this.nomsJoueurs.get(this.joueurCourantIdx);
		}
		else
		{
			nomJoueur = "—";
		}
		this.labelJoueur.setText("Agent: " + nomJoueur);

		int couleurIndex = Math.min(this.joueurCourantIdx, COULEURS_JOUEURS.length - 1);
		this.labelJoueur.setForeground(COULEURS_JOUEURS[couleurIndex]);

		String nomCarte = "AUCUNE";
		if (!this.carteActive.isEmpty())
		{
			if (this.carteActiveEstJoker)
			{
				nomCarte = "🎴 JOKER !";
			}
			else
			{
				nomCarte = this.carteActive;
			}
		}
		this.labelCarteActive.setText("🎴 Carte: " + nomCarte);

		// Met a jour l'affichage des cartes piochées
		this.panneauCartes.removeAll();

		for (String carte : this.cartesRetournees)
		{
			Image img = this.getImageParCarte(carte);
			JLabel carteLabel = new JLabel();

			if (img != null)
			{
				// Redimensionne l'image pour qu'elle tienne bien
				Image imgRedim = img.getScaledInstance(50, 60, Image.SCALE_SMOOTH);
				carteLabel.setIcon(new ImageIcon(imgRedim));
				carteLabel.setHorizontalAlignment(JLabel.CENTER);
			}
			else
			{
				// Fallback si l'image n'existe pas
				carteLabel.setOpaque(true);
				carteLabel.setBackground(new Color(100, 100, 100));
				carteLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
				carteLabel.setPreferredSize(new Dimension(50, 60));
				carteLabel.setHorizontalAlignment(JLabel.CENTER);
				carteLabel.setForeground(Color.WHITE);
				carteLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

				if (carte.equals("JOKER"))
				{
					carteLabel.setText("🎴");
				}
				else
				{
					carteLabel.setText(carte.substring(0, 1));
				}
			}

			carteLabel.setToolTipText(carte);
			this.panneauCartes.add(carteLabel);
		}

		this.panneauCartes.revalidate();
		this.panneauCartes.repaint();

		// Met a jour les scores
		String texteScores = "";
		for (int i = 0; i < this.nomsJoueurs.size(); i++)
		{
			texteScores = texteScores + this.nomsJoueurs.get(i) + " : " + this.scores[i] + " pts\n";
		}
		this.zoneScores.setText(texteScores);

		// Met a jour les zones visitees
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
				for (String z : zones)
				{
					texteZones = texteZones + "  X " + z + "\n";
				}
			}
			texteZones = texteZones + "\nObjectif: visiter les 5 zones !";
			this.zoneZonesVisites.setText(texteZones);
		}

		// Redessine le plateau
		this.panneauPlateau.repaint();
	}

	
	// Affiche un message dans la  barre d'etat
	private void afficherMessage(String msg)
	{
		if (this.labelMessage != null)
		{
			this.labelMessage.setText(msg);
		}
		System.out.println(msg);
	}

	/*-------------------------------*/
	/* Panneau de dessin du plateau  */
	/*-------------------------------*/
	private class PanneauPlateau extends JPanel
	{
		private final int RAYON = 25;
		private final int SEUIL = 30;

		public PanneauPlateau()
		{
			this.setBackground(new Color(30, 30, 40));

			MouseAdapter monEcouteur = new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					gererClic(e.getX(), e.getY());
				}
			};

			this.addMouseListener(monEcouteur);
		}

		private void gererClic(int mx, int my)
		{
			if (partieTerminee) return;

			// Trouver le sommet clique
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
				int src = sommetSelectionne;
				int cible = idClique;
				sommetSelectionne = -1;
				tenterTracerRoute(src, cible);
			}
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Dessine le fond
			if (fondImage != null)
			{
				g2.drawImage(fondImage, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g2.setColor(new Color(40, 40, 50));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}

			// Dessine toutes les aretes possibles 
			g2.setColor(new Color(200, 180, 100)); 
			g2.setStroke(new BasicStroke(2f));
			for (Arete a : plateau.getAretes())
			{
				Sommet s1 = plateau.trouverSommet(a.getSrc());
				Sommet s2 = plateau.trouverSommet(a.getCible());
				if (s1 != null && s2 != null)
				{
					g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
				}
			}

			// Dessine les chemins des joueurs
			for (int i = 0; i < cheminsParJoueur.size(); i++)
			{
				int couleurIndex = Math.min(i, COULEURS_JOUEURS.length - 1);
				Color couleur = COULEURS_JOUEURS[couleurIndex];

				g2.setColor(couleur);
				g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				for (Arete a : cheminsParJoueur.get(i))
				{
					Sommet s1 = plateau.trouverSommet(a.getSrc());
					Sommet s2 = plateau.trouverSommet(a.getCible());
					if (s1 != null && s2 != null)
					{
						g2.drawLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
					}
				}
			}

			// Dessine les sommets
			for (Sommet s : plateau.getSommets())
			{
				Color couleur = Sommet.getCouleurParType(s.getTypeZone());

			
				if (s.getId() == sommetSelectionne)
				{
					g2.setColor(new Color(255, 255, 150, 100));
					g2.fillOval(s.getX() - RAYON - 5, s.getY() - RAYON - 5,
							   (RAYON + 5) * 2, (RAYON + 5) * 2);
				}

				// Cercle interieur
				g2.setColor(couleur);
				g2.fillOval(s.getX() - RAYON, s.getY() - RAYON, RAYON * 2, RAYON * 2);

				// Lettre au centre
				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 14));
				String lettre = s.getPremiereLettre();
				int textX = s.getX() - g2.getFontMetrics().stringWidth(lettre) / 2;
				g2.drawString(lettre, textX, s.getY() + 6);

				// Contour blanc
				g2.setColor(new Color(255, 255, 255, 80));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawOval(s.getX() - RAYON, s.getY() - RAYON, RAYON * 2, RAYON * 2);

				// Petit texte avec l'id
				g2.setColor(new Color(200, 200, 200, 150));
				g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
				g2.drawString(String.valueOf(s.getId()), s.getX() - 4, s.getY() - RAYON - 3);
			}
		}
	}
}