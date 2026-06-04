package ihm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import controleur.Controleur;
import metier.*;

public class PanelPlateau extends JPanel implements ActionListener
{
	private Controleur ctrl;
	private Image imageFondPlateau; 
	
	// images pour les types de cases
	private Image imgPort;
	private Image imgPetrolier;
	private Image imgTank;
	private Image imgFerme;
	private Image imgHopital;
	
	private JButton btnPasserTour;
	private JPanel panelActionBas;
	private Sommet sommetSelectionne; 

	public PanelPlateau(Controleur ctrl) 
	{
		this.ctrl = ctrl;
		this.setLayout(new BorderLayout());
		this.sommetSelectionne = null;

		// chargement des images du projet
		try 
		{
			this.imageFondPlateau = ImageIO.read(new File("src/images/fond_plateau.png")); 
			this.imgPort          = ImageIO.read(new File("src/images/port.png"));
			this.imgPetrolier     = ImageIO.read(new File("src/images/petrolier.png"));
			this.imgTank          = ImageIO.read(new File("src/images/tank.png"));
			this.imgFerme         = ImageIO.read(new File("src/images/ferme.png"));
			this.imgHopital       = ImageIO.read(new File("src/images/hopital.png"));
		} 
		catch (Exception e) 
		{
			System.out.println("Erreur images: " + e.getMessage());
		}

		// panel du bas pour le bouton
		this.panelActionBas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		this.panelActionBas.setOpaque(false);

		// design du bouton passer le tour
		this.btnPasserTour = new JButton("PASSER LE TOUR");
		this.btnPasserTour.setFont(new Font("Courier New", Font.BOLD, 12));
		this.btnPasserTour.setBackground(new Color(15, 15, 15));
		this.btnPasserTour.setForeground(new Color(0, 255, 65));
		this.btnPasserTour.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 65), 1));
		this.btnPasserTour.setPreferredSize(new Dimension(150, 30));
		this.btnPasserTour.setFocusPainted(false);
		this.btnPasserTour.addActionListener(this);
		
		this.panelActionBas.add(this.btnPasserTour);
		this.add(this.panelActionBas, BorderLayout.SOUTH);

		// clic souris sur le plateau
		this.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				Graphe grapheCourant = ctrl.getJoueurCourant().getPlateau();
				Sommet sommetClique = null;

				for (Sommet s : grapheCourant.getListeSommets()) 
				{
					int centreX = s.getX() + 20;
					int centreY = s.getY() + 20;
					
					double distance = Math.sqrt(Math.pow(mouseX - centreX, 2) + Math.pow(mouseY - centreY, 2));
					
					if (distance <= 20) 
					{
						sommetClique = s;
						break;
					}
				}

				if (sommetClique != null) 
				{
					if (sommetSelectionne == null) 
					{
						sommetSelectionne = sommetClique;
					} 
					else 
					{
						if (sommetSelectionne.getId() != sommetClique.getId()) 
						{
							ctrl.tenterTracerRoute(sommetSelectionne.getId(), sommetClique.getId());
						}
						sommetSelectionne = null;
					}
				} 
				else 
				{
					sommetSelectionne = null;
				}

				mettreAJourAffichage();
			}
		});

		this.mettreAJourAffichage();
	}

	public void mettreAJourAffichage() 
	{
		this.btnPasserTour.setEnabled(this.ctrl.isPasserAutorise());
		this.repaint();
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnPasserTour) 
		{
			this.ctrl.joueurPasseSonTour();
			this.sommetSelectionne = null;
			this.mettreAJourAffichage();
		}
	}

	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// fond
		if (this.imageFondPlateau != null) 
		{
			g2d.drawImage(this.imageFondPlateau, 0, 0, 660, 660, this);
		}
		else
		{
			g2d.setColor(new Color(20, 20, 20));
			g2d.fillRect(0, 0, 660, 660);
		}

		// affichage du score et des infos a droite
		g2d.setColor(new Color(10, 10, 10, 230));
		g2d.fillRect(660, 0, this.getWidth() - 660, this.getHeight());
		g2d.setColor(new Color(0, 255, 65));
		g2d.drawRect(670, 15, this.getWidth() - 685, 140);

		g2d.setFont(new Font("Courier New", Font.BOLD, 13));
		g2d.drawString("OFFICIER : " + this.ctrl.getJoueurCourant().getNom(), 685, 45);
		
		g2d.setColor(Color.YELLOW);
		g2d.drawString("OBJECTIF : ROUTE " + this.ctrl.getCarteActive(), 685, 80);
		
		g2d.setColor(new Color(0, 255, 65));
		g2d.drawString("RÉSEAU   : " + this.ctrl.getJoueurCourant().getPlateau().calculerScoreFormuleProf() + " PTS", 685, 115);

		Graphe grapheCourant = this.ctrl.getJoueurCourant().getPlateau();
		Sommet[] sommets = grapheCourant.getListeSommets();
		boolean[][] grillePossibles = grapheCourant.getGrillePlanairePossibles();

		// dessiner les arretes du fichier texte (lignes vertes claires)
		g2d.setColor(new Color(0, 255, 65, 60)); 
		g2d.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < sommets.length; i++) 
		{
			for (int j = i; j < sommets.length; j++) 
			{
				if (grillePossibles[i][j]) 
				{
					g2d.drawLine(sommets[i].getX() + 20, sommets[i].getY() + 20, 
								 sommets[j].getX() + 20, sommets[j].getY() + 20);
				}
			}
		}

		// dessiner les routes choisies par le joueur (lignes blanches)
		g2d.setColor(new Color(255, 255, 255)); 
		g2d.setStroke(new BasicStroke(4.0f)); 
		for (String lien : grapheCourant.getCheminsTracesJoueur()) 
		{
			int idSrc = Integer.parseInt(lien.split("-")[0]);
			int idCible = Integer.parseInt(lien.split("-")[1]);
			
			g2d.drawLine(sommets[idSrc].getX() + 20, sommets[idSrc].getY() + 20, 
						 sommets[idCible].getX() + 20, sommets[idCible].getY() + 20);
		}

		// dessiner les sommets et les qg
		for (Sommet s : sommets) 
		{
			Image imgZone = null;
			if (s.getTypeZone().equals("FERME")) imgZone = this.imgFerme;
			if (s.getTypeZone().equals("HOPITAL")) imgZone = this.imgHopital;
			if (s.getTypeZone().equals("PETROLIER")) imgZone = this.imgPetrolier;
			if (s.getTypeZone().equals("PORT")) imgZone = this.imgPort;
			if (s.getTypeZone().equals("TANK")) imgZone = this.imgTank;

			// coloriage des bases de depart (les angles et le centre)
			Color couleurBordureQG = null;
			if (s.getId() == 0)  couleurBordureQG = new Color(220, 20, 60);    // Rouge
			if (s.getId() == 6)  couleurBordureQG = new Color(139, 69, 19);   // Marron
			if (s.getId() == 24) couleurBordureQG = new Color(34, 139, 34);   // Vert
			if (s.getId() == 48) couleurBordureQG = new Color(30, 144, 255);  // Bleu

			if (couleurBordureQG != null)
			{
				g2d.setColor(couleurBordureQG);
				g2d.fillRect(s.getX() - 4, s.getY() - 4, 48, 48); 
			}

			// image par dessus
			if (imgZone != null)
			{
				g2d.drawImage(imgZone, s.getX(), s.getY(), 40, 40, this);
			}

			// encadrement
			if (couleurBordureQG != null)
			{
				g2d.setColor(Color.WHITE);
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRect(s.getX(), s.getY(), 40, 40); 
			}
			else
			{
				g2d.setColor(new Color(80, 80, 80));
				g2d.setStroke(new BasicStroke(1f));
				g2d.drawRect(s.getX(), s.getY(), 40, 40);
			}

			// si selectionné
			if (this.sommetSelectionne != null && this.sommetSelectionne.getId() == s.getId()) 
			{
				g2d.setColor(Color.YELLOW);
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRect(s.getX() - 6, s.getY() - 6, 52, 52);
			}
		}
	}
}