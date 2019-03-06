package panel;

import java.awt.Color;
import java.awt.Graphics;

import elmtJeu.Joueur;

public class PlanPanel extends Panel{
	private static final long serialVersionUID = 1L;
	public PlanPanel(String nvImage,int nvLargeur, int nvLongueur,Joueur nvJoueur) {
		super(nvImage,nvLargeur, nvLongueur, nvJoueur);
	}
	
	
	 public void paintComponent(Graphics g){
		 	super.paintComponent(g);	
			g.setColor(Color.red);
			g.fillOval(joueur.getPosX(), joueur.getPosY(), 15, 15);
								
	 }
			 
	
	
}