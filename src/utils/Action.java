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
import panel.JeuPanel;
import panel.Panel;
import panel.PlanPanel;
import panel.ZonePanel;

public class Action implements ActionListener{
	private String act;
	PlanPanel planPanel;
	ZonePanel zonePanel;
	JeuPanel jeuPanel;
	Joueur joueur;
	Fenetre fenetre;
	
	public Action(String action, PlanPanel nvPlanPanel, JeuPanel nvJeuPanel, ZonePanel nvZonePanel, Joueur nvJoueur, Fenetre fen) {
		this.act = action;
		this.jeuPanel = nvJeuPanel;
		this.zonePanel = nvZonePanel;
		this.planPanel = nvPlanPanel;
		this.joueur = nvJoueur;
		this.fenetre = fen;
	}
	
	public void avancer() {
		  joueur.avancer();
		  this.planPanel.repaint();
		  enigme();
	}
	
	public void allerAGauche() {
		joueur.allerAGauche();
		this.planPanel.repaint();
		enigme();
	}
	
	public void reculer() {
		  joueur.reculer();
		  this.planPanel.repaint();
		  enigme();
	}
	
	public void allerADroite() {
		joueur.allerADroite();
		this.planPanel.repaint();
		enigme();
	}
	
	public void enigme() {
		Zone currentZone = zonePanel.getCurrentZone(joueur); // la zone ou se trouve le joueur
		  
		  if(!currentZone.getNom().equals(joueur.getCurrentZone())) {
			  //si le joueur a changé de zone
			  ((CardLayout) this.zonePanel.getLayout()).show(this.zonePanel, currentZone.getNom());//je change l'image de la zone
			  
			  joueur.setCurrentZone(currentZone.getNom()); //je change la zone du joueur
			  int currentEtape = joueur.getCurrentEtape(); //etape que le joueur doit faire maintenant
			  Etape etape = currentZone.getEtapes(currentEtape); // on regarde si l'étape a faire est dans cette zone
			  System.out.println(currentEtape);
			  if(etape != null) System.out.println(etape.getNumEtape());
			  else System.out.println("c'est null");
			  
			  
			  JOptionPane jop1 = new JOptionPane();
			  ImageIcon img = new ImageIcon("src/jeux/test2/images/enigme.jpg");
			  String txt = "";
			  if(etape == null) {
					 txt = " aucune etape associée";
					  JOptionPane.showMessageDialog(null, txt, "Enigme", JOptionPane.INFORMATION_MESSAGE, img); 
				 }
				 else {
					 if(etape.getNumEtape() == -1) {
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
		Zone currentZone = zonePanel.getCurrentZone(joueur);
		if(currentZone != null) {
			jeuPanel.setTexte(currentZone.getInstructions(), "java");
			jeuPanel.setImage("java");
		}
		else {
			jeuPanel.setTexte("Entre dans une piece, je t'aiderais ensuite", "java");
			jeuPanel.setImage("java");
		}
		
		
		this.jeuPanel.repaint();
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
			jeuPanel.setTexte(joueur.getChrono().getStringTime(), "chrono");
			jeuPanel.setImage("chrono");
			this.jeuPanel.repaint();
		}
		
		if(this.act == "commandes") {
			jeuPanel.setTexte("on affichera la liste des commandes", "commandes");
			jeuPanel.setImage("gardien");
			this.jeuPanel.repaint();
		}
		
		if(this.act == "enigme") {
			this.enigme();
		}
		
		if(this.act == "java") {
			this.aideJava();
		}
		
		
		
		
		
			
	}
}