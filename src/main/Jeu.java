package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;
import elmtJeu.Fenetre;
import elmtJeu.Joueur;
import utils.Chrono;

public class Jeu{
	public static final String NAME = "La Java De Papel";
	public static final int DUREE = 6000;
	public static Joueur joueur;
	
	public static void main(String[] args) throws IOException {
		
		//création du joueur
		Jeu.joueur = new Joueur(201,438); //position de départ
		
		
		//new Fenetre instance
		Fenetre fen = new Fenetre();
		
		
		Timer timer = new Timer(DUREE*1000, new ActionListener() {
			  public void actionPerformed(ActionEvent a) {
				 fen.setVisible(false);
				 //afficher une fenetre de jeu comme quoi on a perdu
			  }
		  });
		  timer.start();
		
	}
}
