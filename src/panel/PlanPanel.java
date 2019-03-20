package panel;

import java.awt.Color;
import java.awt.Graphics;
import main.Jeu;

/**
 * <b>PlanPanel est une classe heritant de Panel</b>
 * <p>
 * PlanPanel est la classe representant l'espace dans lequel se trouve le plan (en haut, a gauche).
 * PlanPanel est donc caracterise par les identifiants de Panel en plus de celui la:
 * <ul>
 * <li>Une constante qui permet d'affecter un numero de version a la classe</li>
 * </ul>
 * </p>
 * Un Panel fournit un espace dans lequel une application peut attacher tout autre composant, 
 * y compris d'autres Panels.
 * <p>
 *
 * @see Panel
 */
public class PlanPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur Plan Panel
	 * <p> 
	 * A la construction d'un objet PlanPanel, 
	 * un Panel est cree avec une image, une largeur et une longueur.
	 * </p>
	 * @param nvImage
	 * @param nvLargeur
	 * @param nvLongueur
	 */
	public PlanPanel(String nvImage,int nvLargeur, int nvLongueur) {
		super(nvImage,nvLargeur, nvLongueur);
	}
	
	/**
	 * Parametre la disposition du contenu du Panel
	 */
	 public void paintComponent(Graphics g){
		 	super.paintComponent(g);	
			g.setColor(Color.red);
			g.fillOval(Jeu.getJoueur().getPosX(), Jeu.getJoueur().getPosY(), 15, 15);
	 }
			 
	
	
}
