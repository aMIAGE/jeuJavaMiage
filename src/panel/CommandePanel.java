package panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import elmtJeu.Fenetre;
import elmtJeu.Joueur;
import utils.Action;
import utils.Bouton;

public class CommandePanel extends Panel{
	private static final long serialVersionUID = 1L;
	Fenetre fenetre;
	
	public CommandePanel(String nvImage, int nvLargeur, int nvLongueur, Fenetre fen) {
		
		super(nvImage, nvLargeur, nvLongueur);
		this.fenetre = fen;
		
	    this.setLayout(new GridLayout(2, 1));
	    this.add(this.creerBoutonsDirection());
	    this.add(this.creerBoutonsCommandes());
	    
		
	}
	
	
	public Panel creerBoutonsCommandes() {
		Panel commandes = new Panel("blanc", 600, 450);
		
		Bouton java = new Bouton("Appeler Java");
		java.addActionListener(new Action("java",this.fenetre) );
		commandes.add(java);
		
		
		commandes.add(new Bouton("Voir l'inventaire"));
		//affiche l'inventaire sur le jeuPanel
		
		Bouton comm = new Bouton("Voir les commandes");
		comm.addActionListener(new Action("commandes", this.fenetre));
		commandes.add(comm);
		//affiche une image contenant les commandes sur le jeuPanel
		
		Bouton boutChrono = new Bouton("Voir le chrono");
		boutChrono.addActionListener(new Action("chrono", this.fenetre));
		commandes.add(boutChrono);
		//affiche le chrono sur le jeuPanel
		
		Bouton boutEnigme = new Bouton("ouvrir une fenetre d'enigme");
		boutEnigme.addActionListener(new Action("enigme", this.fenetre));
		commandes.add(boutEnigme);
		
		return commandes;
	}
		
		public Panel creerBoutonsDirection() {
			Panel direction = new Panel("blanc", 600, 450);
			direction.setLayout(new GridLayout(3,3));
			
			
			Bouton av = new Bouton("Avancer");
		    av.addActionListener(new Action("av",this.fenetre));
		    
		    Bouton enter = new Bouton("Entrer");
		    
		    Bouton gauche = new Bouton("Gauche");
		    gauche.addActionListener(new Action("gauche",this.fenetre));
		    
		    Bouton droite = new Bouton("Droite");
		    droite.addActionListener(new Action("droite",this.fenetre));

		    Bouton rec = new Bouton("Reculer");
		    rec.addActionListener(new Action("rec",this.fenetre));
		    
		    direction.add(new Panel("blanc" ,10,10)); //provisoire pour mettre un blanc 
		    direction.add(av);
		    direction.add(new Panel("blanc" ,10,10)); //provisoire pour mettre un blanc 
		    direction.add(gauche);
		    direction.add(enter);
		    direction.add(droite);
		    direction.add(new Panel("blanc" ,10,10)); //provisoire pour mettre un blanc 
		    direction.add(rec);
		    direction.add(new Panel("blanc" ,10,10)); //provisoire pour mettre un blanc 
		    
		    return direction;
		}

}
