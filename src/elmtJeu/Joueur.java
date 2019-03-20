package elmtJeu;
import main.Jeu;
import panel.ZonePanel;
import utils.Chrono;

/**
 * La classe Joueur correspond a un joueur du jeu.
 * Un joueur est caracterise par les proprietes suivantes :
 * <ul>
 * <li>Un panel.</li>
 * <li>Une largeur.</li>
 * <li>Une longueur.</li>
 * <li>La largeur du panel.</li>
 * <li>La longueur du panel.</li>
 * <li>Une reference vers un Timer.</li>
 * <li>Une reference vers un PanelListe : la liste des panels de la fenetre.</li>
 * </ul>
 *
 */
public class Joueur{
	/**
	 * Une reference vers une instance de Chrono.
	 * @see Chrono
	 * @see #getChrono()
	 * @see #setChrono(Chrono nvChron)
	 */
	private Chrono chrono;
	/**
	 * Entier representant la position sur l'axe x.
	 * @see #getPosX()
	 * @see #setPosX(int x)
	 * @see #allerADroite()
	 * @see #allerAGauche()
	 */
	private int posX;
	/**
	 * Entier representant la position sur l'axe y.
	 * @see #getPosY()
	 * @see #setPosY(int y)
	 * @see #avancer()
	 * @see #reculer()
	 */
	private int posY;
	/**
	 * Entier correspondant au numero de l'etape.
	 * @see Etape
	 * @see #getCurrentEtape()
	 * @see #setCurrentEtape()
	 */
	private int currentEtape;
	/**
	 * Chaine correspondant au nom de la zone.
	 * @see #getCurrentZone()
	 * @see #setCurrentZone(String nomZone)
	 */
	private String currentZone;
	
	/**
	 * Constructeur de Joueur, initialise les proprietes.
	 * @param x la position sur l'axe x
	 * @param y la position sur l'axe y
	 */
	public Joueur(int x, int y) {
		this.posX = x;
		this.posY = y;
		this.chrono = null;
		this.currentEtape = 0;
		this.currentZone = "extérieur";
	}
	
	/**
	 * Acceder a la position sur l'axe x en lecture.
	 * @return la position sur l'axe x
	 */
	public int getPosX() {
		return this.posX;
	}
	 
	/**
	 * Acceder a la position sur l'axe y en lecture.
	 * @return la position sur l'axe y
	 */
	public int getPosY() {
		return this.posY;
	}
	 
	/**
	 * Acceder a la position sur l'axe x en ecriture.
	 * @param x la nouvelle position sur l'axe x
	 */
	public void setPosX(int x) {
		this.posX = x;
	}
	 
	/**
	 * Acceder a la position sur l'axe y en ecriture.
	 * @param y la nouvelle position sur l'axe y
	 */
	public void setPosY(int y) {
		this.posY = y;
	}
	 
	/**
	 * Initialiser le chrono.
	 * @param nvChrono le chrono
	 */
	public void setChrono(Chrono nvChrono) {
		this.chrono = nvChrono;
	}
	 
	/**
	 * Acceder au chrono en lecture.
	 * @return le chrono
	 */
	public Chrono getChrono() {
		return this.chrono;
	}
	 
	/**
	 * Fait avancer le joueur en modifiant sa position sur l'axe y.
	 */
	public void avancer() {
		this.posY = this.posY-10;
		
	}
	 
	/**
	 * Fait reculer le joueur en modifiant sa position sur l'axe y.
	 */
	public void reculer() {
		this.posY = this.posY+10;
	}
	 
	/**
	 * Le joueur tourne va a droite en modifiant sa position sur l'axe x.
	 */
	public void allerADroite() {
		this.posX = this.posX + 10;
	}
	 
	/**
	 * Le joueur tourne va a gauche en modifiant sa position sur l'axe x.
	 */
	public void allerAGauche() {
		this.posX = this.posX - 10;
	}
	 
	/**
	 * Acceder au numero de l'etape en lecture.
	 * @return le numero de l'etape
	 */
	public int getCurrentEtape() {
		return this.currentEtape;
	}
	
	/**
	 * Incremente le numero de l'etape de 1
	 */
	public void setCurrentEtape() {
		this.currentEtape ++;
	}
	
	/** 
	 * Acceder au nom de la zone en lecture.
	 * @return le nom de la zone
	 */
	public String getCurrentZone() {
		return this.currentZone;
	}
		
	/**
	 * Acceder au nom de la zone en ecriture.
	 * @param nomZone le nom de la zone
	 */
	public void setCurrentZone(String nomZone) {
		this.currentZone = nomZone;
	}
	
	/**
	 * Teste si le joueur essaye de changer de zone.
	 * @return vrai si le joueur essaye de changer de zone, faux sinon
	 */
	public boolean isChangementZone() {
		//renvoie vrai si le joueur essaye de changer de zone 
		ZonePanel zonePanel = Jeu.getFenetre().getPanels().getZonePanel();
		Zone currentZone = zonePanel.getCurrentZone(); 
		if(currentZone != null) {
			return !currentZone.getNom().equals(this.currentZone);
		}
		else{
			return true;//ici le joueur va etre arreté par une collision ou autre
		}	
	}
}
