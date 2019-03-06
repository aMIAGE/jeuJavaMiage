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
	PlanPanel planPanel;
	ZonePanel zonePanel;
	JeuPanel jeuPanel;
	Joueur joueur;
	Fenetre fenetre;
	
	public CommandePanel(String nvImage, int nvLargeur, int nvLongueur, JeuPanel nvJeuPanel, PlanPanel nvPlanPanel, ZonePanel nvZonePanel, Joueur nvJoueur, Fenetre fen) {
		
		super(nvImage, nvLargeur, nvLongueur,null);
		this.jeuPanel = nvJeuPanel;
		this.zonePanel = nvZonePanel;
		this.planPanel = nvPlanPanel;
		this.joueur = nvJoueur;
		this.fenetre = fen;
		
	    this.setLayout(new GridLayout(2, 1));
	    this.add(this.creerBoutonsDirection());
	    this.add(this.creerBoutonsCommandes());
	    
		
	}
	
	
	public Panel creerBoutonsCommandes() {
		Panel commandes = new Panel("blanc", 600, 450,null);
		
		Bouton java = new Bouton("Appeler Java");
		java.addActionListener(new Action("java",null, jeuPanel, zonePanel, joueur,null) );
		commandes.add(java);
		
		
		commandes.add(new Bouton("Voir l'inventaire"));
		//affiche l'inventaire sur le jeuPanel
		
		Bouton comm = new Bouton("Voir les commandes");
		comm.addActionListener(new Action("commandes", null, jeuPanel,null, null,null));
		commandes.add(comm);
		//affiche une image contenant les commandes sur le jeuPanel
		
		Bouton boutChrono = new Bouton("Voir le chrono");
		boutChrono.addActionListener(new Action("chrono", null, jeuPanel,null, joueur,null));
		commandes.add(boutChrono);
		//affiche le chrono sur le jeuPanel
		
		Bouton boutEnigme = new Bouton("ouvrir une fenetre d'enigme");
		boutEnigme.addActionListener(new Action("enigme", null, jeuPanel, zonePanel, joueur,this.fenetre));
		commandes.add(boutEnigme);
		
		return commandes;
	}
		
		public Panel creerBoutonsDirection() {
			Panel direction = new Panel("blanc", 600, 450,null);
			direction.setLayout(new GridLayout(3,3));
			
			
			Bouton av = new Bouton("Avancer");
		    av.addActionListener(new Action("av",planPanel,null,  zonePanel,joueur,null));
		    
		    Bouton enter = new Bouton("Entrer");
		    
		    Bouton gauche = new Bouton("Gauche");
		    gauche.addActionListener(new Action("gauche",planPanel,null, zonePanel, joueur,null));
		    
		    Bouton droite = new Bouton("Droite");
		    droite.addActionListener(new Action("droite",planPanel,null, zonePanel, joueur,null));

		    Bouton rec = new Bouton("Reculer");
		    rec.addActionListener(new Action("rec",planPanel, null, zonePanel,joueur,null));
		    
		    direction.add(new Panel("blanc" ,10,10,null)); //provisoire pour mettre un blanc 
		    direction.add(av);
		    direction.add(new Panel("blanc" ,10,10,null)); //provisoire pour mettre un blanc 
		    direction.add(gauche);
		    direction.add(enter);
		    direction.add(droite);
		    direction.add(new Panel("blanc" ,10,10,null)); //provisoire pour mettre un blanc 
		    direction.add(rec);
		    direction.add(new Panel("blanc" ,10,10,null)); //provisoire pour mettre un blanc 
		    
		    return direction;
		}

}
