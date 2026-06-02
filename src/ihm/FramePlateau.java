package ihm;

import java.awt.BorderLayout;
import javax.swing.*;


public class FramePlateau extends JFrame 
{
    private PanelPlateau panelPlateau;

    public FramePlateau()
    {
        this.setTitle("Opération Réseau Rouge");
        this.setSize(800, 800);
        this.setLayout(new BorderLayout());
        

        this.panelPlateau = new PanelPlateau();
        this.add(panelPlateau, BorderLayout.CENTER);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) 
    {
        new FramePlateau();
    }
}