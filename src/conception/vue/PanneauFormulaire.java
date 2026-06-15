package conception.vue;

import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class PanneauFormulaire extends JPanel
{
	private JSpinner             spLargeur;
	private JSpinner             spHauteur;
	private JComboBox<String>    comboTypes;
	private JLabel               labelStatut;
	private ControleurConception ctrl;

	public PanneauFormulaire(ControleurConception ctrl)
	{
		this.ctrl = ctrl;
		this.construire();
	}

	private void construire()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(new Color(30, 30, 40));
		this.setBorder(new EmptyBorder(10, 10, 10, 10));

		// -------------------------------------------------------
		//  CONFIGURATION DES DIMENSIONS (Lignes et Colonnes)
		// -------------------------------------------------------
		JPanel panParams = new JPanel(new GridBagLayout());
		panParams.setBackground(new Color(30, 30, 40));
		panParams.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
			" Configuration du Plateau ", TitledBorder.LEFT, TitledBorder.TOP,
			new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Saisie des Colonnes (Largeur)
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
		JLabel lblL = new JLabel("Colonnes :"); 
		lblL.setForeground(Color.LIGHT_GRAY);
		panParams.add(lblL, gbc);
		
		gbc.gridx = 1; gbc.weightx = 0.6;
		spLargeur = new JSpinner(new SpinnerNumberModel(7, 3, 30, 1));
		panParams.add(spLargeur, gbc);

		// Saisie des Lignes (Hauteur)
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.4;
		JLabel lblH = new JLabel("Lignes :"); 
		lblH.setForeground(Color.LIGHT_GRAY);
		panParams.add(lblH, gbc);
		
		gbc.gridx = 1; gbc.weightx = 0.6;
		spHauteur = new JSpinner(new SpinnerNumberModel(7, 3, 30, 1));
		panParams.add(spHauteur, gbc);

		// LE BOUTON GÉNÉRER
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
		JButton btnGenerer = new JButton("Générer le Plateau");
		btnGenerer.setBackground(new Color(75, 75, 100));
		btnGenerer.setForeground(Color.WHITE);
		btnGenerer.setFocusPainted(false);
		btnGenerer.setFont(new Font("SansSerif", Font.BOLD, 11));
		btnGenerer.addActionListener(e -> executerGeneration());
		panParams.add(btnGenerer, gbc);

		this.add(panParams);
		this.add(Box.createVerticalStrut(15));

		// -------------------------------------------------------
		//  OUTILS D'ÉDITION 
		// -------------------------------------------------------
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

		// Label de suivi de la taille actuelle
		gbcO.gridx = 0; gbcO.gridy = 1; gbcO.gridwidth = 2;
		labelStatut = new JLabel("Plateau actuel : 7 × 7");
		labelStatut.setForeground(new Color(150, 220, 150));
		labelStatut.setFont(new Font("SansSerif", Font.ITALIC, 11));
		panOutils.add(labelStatut, gbcO);

		this.add(panOutils);
		this.add(Box.createVerticalGlue());
	}

	/**
	 * Déclenchée lors du clic sur le bouton "Générer"
	 */
	private void executerGeneration()
	{
		int largeur = (int) spLargeur.getValue(); // Récupère les colonnes
		int hauteur = (int) spHauteur.getValue(); // Récupère les lignes
		
		if (ctrl != null) 
		{
			// Envoie l'ordre au contrôleur de détruire l'ancien graphe et d'en faire un nouveau
			ctrl.redimensionnerPlateau(hauteur, largeur);
			
			// Met à jour le texte sous les outils
			labelStatut.setText("Plateau actuel : " + largeur + " × " + hauteur);
		}
	}
}
