package conception.vue;

import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;

public class FenetreConception extends JFrame
{
	private ControleurConception controleur;
	private PanneauGrille        panneauGrille;
	private PanneauFormulaire    panneauFormulaire;
	private BarreStatut          barreStatut;

	public FenetreConception()
	{
		super("Opération Réseau Rouge — Éditeur de plateau");

		this.controleur = new ControleurConception(7, 7, 4, 4);

		construireInterface();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(980, 700));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void construireInterface()
	{
		setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(30, 30, 40));
		setJMenuBar(creerMenuBar());

		JPanel panneauGauche = new JPanel();
		panneauGauche.setLayout(new BoxLayout(panneauGauche, BoxLayout.Y_AXIS));
		panneauGauche.setBackground(new Color(40, 40, 52));
		panneauGauche.setPreferredSize(new Dimension(240, 0));

		panneauFormulaire = new PanneauFormulaire(controleur);
		panneauGauche.add(panneauFormulaire);

		panneauGrille = new PanneauGrille(controleur, new PanneauGrille.CelluleListener()
		{
			
			public void CelluleCliquee(int ligne, int colonne)
			{
				controleur.CelluleCliquee(ligne, colonne);
				panneauGrille.mettreAJourIcons();
			}

			
			public void CelluleSurvolee(int ligne, int colonne)
			{
				controleur.CelluleSurvolee(ligne, colonne);
			}
		});

		barreStatut = new BarreStatut();

		JScrollPane scrollGrille = new JScrollPane(panneauGrille);
		scrollGrille.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));

		JScrollPane scrollGauche = new JScrollPane(panneauGauche);
		scrollGauche.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
		scrollGauche.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollGauche.setPreferredSize(new Dimension(250, 0));

		add(scrollGauche, BorderLayout.WEST);
		add(scrollGrille, BorderLayout.CENTER);
		add(barreStatut,  BorderLayout.SOUTH);

		controleur.setVues(panneauGrille, barreStatut);
	}

	private JMenuBar creerMenuBar()
	{
		JMenuBar mb               = new JMenuBar();

		JMenu menuFichier         = new JMenu("Fichier");
		JMenuItem miSauvegarder   = new JMenuItem("Sauvegarder");
		JMenuItem miCharger       = new JMenuItem("Charger");
		JMenuItem miReinitialiser = new JMenuItem("Réinitialiser");
		JMenuItem miQuitter       = new JMenuItem("Quitter");

		miSauvegarder  .addActionListener(e -> controleur.sauvegarderPlateau());
		miCharger      .addActionListener(e -> controleur.chargerPlateau());
		miReinitialiser.addActionListener(e -> controleur.reinitialiser());
		miQuitter      .addActionListener(e -> controleur.quitter());

		menuFichier.add(miSauvegarder);
		menuFichier.add(miCharger);
		menuFichier.add(miReinitialiser);
		menuFichier.addSeparator();
		menuFichier.add(miQuitter);

		JMenu     menuJeu      = new JMenu("Jeu");
		JMenuItem miLancer     = new JMenuItem("Lancer la partie");
		miLancer.addActionListener(e -> controleur.lancerJeu());
		menuJeu.add(miLancer);

		mb.add(menuFichier);
		mb.add(menuJeu);
		return mb;
	}

	public static void main(String[] args)
	{
	    FenetreConception f = new FenetreConception();
	}
}