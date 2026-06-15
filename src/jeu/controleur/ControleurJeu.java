package jeu.controleur;

import jeu.metier.Jeu;
import jeu.vue.PanneauInfo;
import jeu.vue.PanneauPlateau;

public class ControleurJeu
{
	private Jeu            jeu;
	private PanneauPlateau panneauPlateau;
	private PanneauInfo    panneauInfo;
	private boolean        modeDebug = false;
	
	public ControleurJeu(Jeu jeu)
	{
		this.jeu = jeu;
	}
	
	public void setVues(PanneauPlateau plateau, PanneauInfo info)
	{
		this.panneauPlateau = plateau;
		this.panneauInfo    = info;
	}
	
	public Jeu getJeu() { return this.jeu; }
	
	public void setModeDebug(boolean actif)
	{
		this.modeDebug = actif;
		if (panneauInfo != null)
		{
			panneauInfo.setMessage(actif ? "MODE DEBUG ACTIVE — DEMO" : "Regles normales restaurees");
		}
	}
	
	public boolean isModeDebug() { return this.modeDebug; }
	
	public void forcerCarteMetier(String typeCarte)
	{
		jeu.forcerCarteDebug(typeCarte);
	}
	
	/*-------------------------------------------------*/
	/* Intercepte le clic sur la grille du plateau     */
	/*-------------------------------------------------*/
	public void CelluleSelectionnee(int lig, int col)
	{
		if (modeDebug)
		{
			
			boolean forceOk = jeu.forcerPlacementCaseDebug(lig, col);
			if (forceOk && panneauInfo != null)
			{
				panneauInfo.setMessage("Demo : Saut force en (" + lig + "," + col + ")");
			}
		}
		else
		{
			// Mode de jeu normal soumis aux regles strictes
			boolean coupValide = jeu.jouerCoup(lig, col);
			if (coupValide && panneauInfo != null)
			{
				panneauInfo.rafraichir();
			}
		}
		
		// Force la mise a jour visuelle du dessin des arêtes
		if (panneauPlateau != null)
		{
			panneauPlateau.mettreAJourAffichage();
		}
	}
}