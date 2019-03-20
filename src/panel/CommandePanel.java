package panel;

import javax.swing.JLabel;

import main.Jeu;
import net.miginfocom.swing.MigLayout;
import utils.Action;
import utils.Bouton;

/**
 * <b>CommandePanel est une classe heritant de Panel</b>
 * <p>
 * CommandePanel est la classe representant l'espace dans lequel se trouvent les commandes (en bas, a droite).
 * CommandePanel est donc caracterise par les identifiants de Panel en plus de ceux la:
 * <ul>
 * <li>Une constante qui permet d'affecter un numero de version a la classe</li>
 * <li>Une zone d'affichage indiquant l'etape courante du joueur</li>
 * <li>Une zone d'affichage indiquant la zone courante du joueur</li>
 * <li>Une zone d'affichage indiquand le scenario de l'etape courante</li>
 * <li>Un compteur indiquant le nombre de consultation du scenario</li>
 * </ul>
 * </p>
 * <p>
 *  Un Panel fournit un espace dans lequel une application peut attacher tout autre composant, 
 *  y compris d'autres Panels.
 *  </p>
 * @see Panel
 */
public class CommandePanel extends Panel{
	/**
	 * Le serialVersionUID (qui permet d'affecter un numero de version a la classe)
	 * est une propriete de classe et une constante (donc non modifiable)
	 */
	private static final long serialVersionUID = 1L;
	JLabel etapeLabel;
	JLabel zoneLabel;
	JLabel scenarLabel;
	private int nbScenar;
/**
 * 	Constructeur CommandePanel
 * <p>
 * A la construction d'un objet CommandePanel, 
 * un Panel est cree avec une image, une largeur et une longueur,
 * un gestionnaire de disposition est cree
 * le nombre de consultation du scenario est initialise a 0, 
 * des boutons de directions et des boutons de commandes sont ajoutes,
 * la zone d'affichage de l'etape courante du joueur est creee,
 * la zone d'affichage de la zone courante du joueur est creee,
 * les zones d'affichage de l'etape, de la zone et du nombre de consultation courants sont ajoutes,
 * </p>
 * @param nvImage
 * 					l'image de fond du panel
 * @param nvLargeur
 * 					la largeur du panel
 * @param nvLongueur
 * 					la longueur du panel
 * @param nbEtapes
 * 					le nombre total d'etapes du jeu
 * 
 * @see Fenetre
 */
	public CommandePanel(String nvImage, int nvLargeur, int nvLongueur, int nbEtapes) {
		
		super(nvImage, nvLargeur, nvLongueur);
		this.nbScenar = 0;
	    this.setLayout(new MigLayout());
	    this.add(this.creerBoutonsDirection(), "cell 0 0 1 2");
	    this.add(this.creerBoutonsCommandes(), "cell 1 0 1 2");
	    etapeLabel = new JLabel("vous en etes à l'etape : "+Jeu.getJoueur().getCurrentEtape()+" sur "+ nbEtapes);
	    zoneLabel = new JLabel("vous etes dans " + Jeu.getJoueur().getCurrentZone());
	    scenarLabel = new JLabel("vous avez regardé le scénario " + this.nbScenar +" fois sur 5");
	    this.add(etapeLabel, "cell 0 2 2 1");
	    this.add(zoneLabel, "cell 0 3 2 1");
	    this.add(scenarLabel, "cell 0 4 2 1");
	    
	    
		
	}
	
	/**
	 * Cree les bouttons commande (qui permettent au joueur de consulter des informations)
	 * @return le Panel de commande cree
	 */
	public Panel creerBoutonsCommandes() {
		Panel commandes = new Panel("blanc", 600, 450);
		commandes.setLayout(new MigLayout());
		
		Bouton java = new Bouton("Appeler Java");
		java.addActionListener(new Action("java") );
		commandes.add(java, "cell 0 0");
		
		
		Bouton comm = new Bouton("Voir le scénario");
		comm.addActionListener(new Action("scenar"));
		commandes.add(comm, "cell 0 1");

		
		Bouton boutChrono = new Bouton("Voir le chrono");
		boutChrono.addActionListener(new Action("chrono"));
		commandes.add(boutChrono, "cell 0 2");
		
		return commandes;
	}
		/**
		 * Cree les bouttons direction (qui permettent au joueur de se deplacer)
		 * @return le Panel de direction cree
		 */
		public Panel creerBoutonsDirection() {
			Panel direction = new Panel("blanc", 600, 450);
			direction.setLayout(new MigLayout());
			
			
			Bouton av = new Bouton("Avancer");
		    av.addActionListener(new Action("av"));
		    
		    Bouton gauche = new Bouton("Gauche");
		    gauche.addActionListener(new Action("gauche"));
		    
		    Bouton droite = new Bouton("Droite");
		    droite.addActionListener(new Action("droite"));

		    Bouton rec = new Bouton("Reculer");
		    rec.addActionListener(new Action("rec"));
		    
		    direction.add(av, "cell 1 0");
		    direction.add(gauche, "cell 0 1");
		    direction.add(droite, "cell 2 1");
		    direction.add(rec, "cell 1 2");
		    
		    return direction;
		}
		
		/**
		 * Met a jour la zone d'affichage de l'etape courante du joueur
		 */
		public void setEtapeLabel() {
			this.etapeLabel.setText("vous en etes à l'etape : "+Jeu.getJoueur().getCurrentEtape()+" sur "+Jeu.getFenetre().getPanels().getZonePanel().getNbEtape());
		}
		
		/**
		 * Met a jour la zone d'affichage de la zone courante du joueur 
		 * @param nom
		 * 				la zone courante du joueur
		 */
		public void setZoneLabel(String nom) {
			this.zoneLabel.setText("vous etes dans " + nom);
		}
		
		/**
		 * Met a jour la zone d'affichage du nombre de consultation du scenario
		 */
		public void setScenarLabel() {
			this.scenarLabel.setText("vous avez regardé le scénario " + this.nbScenar +" fois sur 5");
		}
		
		/**
		 * Recupere le nombre de consultation du scenario
		 * @return le nombre de consultation du scenario
		 */
		public int getNbScenar() {
			return this.nbScenar;
		}
		
		/**
		 * Met a jour le nombre de consultation du scenario (rajoute +1)
		 */
		public void setNbScenar() {
			this.nbScenar++;
		}

}
