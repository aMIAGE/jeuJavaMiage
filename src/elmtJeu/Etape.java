package elmtJeu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Timer;
import main.Jeu;

/**
 * La classe Etape correspond a une etape dans le jeu.
 * Une etape est caracterisee par les proprietes suivantes :
 * <ul>
 * <li>Une zone.</li>
 * <li>Un numero d'etape.</li>
 * <li>Une enigme.</li>
 * <li>Un booleen creuser.</li>
 * <li>Un timer.</li>
 * </ul>
 * @see Enigme
 */
public class Etape {
	/**
	 * Chaine qui contient le nom de la zone.
	 * @see #getNomZone()
	 */
	private String zone;
	/**
	 * Entier qui represente le numero de l'etape.
	 * @see #getNumEtape()
	 */
	private int numEtape;
	/**
	 * Une reference vers une instance d'Enigme.
	 * @see Enigme
	 */
	private Enigme enigme;
	/**
	 * Un booleen indiquant si l'enigme declenche le mini-jeu creuser.
	 */
	private Boolean creuser;
	/**
	 * Une reference vers une instance de Timer.
	 */
	private Timer timer;
	
	
	/**
	 * Constructeur Etape, initialise les proprietes a l'aide des parametres.
	 * @param etape le nom de la zone
	 * @param enigme le numero de l'etape
	 * @param creuser true si l'etape a le mini-jeu creuser, faux sinon
	 */
	public Etape( Vector<String> etape, Enigme enigme, Boolean creuser) {
		this.zone = etape.get(0);
		this.numEtape = Integer.parseInt(etape.get(1));
		this.enigme = enigme;
		this.creuser = creuser;
	}
	
	/**
	 * Acceder a la zone en lecture
	 * @return le nom de la zone
	 */
	public String getNomZone() {
		return this.zone;
	}
	
	/**
	 * Acceder au numero de l'etape en lecture
	 * @return le numero de l'etape
	 */
	public int getNumEtape() {
		return this.numEtape;
	}
	
	/**
	 * Lance une enigme.
	 * Si creuser est true, le mini-jeu creuser est lance, sinon une enigme s'affiche.
	 */
	public void launchEnigme() {
		Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Tiens toi prêt à résoudre une nouvelle enigme", "java");
		Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
		Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
		timer = new Timer(1*1000, new ActionListener() {
			  public void actionPerformed(ActionEvent a) {
				  if(creuser) {
					  Creuser.startCreuser();
				  }
				  else {
					  enigme.startEnigme();
				  }
				  Jeu.getFenetre().getPanels().getJeuPanel().setTime();
			      timer.stop();
			  }
		  });
		timer.start();
	}
}
