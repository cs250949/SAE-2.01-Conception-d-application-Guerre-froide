package conception.vue;

import conception.controleur.ControleurConception;
import java.awt.*;
import javax.swing.*;

public class PanneauBoutons extends JPanel
{
	private ControleurConception controleur;
	
	public PanneauBoutons(ControleurConception controleur)
	{
		this.controleur = controleur;
		this.construire();
	}
	
	private void construire()
	{
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		this.setBackground(new Color(30, 30, 40));
		
		JButton btnRegles = new JButton("Regles");
		btnRegles.setBackground(new Color(80, 80, 120));
		btnRegles.setForeground(Color.WHITE);
		btnRegles.addActionListener(e -> this.controleur.afficherRegles());
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.setBackground(new Color(120, 60, 60));
		btnAnnuler.setForeground(Color.WHITE);
		btnAnnuler.addActionListener(e -> System.exit(0));
		
		JButton btnReset = new JButton("Reinitialiser");
		btnReset.setBackground(new Color(100, 80, 40));
		btnReset.setForeground(Color.WHITE);
		btnReset.addActionListener(e -> this.controleur.reinitialiserChamps());
		
		JButton btnValider = new JButton("Valider et lancer");
		btnValider.setBackground(new Color(60, 100, 60));
		btnValider.setForeground(Color.WHITE);
		btnValider.addActionListener(e -> this.controleur.validerEtLancer());
		
		this.add(btnRegles);
		this.add(btnAnnuler);
		this.add(btnReset);
		this.add(btnValider);
	}
}