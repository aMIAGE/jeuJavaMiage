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

/**
 * La classe Fenetre represente la fenetre de jeu.
Une fenetre est caracterisee par les proprietes suivantes :
 * <ul>
 * <li>Un panel.</li>
 * <li>Une largeur.</li>
 * <li>Une longueur.</li>
 * <li>La largeur du panel.</li>
 * <li>La longueur du panel.</li>
 * <li>Une reference vers un Timer.</li>
 * <li>Une reference vers un PanelListe : la liste des panels de la fenetre.</li>
 * </ul>
 * @see PanelListe
 * @see Panel
 */

//création de la fenetre avec ses propriétés, instanciation du panel principal
	public class Fenetre extends JFrame{
		private static final long serialVersionUID = 1L;
		/**
		 * Une reference vers une instance de Panel.
		 */
		private Panel fenContent;
		/**
		 * La largeur max de la fenetre.
		 */
		private int maxWidth = 1000;
		/**
		 * La longueur max de la fenetre.
		 */
		private int maxHeight = 700;
		/**
		 * La largeur du panel.
		 * @see #getPanelWidth()
		 */
		private int panelWidth = 500;
		/**
		 * La longueur du panel.
		 * @see #getPanelHeight()
		 */
		private int panelHeight = 350;
		/**
		 * Une reference vers une instance de Timer.
		 */
		private Timer timer;
		/**
		 * Une reference vers une instance de PanelListe. La liste des panels de la fenetre.
		 * @see #getPanels()
		 */
		private PanelListe panels;
	
	/**
	 * Constructeur Fenetre, creation de la fenetre avec ses proprietes et instanciation du panel principal.
	 * 		 * @throws IOException
	 */
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
	
	/**
	 * Instancie les differents panels constituant la fenetre de jeu et initialise de leurs proprietes.
	 * @throws IOException
	 * @see Panel
	 * @see PlanPanel
	 * @see ZonePanel
	 * @see JeuPanel
	 * @see CommandePanel
	 */
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
	
	/**
	 * Cree le panel de bienvenue.
	 * Ce panel est affiche lors de l'ouverture du jeu. Le jeu et le timer commencent lorsque le joueur interagit avec la fenetre.
	 */
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
	
	/**
	 * Affiche le panel.
	 * @param nomPanel le nom du panel
	 */
	public void afficherPanel(String nomPanel){
		((CardLayout) this.fenContent.getLayout()).show(this.fenContent, nomPanel);
	}
	 
	/**
	 * Acceder a la largeur du panel en lecture.
	 * @return la largeur du panel
	 */
	public int getPanelWidth() {
		return this.panelWidth;
	}
	 
	/**
	 * Acceder a la longueur du panel en lecture.
	 * @return la longueur du panel
	 */
	public int getPanelHeight() {
		return this.panelHeight;
	}
	 
	/**
	 * Acceder a la liste des panels de la fenetre en lecture.
	 * @return la liste des panels de la fenetre
	 * @see PanelListe
	 */
	public PanelListe getPanels() {
		return this.panels;
	}  	
}