package utils;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import elmtJeu.Enigme;
import elmtJeu.Etape;
import elmtJeu.Fenetre;
import elmtJeu.Joueur;
import main.Jeu;
import panel.JeuPanel;
import panel.Panel;
import panel.PlanPanel;
import panel.ZonePanel;

public class Action implements ActionListener{
	private String act;
	Fenetre fenetre;
	
	public Action(String action, Fenetre fen) {
		this.act = action;
		this.fenetre = fen;
	}
	
	public void avancer() {
		  Jeu.joueur.avancer();
		  this.fenetre.panels.getPlanPanel().repaint();
		  enigme();
	}
	
	public void allerAGauche() {
		Jeu.joueur.allerAGauche();
		this.fenetre.panels.getPlanPanel().repaint();
		enigme();
	}
	
	public void reculer() {
		  Jeu.joueur.reculer();
		  this.fenetre.panels.getPlanPanel().repaint();
		  enigme();
	}
	
	public void allerADroite() {
		Jeu.joueur.allerADroite();
		this.fenetre.panels.getPlanPanel().repaint();
		enigme();
	}
	
	public void enigme() {
		ZonePanel zonePanel = this.fenetre.panels.getZonePanel();
		Joueur joueur = Jeu.joueur;
		
		//récupération de la zone actuelle du joueur
		Zone currentZone = zonePanel.getCurrentZone(); 
		  
		  if(!currentZone.getNom().equals(joueur.getCurrentZone())) {
			  //si le joueur a changé de zone
			  zonePanel.setZoneImage(currentZone.getNom());//je change l'image de la zone
			  joueur.setCurrentZone(currentZone.getNom()); //je change la zone du joueur
			  
			  int currentEtape = joueur.getCurrentEtape(); //etape que le joueur doit faire maintenant
			  Etape etape = currentZone.getEtapes(currentEtape); // on regarde si l'étape a faire est dans cette zone
			  
			  
			  JOptionPane jop1 = new JOptionPane();
			  ImageIcon img = new ImageIcon("src/jeux/test2/images/enigme.jpg");
			  String txt = "";
			  if(etape == null) {
				  //on pourra mettre un message en JAVA qui dit qu'il n'y a pas/plus d'étape la ou il vient d'arriver
					 //txt = " aucune etape associée";
					  //JOptionPane.showMessageDialog(null, txt, "Enigme", JOptionPane.INFORMATION_MESSAGE, img); 
				 }
				 else {
					 if(etape.getNumEtape() == -1) {
						 //si on veut dire pourquoi on a perdu on doit enregistré dans chaque étape un message disant pourquoi on perd 
						  txt = "vous avez perdu";
						  JOptionPane.showMessageDialog(null, txt, "Enigme", JOptionPane.INFORMATION_MESSAGE, img); 
						 }
					 else {
						 etape.launchEnigme(this.fenetre);
						 joueur.setCurrentEtape();
					 }
				 }
		   
		  }
		   
		       
	}
	
	
	public void aideJava() {
		ZonePanel zonePanel = this.fenetre.panels.getZonePanel();
		JeuPanel jeuPanel = this.fenetre.panels.getJeuPanel();
		Zone currentZone = zonePanel.getCurrentZone();
		
		if(currentZone != null) {
			jeuPanel.setTexte(currentZone.getInstructions(), "java");
			jeuPanel.setImage("java");
		}
		else {
			jeuPanel.setTexte("Entre dans une piece, je t'aiderais ensuite", "java");
			jeuPanel.setImage("java");
		}
		
		
		jeuPanel.repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(this.act == "av" ) {
		  this.avancer();
		}
		
		if(this.act == "gauche" ) {
			this.allerAGauche();

		}
		
		if(this.act == "droite" ) {
			this.allerADroite();

		}
		
		if(this.act == "rec" ) {
			this.reculer();

		}
		
		if(this.act == "chrono") {
			JeuPanel jeuPanel = this.fenetre.panels.getJeuPanel();
			jeuPanel.setTexte(Jeu.joueur.getChrono().getStringTime(), "chrono");
			jeuPanel.setImage("chrono");
			jeuPanel.repaint();
		}
		
		if(this.act == "commandes") {
			JeuPanel jeuPanel = this.fenetre.panels.getJeuPanel();
			jeuPanel.setTexte("on affichera la liste des commandes", "commandes");
			jeuPanel.setImage("gardien");
			jeuPanel.repaint();
		}
		
		if(this.act == "enigme") {
			this.enigme();
		}
		
		if(this.act == "java") {
			this.aideJava();
		}
		
		
		
		
		
			
	}
}