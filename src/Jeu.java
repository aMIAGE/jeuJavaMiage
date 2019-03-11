

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Timer;

import elmtJeu.Fenetre;
import elmtJeu.Joueur;
import utils.Chrono;

//on créer un joueur, on initialise le chrono
//plus tard cela permettra peut etre de faire plusieurs joueurs, plusieurs chrono, quitter une partie, revenir sur une partie ....
public class Jeu{
	public static final String NAME = "La Java De Papel";
	public static final int DUREE = 6000;
	public static Joueur joueur;
	
	public static void main(String[] args) throws IOException {
		
		//on crée le joueur en le placant à l'entrée du bâtiment 
		Jeu.joueur = new Joueur(228,306);
		joueur.setChrono(new Chrono(System.currentTimeMillis()));
		
		Fenetre fen = new Fenetre(Jeu.joueur);
		
		
		Timer timer = new Timer(DUREE*1000, new ActionListener() {
			  public void actionPerformed(ActionEvent a) {
				 fen.setVisible(false);
				 //afficher une fenetre de jeu comme quoi on a perdu
			  }
		  });
		  timer.start();
		
	}
}
