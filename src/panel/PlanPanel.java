package panel;

import java.awt.Color;
import java.awt.Graphics;

import elmtJeu.Joueur;
import main.Jeu;

public class PlanPanel extends Panel{
	private static final long serialVersionUID = 1L;
	public PlanPanel(String nvImage,int nvLargeur, int nvLongueur) {
		super(nvImage,nvLargeur, nvLongueur);
	}
	
	
	 public void paintComponent(Graphics g){
		 	super.paintComponent(g);	
			g.setColor(Color.red);
			g.fillOval(Jeu.joueur.getPosX(), Jeu.joueur.getPosY(), 15, 15);
								
	 }
			 
	
	
}