package conception.vue;

import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class PanneauFormulaire extends JPanel
{
	private JSpinner             spLargeur;
	private JSpinner             spHauteur;
	private JSpinner             spNbCouleur;
	private JComboBox<Integer>   comboZones;
	private JComboBox<String>    comboTypes;
	private JLabel               labelStatut;
	private JLabel               labelDescZones;
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
		//  CONFIGURATION DU NOUVEAU PLATEAU
		// -------------------------------------------------------
		JPanel panParams = new JPanel(new GridBagLayout());
		panParams.setBackground(new Color(30, 30, 40));
		panParams.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 120)),
			"Nouveau plateau",
			TitledBorder.LEFT, TitledBorder.TOP,
			new Font("SansSerif", Font.BOLD, 11),
			new Color(220, 180, 80)
		));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 6, 4, 6);
		gbc.fill   = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		spLargeur   = new JSpinner(new SpinnerNumberModel(7, 3, 20, 1));
		spHauteur   = new JSpinner(new SpinnerNumberModel(7, 3, 20, 1));
		spNbCouleur = new JSpinner(new SpinnerNumberModel(4, 2, 8,  1));

		String[]   etiquettes = { "Largeur (colonnes) :", "Hauteur (lignes) :", "Nb couleurs :" };
		JSpinner[] spinners   = { spLargeur, spHauteur, spNbCouleur };

		for (int i = 0; i < etiquettes.length; i++)
		{
			gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.6;
			JLabel lbl = new JLabel(etiquettes[i]);
			lbl.setForeground(Color.LIGHT_GRAY);
			lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
			panParams.add(lbl, gbc);

			gbc.gridx = 1; gbc.weightx = 0.4;
			spinners[i].setPreferredSize(new Dimension(70, 26));
			panParams.add(spinners[i], gbc);
		}

		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.6;
		JLabel lblZones = new JLabel("Nombre de zones :");
		lblZones.setForeground(Color.LIGHT_GRAY);
		lblZones.setFont(new Font("SansSerif", Font.PLAIN, 11));
		panParams.add(lblZones, gbc);

		gbc.gridx = 1; gbc.weightx = 0.4;
		Integer[] zones = { 2, 3, 4 };
		comboZones = new JComboBox<>(zones);
		comboZones.setSelectedItem(4);
		comboZones.setPreferredSize(new Dimension(70, 26));
		comboZones.addActionListener(e -> 
		{
			int nb = (Integer) comboZones.getSelectedItem();
			ctrl.setNbZonesActives(nb);
			labelDescZones.setText(descriptionZones(nb));
		});
		panParams.add(comboZones, gbc);

		this.add(panParams);
		this.add(Box.createVerticalStrut(5));

		labelDescZones = new JLabel(descriptionZones(4));
		labelDescZones.setForeground(new Color(200, 200, 150));
		labelDescZones.setFont(new Font("SansSerif", Font.ITALIC, 10));
		labelDescZones.setAlignmentX(CENTER_ALIGNMENT);
		this.add(labelDescZones);
		this.add(Box.createVerticalStrut(8));

		JPanel panBouton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
		panBouton.setBackground(new Color(30, 30, 40));

		JButton btnGenerer = new JButton("↻ Générer");
		btnGenerer.setBackground(new Color(60, 100, 60));
		btnGenerer.setForeground(Color.WHITE);
		btnGenerer.setFocusPainted(false);
		btnGenerer.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnGenerer.setPreferredSize(new Dimension(120, 32));
		btnGenerer.addActionListener(e -> executerGeneration());
		panBouton.add(btnGenerer);
		this.add(panBouton);

		labelStatut = new JLabel(" ");
		labelStatut.setForeground(new Color(180, 220, 140));
		labelStatut.setFont(new Font("SansSerif", Font.ITALIC, 10));
		labelStatut.setHorizontalAlignment(SwingConstants.CENTER);
		labelStatut.setAlignmentX(CENTER_ALIGNMENT);
		this.add(labelStatut);

		this.add(Box.createVerticalStrut(15));

		// -------------------------------------------------------
		// ÉDITION ET PLACEMENT DES INFRAS
		// -------------------------------------------------------
		JPanel panOutils = new JPanel(new GridBagLayout());
		panOutils.setBackground(new Color(35, 35, 45));
		panOutils.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(100, 100, 120)),
			"Infrastructures des Sommets",
			TitledBorder.LEFT, TitledBorder.TOP,
			new Font("SansSerif", Font.BOLD, 11),
			new Color(220, 100, 100)
		));

		GridBagConstraints gbcO = new GridBagConstraints();
		gbcO.insets = new Insets(6, 6, 6, 6);
		gbcO.fill   = GridBagConstraints.HORIZONTAL;
		gbcO.anchor = GridBagConstraints.WEST;

		gbcO.gridx = 0; gbcO.gridy = 0; gbcO.weightx = 0.4;
		JLabel lblType = new JLabel("Infrastructure :");
		lblType.setForeground(Color.LIGHT_GRAY);
		lblType.setFont(new Font("SansSerif", Font.PLAIN, 11));
		panOutils.add(lblType, gbcO);

		gbcO.gridx = 1; gbcO.weightx = 0.6;
		String[] types = { "HOPITAL", "FERME", "PETROLIER", "PORT", "TANK", "BASE_DEPART", "VIDE" };
		comboTypes = new JComboBox<>(types);
		comboTypes.setPreferredSize(new Dimension(110, 26));
		comboTypes.addActionListener(e -> 
			ctrl.setTypeCourant((String) comboTypes.getSelectedItem()));
		panOutils.add(comboTypes, gbcO);

		this.add(panOutils);
		this.add(Box.createVerticalGlue());
	}

	private void executerGeneration()
	{
		int largeur = (int) spLargeur.getValue();
		int hauteur = (int) spHauteur.getValue();
		ctrl.redimensionnerPlateau(hauteur, largeur);
		labelStatut.setText("Plateau : " + largeur + " × " + hauteur);
	}

	private String descriptionZones(int nb)
	{
		switch (nb)
		{
			case 2:  return "<html>Actives : <b>Ouest · Est</b></html>";
			case 3:  return "<html>Actives : <b>Ouest · Est · Non-Aligné</b></html>";
			default: return "<html>Actives : <b>Ouest · Est · Non-Aligné · Chinois</b></html>";
		}
	}

	public int getLargeur  ()   { return (int) spLargeur  .getValue();   }
	public int getHauteur  ()   { return (int) spHauteur  .getValue();   }
	public int getNbCouleur()   { return (int) spNbCouleur.getValue();   }
}