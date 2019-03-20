package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;
import elmtJeu.Fenetre;
import elmtJeu.Joueur;
import utils.MsgPerdu;

public class Jeu{
	public static final String NAME = "La Java De Papel";
	public static final int DUREE = 20; //en minutes
	private static Joueur joueur;
	private static Fenetre fen;
	private static Timer timer;
	
	public static void main(String[] args) throws IOException {
		Jeu.creationJeu();
		
		
	}
	
	public static void creationJeu() throws IOException {
		//création du joueur
		Jeu.joueur = new Joueur(228,306); //position de départ
		
		//new Fenetre instance
		Jeu.fen = new Fenetre();
		
		
		timer = new Timer(DUREE*1000*60, new ActionListener() {
			  public void actionPerformed(ActionEvent a) {
				 MsgPerdu.createMessage("Le temps est écoulé, vous avez perdu ... Voulez vous recommencer ?");
				 timer.stop();
			  }
		  });
		  timer.start();
	}
	
	public static Joueur getJoueur() {
		return Jeu.joueur;
	}
	
	public static Fenetre getFenetre() {
		return Jeu.fen;
	}
}
