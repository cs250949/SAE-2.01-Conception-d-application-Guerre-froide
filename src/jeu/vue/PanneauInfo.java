package jeu.vue;

import java.awt.*;
import javax.swing.*;
import jeu.controleur.ControleurJeu;

public class PanneauInfo extends JPanel
{
	private ControleurJeu controleur;
	
	private JLabel        lblManche;
	private JLabel        lblCarteActive;
	private JTextArea     txtScores;
	
	private JButton       btnPiocher;
	private JButton       btnPasser;

	private JPanel        pnlSwitchAgents;
	private JButton       btnAgentAlpha;
	private JButton       btnAgentBravo;
	private JButton       btnAgentCharlie;
	private JButton       btnAgentDelta;

	private boolean       estModeDemo;

	public PanneauInfo(ControleurJeu controleur, boolean estModeDemo)
	{
		this.controleur = controleur;
		this.estModeDemo = estModeDemo;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setPreferredSize(new Dimension(280, 750));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		/*-------------------------------------------------*/
		/* Statut Global                                   */
		/*-------------------------------------------------*/
		JPanel pnlStatut = new JPanel(new BorderLayout());
		pnlStatut.setBorder(BorderFactory.createTitledBorder(" Statut Global "));
		
		lblManche = new JLabel("Manche : 1 / 4", JLabel.CENTER);
		lblManche.setFont(new Font("Arial", Font.BOLD, 12));
		
		lblCarteActive = new JLabel("Pas de carte", JLabel.CENTER);
		lblCarteActive.setHorizontalTextPosition(JLabel.CENTER);
		lblCarteActive.setVerticalTextPosition(JLabel.BOTTOM);
		lblCarteActive.setFont(new Font("Arial", Font.ITALIC, 12));
		
		pnlStatut.add(lblManche, BorderLayout.NORTH);
		pnlStatut.add(lblCarteActive, BorderLayout.CENTER);
		
		this.add(pnlStatut);
		this.add(Box.createVerticalStrut(10));

		/*-------------------------------------------------*/
		/* Scores en Temps Réel                            */
		/*-------------------------------------------------*/
		JPanel pnlScores = new JPanel(new BorderLayout());
		pnlScores.setBorder(BorderFactory.createTitledBorder(" Scores en Temps Réel "));
		txtScores = new JTextArea(5, 20);
		txtScores.setEditable(false);
		txtScores.setBackground(new Color(240, 240, 240)); 
		txtScores.setForeground(new Color(30, 30, 30));     
		txtScores.setFont(new Font("Monospaced", Font.BOLD, 12));
		pnlScores.add(new JScrollPane(txtScores), BorderLayout.CENTER);
		this.add(pnlScores);
		this.add(Box.createVerticalStrut(10));

		/*-------------------------------------------------*/
		/* Sélection du Réseau Agent                       */
		/*-------------------------------------------------*/
		if (estModeDemo) 
		{
			pnlSwitchAgents = new JPanel(new GridLayout(2, 2, 5, 5));
			pnlSwitchAgents.setBorder(BorderFactory.createTitledBorder(" Sélection du Réseau Agent "));
			
			btnAgentAlpha   = new JButton("ALPHA");
			btnAgentBravo   = new JButton("BRAVO");
			btnAgentCharlie = new JButton("CHARLIE");
			btnAgentDelta   = new JButton("DELTA");

			Color couleurBouton = new Color(70, 85, 105);
			for (JButton btn : new JButton[]{btnAgentAlpha, btnAgentBravo, btnAgentCharlie, btnAgentDelta}) {
				btn.setBackground(couleurBouton);
				btn.setForeground(Color.WHITE);
				btn.setFont(new Font("Arial", Font.BOLD, 11));
			}

			btnAgentAlpha.addActionListener(e -> ouvrirFenetreDemo("ALPHA"));
			btnAgentBravo.addActionListener(e -> ouvrirFenetreDemo("BRAVO"));
			btnAgentCharlie.addActionListener(e -> ouvrirFenetreDemo("CHARLIE"));
			btnAgentDelta.addActionListener(e -> ouvrirFenetreDemo("DELTA"));

			pnlSwitchAgents.add(btnAgentAlpha);
			pnlSwitchAgents.add(btnAgentBravo);
			pnlSwitchAgents.add(btnAgentCharlie);
			pnlSwitchAgents.add(btnAgentDelta);
			
			this.add(pnlSwitchAgents);
			this.add(Box.createVerticalStrut(15));
		}

		/*-------------------------------------------------*/
		/* Actions de Jeu                                  */
		/*-------------------------------------------------*/
		btnPiocher = new JButton("Piocher une Carte");
		btnPasser  = new JButton("Passer le Tour");

		btnPiocher.setBackground(new Color(45, 115, 75)); 
		btnPiocher.setForeground(Color.WHITE);
		btnPiocher.setFont(new Font("Arial", Font.BOLD, 13));
		btnPiocher.setMaximumSize(new Dimension(260, 35));
		btnPiocher.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnPasser.setBackground(new Color(110, 55, 55));   
		btnPasser.setForeground(Color.WHITE);
		btnPasser.setFont(new Font("Arial", Font.BOLD, 13));
		btnPasser.setMaximumSize(new Dimension(260, 35));
		btnPasser.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnPiocher.addActionListener(e -> {
			if (estModeDemo) {
				// Liste de vos formes/cartes d'objectifs (triangle, rond, croix, carre)
				String[] poolCartes = {"Triangle", "Rond", "Croix", "Carre"};
				int randomIdx = (int)(Math.random() * poolCartes.length);
				String cartePiochee = poolCartes[randomIdx];
				
				try {
					// Charge l'image depuis le dossier images/ de votre projet
					ImageIcon originalIcon = new ImageIcon("images/" + cartePiochee.toLowerCase() + ".png");
					Image imgRedimensionnee = originalIcon.getImage().getScaledInstance(120, 140, Image.SCALE_SMOOTH);
					lblCarteActive.setIcon(new ImageIcon(imgRedimensionnee));
					lblCarteActive.setText(""); 
				} catch (Exception ex) {
					lblCarteActive.setIcon(null);
					lblCarteActive.setText("CARTE : " + cartePiochee.toUpperCase());
				}
			} else {
				lblCarteActive.setIcon(null);
				lblCarteActive.setText("Carte Piochée");
			}
			rafraichir();
		});

		btnPasser.addActionListener(e -> {
			lblCarteActive.setIcon(null);
			lblCarteActive.setText("Tour suivant");
		});

		this.add(btnPiocher);
		this.add(Box.createVerticalStrut(8));
		this.add(btnPasser);
	}

	private void ouvrirFenetreDemo(String nomAgent)
	{
		if (this.controleur == null) return;
		try {
			java.lang.reflect.Field fieldPlateau = ControleurJeu.class.getDeclaredField("panneauPlateau");
			fieldPlateau.setAccessible(true);
			PanneauPlateau plateauPrincipal = (PanneauPlateau) fieldPlateau.get(this.controleur);

			if (plateauPrincipal != null) {
				java.lang.reflect.Field fieldGraphe = PanneauPlateau.class.getDeclaredField("graphe");
				fieldGraphe.setAccessible(true);
				commun.Graphe grapheActuel = (commun.Graphe) fieldGraphe.get(plateauPrincipal);

				JFrame frameAgent = new JFrame("Réseau Tactique Référence - Agent " + nomAgent);
				frameAgent.setSize(750, 750); 
				frameAgent.setLocationRelativeTo(null);
				
				PanneauPlateau plateauCopie = new PanneauPlateau(grapheActuel, null);
				frameAgent.add(plateauCopie);
				frameAgent.setVisible(true);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Erreur chargement plateau agent.");
		}
	}

	public void rafraichir()
	{
		this.lblManche.setText("Manche : 1 / 4");
		this.txtScores.setText(
			"  Agent ALPHA   :  En attente...\n" +
			"  Agent BRAVO   :  0 pts\n" +
			"  Agent CHARLIE :  0 pts\n" +
			"  Agent DELTA   :  0 pts\n"
		);
	}

	public void setMessage(String msg) {}
}