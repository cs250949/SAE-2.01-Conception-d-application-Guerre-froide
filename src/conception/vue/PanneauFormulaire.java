package conception.vue;

import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class PanneauFormulaire extends JPanel
{
	/*----------------------------*/
	/* Attributs                  */
	/*----------------------------*/
	private JSpinner             spLargeur;
	private JSpinner             spHauteur;
	private JSpinner             spTailleCase;
	private JComboBox<String>    comboTypes;
	private JLabel               labelStatut;
	private ControleurConception ctrl;

	/*----------------------------*/
	/* Constructeur               */
	/*----------------------------*/
	public PanneauFormulaire(ControleurConception ctrl)
	{
		this.ctrl = ctrl;
		this.construire();
	}

	/*----------------------------*/
	/* Construction interface     */
	/*----------------------------*/
	private void construire()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(new Color(30, 30, 40));
		this.setBorder(new EmptyBorder(10, 10, 10, 10));

		/* ------------------------------------------------------- */
		/*  CONFIGURATION DES DIMENSIONS (Lignes, Colonnes, Cases) */
		/* ------------------------------------------------------- */
		JPanel panParams = new JPanel(new GridBagLayout());
		panParams.setBackground(new Color(30, 30, 40));
		panParams.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
			" Configuration du Plateau ", TitledBorder.LEFT, TitledBorder.TOP,
			new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		/* Saisie des Colonnes (Largeur) */
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
		JLabel lblL = new JLabel("Colonnes :");
		lblL.setForeground(Color.LIGHT_GRAY);
		panParams.add(lblL, gbc);

		gbc.gridx = 1; gbc.weightx = 0.6;
		spLargeur = new JSpinner(new SpinnerNumberModel(7, 3, 30, 1));
		panParams.add(spLargeur, gbc);

		/* Saisie des Lignes (Hauteur) */
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.4;
		JLabel lblH = new JLabel("Lignes :");
		lblH.setForeground(Color.LIGHT_GRAY);
		panParams.add(lblH, gbc);

		gbc.gridx = 1; gbc.weightx = 0.6;
		spHauteur = new JSpinner(new SpinnerNumberModel(7, 3, 30, 1));
		panParams.add(spHauteur, gbc);

		/* Saisie de la Taille des Cases */
		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.4;
		JLabel lblT = new JLabel("Taille Case :");
		lblT.setForeground(Color.LIGHT_GRAY);
		panParams.add(lblT, gbc);

		gbc.gridx = 1; gbc.weightx = 0.6;
		spTailleCase = new JSpinner(new SpinnerNumberModel(60, 20, 120, 5));
		panParams.add(spTailleCase, gbc);

		/* LE BOUTON GÉNÉRER */
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
		JButton btnGenerer = new JButton("Générer le Plateau");
		btnGenerer.setBackground(new Color(75, 75, 100));
		btnGenerer.setForeground(Color.WHITE);
		btnGenerer.setFocusPainted(false);
		btnGenerer.setFont(new Font("SansSerif", Font.BOLD, 11));
		btnGenerer.addActionListener(e -> executerGeneration());
		panParams.add(btnGenerer, gbc);

		this.add(panParams);
		this.add(Box.createVerticalStrut(15));

		/* ------------------------------------------------------- */
		/*  OUTILS D'ÉDITION                                       */
		/* ------------------------------------------------------- */
		JPanel panOutils = new JPanel(new GridBagLayout());
		panOutils.setBackground(new Color(30, 30, 40));
		panOutils.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
			" Outils d'édition ", TitledBorder.LEFT, TitledBorder.TOP,
			new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));

		GridBagConstraints gbcO = new GridBagConstraints();
		gbcO.insets = new Insets(5, 5, 5, 5);
		gbcO.anchor = GridBagConstraints.WEST;

		gbcO.gridx = 0; gbcO.gridy = 0; gbcO.weightx = 0.4;
		JLabel lblType = new JLabel("Infrastructure :");
		lblType.setForeground(Color.LIGHT_GRAY);
		panOutils.add(lblType, gbcO);

		gbcO.gridx = 1; gbcO.weightx = 0.6;
		String[] types = { "HOPITAL", "FERME", "PETROLIER", "PORT", "TANK", "BASE_DEPART", "VIDE" };
		comboTypes = new JComboBox<>(types);
		comboTypes.addActionListener(e -> {
			if (ctrl != null) ctrl.setTypeCourant((String) comboTypes.getSelectedItem());
		});
		panOutils.add(comboTypes, gbcO);

		/* Label de suivi de la taille actuelle */
		gbcO.gridx = 0; gbcO.gridy = 1; gbcO.gridwidth = 2;
		labelStatut = new JLabel("Plateau actuel : 7 × 7 | Case : 60px");
		labelStatut.setForeground(new Color(150, 220, 150));
		labelStatut.setFont(new Font("SansSerif", Font.ITALIC, 11));
		panOutils.add(labelStatut, gbcO);

		this.add(panOutils);
		this.add(Box.createVerticalGlue());
	}

	/*----------------------------*/
	/* Génération du plateau      */
	/*----------------------------*/
	private void executerGeneration()
	{
		int largeur    = (int) spLargeur.getValue();
		int hauteur    = (int) spHauteur.getValue();
		int tailleCase = (int) spTailleCase.getValue();

		if (ctrl != null)
		{
			ctrl.redimensionnerPlateau(hauteur, largeur, tailleCase);

			labelStatut.setText("Plateau actuel : " + largeur + " × " + hauteur + " | Case : " + tailleCase + "px");
		}
	}

	/*----------------------------*/
	/* Accesseurs                 */
	/*----------------------------*/
	public int getTailleCase()
	{
		return (int) spTailleCase.getValue();
	}
}