package elmtJeu;


import panel.*;
import utils.Chrono;
import utils.MsgIntro;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.Timer;
import main.Jeu;

//création de la fenetre avec ses propriété, instanciation du panel principal
public class Fenetre extends JFrame{
	private static final long serialVersionUID = 1L;
	private Panel fenContent;
	private int maxWidth = 1000;
	private int maxHeight = 700;
	private int panelWidth = 500;
	private int panelHeight = 350;
	private Timer timer;
	private PanelListe panels;

	public Fenetre() throws IOException {
	    this.setTitle("La Java De Papel");
	    this.setSize(this.maxWidth, this.maxHeight);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    
	    this.fenContent = new Panel("blanc", this.maxWidth,this.maxHeight);
		this.fenContent.setLayout(new CardLayout());
		this.createContentPanel();
		this.createAcceuilPanel();
		this.setContentPane(this.fenContent);
		this.setVisible(true);	
	
	
    }
  
  public void createContentPanel() throws IOException {

	  	Panel content = new Panel("blanc",this.getWidth(), this.getHeight());
  		PlanPanel planPanel = new PlanPanel("plan1",this.panelWidth,this.panelHeight);
  		ZonePanel zonePanel  = new ZonePanel("blanc",this.panelWidth,this.panelHeight);
  		JeuPanel jeuPanel = new JeuPanel("chrono",this.panelWidth,this.panelHeight,"0 s", "chrono");
  		CommandePanel commandePanel = new CommandePanel("blanc", this.panelWidth,this.panelHeight, zonePanel.getNbEtape());
  		panels = new PanelListe(planPanel, zonePanel, commandePanel,jeuPanel);
  		content.setLayout(new GridLayout(2, 2));
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
	  timer = new Timer(3000, new ActionListener() {
				  public void actionPerformed(ActionEvent a) {
				   @SuppressWarnings("unused")
					MsgIntro msg = new MsgIntro("javaDeb", "ok, j'ai tout compris");
					  afficherPanel("contentPanel"); 
					  Jeu.getJoueur().getChrono().start();
					  timer.stop();
				  }
			  });
	  
	  timer.start();
	  
	  
	  Jeu.getJoueur().setChrono(new Chrono(this.panels.getJeuPanel()));
  }
  
 public void afficherPanel(String nomPanel){
	 ((CardLayout) this.fenContent.getLayout()).show(this.fenContent, nomPanel);
 }
 
 public int getPanelWidth() {
	 return this.panelWidth;
 }
 
 public int getPanelHeight() {
	 return this.panelHeight;
 }
 
 public PanelListe getPanels() {
	 return this.panels;
 }
 
 
  	
}