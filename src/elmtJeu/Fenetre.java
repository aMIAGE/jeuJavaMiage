package elmtJeu;


import panel.*;
import utils.Bouton;
import utils.Chrono;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import main.Jeu;
import net.miginfocom.swing.MigLayout;

//création de la fenetre avec ses propriété, instanciation du panel principal
public class Fenetre extends JFrame{
	private static final long serialVersionUID = 1L;
	private Panel fenContent;
	private int maxWidth = 1000;
	private int maxHeight = 700;
	private int panelWidth = 500;
	private int panelHeight = 350;
	public PanelListe panels;

	public Fenetre() throws IOException {
    this.setTitle("La Java De Papel");
    this.setSize(this.maxWidth, this.maxHeight);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    
    this.fenContent = new Panel("blanc", this.maxWidth,this.maxHeight);
    this.fenContent.setBackground(Color.white);
	this.fenContent.setLayout(new CardLayout());
	
	this.createContentPanel();
	this.createAcceuilPanel();
	this.setContentPane(this.fenContent);
	
	this.setVisible(true);	
    }
  
  public void createContentPanel() throws IOException{

	  	Panel content = new Panel("blanc",this.getWidth(), this.getHeight());
  		PlanPanel planPanel = new PlanPanel("plan1",this.panelWidth,this.panelHeight);
  		ZonePanel zonePanel  = new ZonePanel("blanc",this.panelWidth,this.panelHeight);
  		JeuPanel jeuPanel = new JeuPanel("chrono",this.panelWidth,this.panelHeight,"0", "chrono");
  		CommandePanel commandePanel = new CommandePanel("blanc", this.panelWidth,this.panelHeight, this);
  		panels = new PanelListe(planPanel, zonePanel, commandePanel,jeuPanel);
  		GridLayout gl = new GridLayout(2, 2);
  		content.setLayout(gl);
  	    content.add(planPanel);
  	    content.add(jeuPanel);
  	    content.add(zonePanel);
  	    content.add(commandePanel);
  	    this.fenContent.add(content, "contentPanel");
  	    
  	}
  
  public void createAcceuilPanel() {
	  Panel bienvPanel = new Panel("debut", this.maxWidth,this.maxHeight);
	  this.fenContent.add(bienvPanel, "bienvPanel");
	  afficherPanel("bienvPanel");
	  Timer timer = new Timer(3000, new ActionListener() {
				  public void actionPerformed(ActionEvent a) {
					  afficherPanel("contentPanel"); 
				  }
			  });
			  timer.start();
			  Jeu.joueur.setChrono(new Chrono(this.panels.getJeuPanel()));
			  Jeu.joueur.getChrono().start();
  }
  
 public void afficherPanel(String nomPanel){
	 ((CardLayout) this.fenContent.getLayout()).show(this.fenContent, nomPanel);
 }
 
  	
}