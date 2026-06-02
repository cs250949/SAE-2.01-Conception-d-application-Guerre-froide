package ihm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PanelPlateau extends JPanel implements ActionListener 
{
    private static final int TAILLE = 7;
	private JButton[][] boutonsGrille;

    public PanelPlateau() 
    {
        this.setLayout(new GridLayout(TAILLE, TAILLE));
        this.boutonsGrille = new JButton[TAILLE][TAILLE];

        for (int i = 0; i < TAILLE; i++) 
		{
			for (int j = 0; j < TAILLE; j++) 
			{
				this.boutonsGrille[i][j] = new JButton();
				
				// ASTUCE : On utilise le "ActionCommand" pour cacher les coordonnées i et j dans le bouton
				this.boutonsGrille[i][j].setActionCommand(i + "," + j);
				
				this.boutonsGrille[i][j].addActionListener(this);
				this.add(this.boutonsGrille[i][j]);
			}
		}
    }

    public void actionPerformed(ActionEvent e) 
	{
		JButton boutonClique = (JButton) e.getSource();
		
		// On récupère la String "i,j" qu'on avait cachée dans le bouton
		String coordonnees = boutonClique.getActionCommand();
		System.out.println("Case cliquée : " + coordonnees);
		
		// On sépare le i et le j pour obtenir des entiers exploitables par le métier
		String[] parts = coordonnees.split(",");
		int x = Integer.parseInt(parts[0]);
		int y = Integer.parseInt(parts[1]);
		
		/* Étape future avec le Contrôleur :
		   this.controleur.clicSurCase(x, y);
		*/
	}

}