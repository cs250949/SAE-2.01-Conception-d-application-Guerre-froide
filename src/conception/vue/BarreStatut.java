package conception.vue;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BarreStatut extends JPanel
{
	private JLabel labelInfo;
	private JLabel labelPosition;
	
	public BarreStatut()
	{
		setLayout(new BorderLayout());
		setBackground(new Color(40, 40, 50));
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60,60,70)),
			new EmptyBorder(5, 12, 5, 12)));
		
		labelInfo = new JLabel("Selectionnez un outil puis cliquez sur une cellule");
		labelInfo.setForeground(new Color(200,200,180));
		labelInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		labelPosition = new JLabel("");
		labelPosition.setForeground(new Color(180,180,160));
		labelPosition.setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		add(labelInfo, BorderLayout.CENTER);
		add(labelPosition, BorderLayout.EAST);
	}
	
	public void setMessage(String msg) { labelInfo.setText(msg); }
	public void setPosition(int ligne, int colonne)
	{
		if (ligne >= 0 && colonne >= 0)
			labelPosition.setText("Cellule (" + ligne + ", " + colonne + ")");
		else
			labelPosition.setText("");
	}
}