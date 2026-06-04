package ihm;

import controleur.Controleur;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelConfig extends JPanel implements ActionListener
{
	private Controleur ctrl;
	private JTextField txtNbZones;
	private JTextField txtNbSoldats;
	private JTextField txtNbHauteurs;
	private JTextField txtNbLargeurs;
	private JButton    btnValider;
	private JButton    btnAnnuler;
	private JButton    btnRegles;
	private Image      imageFond;

	public PanelConfig(Controleur ctrl)
	{
		this.ctrl = ctrl;
		// GridBagLayout sur le panneau principal pour centrer parfaitement la "petite fenêtre" au milieu
		this.setLayout(new GridBagLayout());
		this.imageFond = Toolkit.getDefaultToolkit().getImage("src/images/fond_menu.png");

		// 1. CRÉATION DE LA "PETITE FENÊTRE" DE FORMULAIRE
		JPanel boitierCentral = new JPanel() 
		{
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				// Fond noir semi-transparent (80% d'opacité) pour le style bunker
				g2d.setColor(new Color(15, 15, 15, 210));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				// Bordure vert fluo style écran cathodique
				g2d.setColor(new Color(0, 255, 65));
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		};
		boitierCentral.setLayout(new BorderLayout());
		boitierCentral.setBorder(new EmptyBorder(25, 25, 25, 25));
		boitierCentral.setOpaque(false);

		// 2. TITRE DU FORMULAIRE
		JLabel lblTitre = new JLabel("PARAMÈTRES DE LA MISSION", SwingConstants.CENTER);
		lblTitre.setFont(new Font("Courier New", Font.BOLD, 18));
		lblTitre.setForeground(new Color(0, 255, 65)); // Vert matrice
		lblTitre.setBorder(new EmptyBorder(0, 0, 20, 0));
		boitierCentral.add(lblTitre, BorderLayout.NORTH);

		// 3. GRILLE DES CHAMPS DE SAISIE 
		JPanel panelGrille = new JPanel(new GridLayout(4, 2, 15, 15));
		panelGrille.setOpaque(false);

		Font policeChamps = new Font("Courier New", Font.BOLD, 13);
		Color vertMatrice = new Color(0, 255, 65);

		JLabel lblNbZones = new JLabel("Nombre de zones :");
		lblNbZones.setFont(policeChamps);
		lblNbZones.setForeground(vertMatrice);
		this.txtNbZones = new JTextField(8);
		 configurerChampTexte(this.txtNbZones);

		JLabel lblNbSoldats = new JLabel("Nombre de soldats :");
		lblNbSoldats.setFont(policeChamps);
		lblNbSoldats.setForeground(vertMatrice);
		this.txtNbSoldats = new JTextField(8);
		configurerChampTexte(this.txtNbSoldats);

		JLabel lblNbHauteurs = new JLabel("Hauteur grille :");
		lblNbHauteurs.setFont(policeChamps);
		lblNbHauteurs.setForeground(vertMatrice);
		this.txtNbHauteurs = new JTextField(8);
		configurerChampTexte(this.txtNbHauteurs);

		JLabel lblNbLargeurs = new JLabel("Largeur grille :");
		lblNbLargeurs.setFont(policeChamps);
		lblNbLargeurs.setForeground(vertMatrice);
		this.txtNbLargeurs = new JTextField(8);
		configurerChampTexte(this.txtNbLargeurs);

		panelGrille.add(lblNbZones);    panelGrille.add(this.txtNbZones);
		panelGrille.add(lblNbSoldats);  panelGrille.add(this.txtNbSoldats);
		panelGrille.add(lblNbHauteurs); panelGrille.add(this.txtNbHauteurs);
		panelGrille.add(lblNbLargeurs); panelGrille.add(this.txtNbLargeurs);

		boitierCentral.add(panelGrille, BorderLayout.CENTER);

		// 4. LES BOUTONS DU FORMULAIRE
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		panelButtons.setOpaque(false);
		panelButtons.setBorder(new EmptyBorder(25, 0, 0, 0));

		this.btnValider = new JButton("LANCER");
		this.btnAnnuler = new JButton("EFFACER");
		this.btnRegles  = new JButton("RÈGLES");

		configurerBouton(this.btnValider);
		configurerBouton(this.btnAnnuler);
		configurerBouton(this.btnRegles);

		this.btnValider.addActionListener(this);
		this.btnAnnuler.addActionListener(this);
		this.btnRegles.addActionListener(this);

		panelButtons.add(this.btnValider);
		panelButtons.add(this.btnAnnuler);
		panelButtons.add(this.btnRegles);

		boitierCentral.add(panelButtons, BorderLayout.SOUTH);

		
		this.add(boitierCentral, new GridBagConstraints());
	}

	private void configurerChampTexte(JTextField tf)
	{
		tf.setFont(new Font("Courier New", Font.BOLD, 13));
		tf.setBackground(new Color(20, 20, 20));
		tf.setForeground(Color.WHITE);
		tf.setCaretColor(new Color(0, 255, 65)); // Curseur vert
		tf.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 45), 1));
	}

	private void configurerBouton(JButton btn)
	{
		btn.setFont(new Font("Courier New", Font.BOLD, 12));
		btn.setBackground(new Color(10, 10, 10));
		btn.setForeground(new Color(0, 255, 65));
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 65), 1));
		btn.setPreferredSize(new Dimension(100, 30));
		
		// Effet de survol de la souris (Hover) très simple
		btn.addMouseListener(new MouseAdapter() 
		{
			public void mouseEntered(MouseEvent e) 
			{
				btn.setBackground(new Color(0, 255, 65));
				btn.setForeground(Color.BLACK);
			}
			public void mouseExited(MouseEvent e) 
			{
				btn.setBackground(new Color(10, 10, 10));
				btn.setForeground(new Color(0, 255, 65));
			}
		});
	}

	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		if (this.imageFond != null) 
		{
			g.drawImage(this.imageFond, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnValider) 
		{
			this.validerConfig();
		} 
		else if (e.getSource() == this.btnAnnuler) 
		{
			this.annulerConfig();
		} 
		else if (e.getSource() == this.btnRegles) 
		{
			this.ouvrirRegles();
		}
	}

	private void validerConfig()
	{
		this.ctrl.demarrerPartie();
	}

	private void ouvrirRegles()
	{
		try 
		{
			File fichierPdf = new File("regles du jeu.pdf"); 
			if (fichierPdf.exists()) 
			{
				Desktop.getDesktop().open(fichierPdf);
			} 
			else 
			{
				JOptionPane.showMessageDialog(this, "Le fichier 'regles du jeu.pdf' est introuvable.");
			}
		} 
		catch (Exception ex) 
		{
			System.out.println("Impossible d'ouvrir le PDF : " + ex.getMessage());
		}
	}

	private void annulerConfig()
	{
		this.txtNbZones.setText("");
		this.txtNbSoldats.setText("");
		this.txtNbHauteurs.setText("");
		this.txtNbLargeurs.setText("");
	}
}