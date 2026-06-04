package ihm;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import controleur.Controleur;

public class FramePlateau extends JFrame 
{
    private Controleur ctrl;
    private PanelConfig panelMenu;      
    private PanelPlateau panelPlateau; 

    public FramePlateau(Controleur ctrl)
    {
        this.ctrl = ctrl;
        this.setTitle("Opération Réseau Rouge");
        this.setSize(1000, 750); 
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // 1. Au démarrage, on crée et on affiche uniquement le Menu d'accueil
        this.panelMenu = new PanelConfig(this.ctrl);
        this.add(this.panelMenu, BorderLayout.CENTER);

        this.setVisible(true);
    }

    /**
     * Cette méthode est appelée par le Contrôleur une fois que le bouton
     * "Lancer la Partie" a été cliqué et que les joueurs sont initialisés.
     */
    public void activerEcranJeu() 
    {
        // 1. On retire le menu d'accueil de la fenêtre
        this.remove(this.panelMenu);

        // 2. On instancie le plateau de jeu qui va charger la grille conçue
        this.panelPlateau = new PanelPlateau(this.ctrl);
        this.add(this.panelPlateau, BorderLayout.CENTER);

        // 3. On force Java à recalculer l'affichage et à redessiner la fenêtre
        this.revalidate();
        this.repaint();
    }

    public static void main(String[] args) 
    {
        // 1. Création du contrôleur de jeu
        Controleur controleur = new Controleur();

        // 2. Création de la fenêtre principale en lui passant le contrôleur
        FramePlateau fenetre = new FramePlateau(controleur);

        // 3. TRÈS IMPORTANT : On lie la fenêtre au contrôleur pour que le double couplage fonctionne
        controleur.setFrame(fenetre);
    }
}