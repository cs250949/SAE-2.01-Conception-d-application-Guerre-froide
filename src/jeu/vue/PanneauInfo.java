package jeu.vue;

import jeu.controleur.ControleurJeu;

import java.awt.*;
import javax.swing.*;

public class PanneauInfo extends JPanel
{
	/*----------------------------*/
	/* Constantes                 */
	/*----------------------------*/
	private static final Color COULEUR_FOND    = new Color(40, 44, 52);
	private static final Color COULEUR_TEXTE   = new Color(200, 220, 255);

	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private ControleurJeu controleur;

	private JLabel        lblManche;
	private JLabel        lblJoueur;
	private JLabel        lblCarte;
	private JTextArea     txtScores;
	private JLabel        lblPosition;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public PanneauInfo(ControleurJeu controleur)
	{
		this.controleur = controleur;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setPreferredSize(new Dimension(260, 350));
		this.setMaximumSize(new Dimension(260, 400));
		this.setBackground(COULEUR_FOND);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		/* Titre */
		JLabel lblTitre = new JLabel("INFORMATIONS", SwingConstants.CENTER);
		lblTitre.setFont(new Font("Arial", Font.BOLD, 14));
		lblTitre.setForeground(Color.WHITE);
		lblTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(lblTitre);

		this.add(Box.createVerticalStrut(10));

		/* Manche */
		this.lblManche = new JLabel("Manche : 1 / 4");
		this.lblManche.setFont(new Font("Arial", Font.BOLD, 12));
		this.lblManche.setForeground(COULEUR_TEXTE);
		this.lblManche.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.lblManche);

		this.add(Box.createVerticalStrut(5));

		/* Joueur courant */
		this.lblJoueur = new JLabel("Joueur : ???");
		this.lblJoueur.setFont(new Font("Arial", Font.BOLD, 12));
		this.lblJoueur.setForeground(new Color(255, 200, 100));
		this.lblJoueur.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.lblJoueur);

		this.add(Box.createVerticalStrut(5));

		/* Carte active */
		this.lblCarte = new JLabel("Carte : Aucune");
		this.lblCarte.setFont(new Font("Arial", Font.ITALIC, 12));
		this.lblCarte.setForeground(COULEUR_TEXTE);
		this.lblCarte.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.lblCarte);

		this.add(Box.createVerticalStrut(10));

		/* Scores */
		JLabel lblScoresTitre = new JLabel("SCORES");
		lblScoresTitre.setFont(new Font("Arial", Font.BOLD, 12));
		lblScoresTitre.setForeground(Color.WHITE);
		lblScoresTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(lblScoresTitre);

		this.txtScores = new JTextArea(6, 25);
		this.txtScores.setEditable(false);
		this.txtScores.setBackground(new Color(30, 34, 42));
		this.txtScores.setForeground(new Color(180, 200, 230));
		this.txtScores.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.txtScores.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JScrollPane scrollScores = new JScrollPane(this.txtScores);
		scrollScores.setPreferredSize(new Dimension(240, 120));
		scrollScores.setMaximumSize(new Dimension(240, 120));
		scrollScores.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(scrollScores);

		this.add(Box.createVerticalStrut(10));

		/* Position survolée */
		this.lblPosition = new JLabel("Survol : -");
		this.lblPosition.setFont(new Font("Arial", Font.PLAIN, 10));
		this.lblPosition.setForeground(new Color(150, 150, 170));
		this.lblPosition.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.lblPosition);
	}

	/*----------------------------*/
	/* Mise à jour                */
	/*----------------------------*/
	public void rafraichir()
	{
		if (controleur == null) return;

		int manche = controleur.getMancheCourante();
		this.lblManche.setText("Manche : " + manche + " / 4");

		this.lblJoueur.setText("Joueur : " + controleur.getJoueurCourant());

		int[] scores = controleur.getScores();
		java.util.List<String> noms = controleur.getNomsJoueurs();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < noms.size(); i++)
		{
			sb.append(String.format("%-15s : %3d pts\n", noms.get(i), scores[i]));
		}
		this.txtScores.setText(sb.toString());
	}

	public void mettreAJourCarte(String carte, boolean estJoker)
	{
		String texte = "Carte : " + carte;
		if (estJoker) { texte += " (JOKER)"; }
		this.lblCarte.setText(texte);
	}

	public void mettreAJourTour(String joueur, int idx)
	{
		this.lblJoueur.setText("Joueur : " + joueur);
	}

	public void mettreAJourManche(int manche)
	{
		this.lblManche.setText("Manche : " + manche + " / 4");
	}

	public void setPosition(int ligne, int colonne)
	{
		this.lblPosition.setText("Survol : (" + ligne + ", " + colonne + ")");
	}

	public void setMessage(String msg)
	{
		System.out.println("[Info] " + msg);
	}

	public void afficherClassement(String classement)
	{
		JOptionPane.showMessageDialog(this, classement, "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
	}
}