package elmtJeu;

import java.util.Vector;

import main.Jeu;

/**
 * La classe Zone represente une zone de jeu.
Une zone est caracterisee par les proprietes suivantes :
 * <ul>
 * <li>Une image</li>
 * <li>Un nom.</li>
 * <li>Un tableau contenant ses dimensions.</li>
 * <li>Un tableau contenant les dimensions des portes.</li>
 * <li>La longueur du panel.</li>
 * <li>Une reference vers un Timer.</li>
 * <li>Une reference vers un PanelListe : la liste des panels de la fenetre.</li>
 * </ul>
 *
 */
public class Zone {
	/**
	 * Chaine qui contient le chemin vers l'image de la zone.
	 */
	private String imgSource;
	/**
	 * Chaine qui contient le nom de la zone.
	 */
	private String nom;
	/**
	 * Tableau qui contient les dimensions de la zone.
	 */
	private Vector<Integer> dimZone = new Vector<Integer>();
	/**
	 * Tableau qui contient les dimensions de la porte.
	 */
	private Vector<Integer> dimPorte = new Vector<Integer>();
	/**
	 * Tableau qui contient les dimensions de la porte2.
	 */
	private Vector<Integer> dimPorte2 = new Vector<Integer>();
	/** 
	 * Chaine qui contient le nom de l'orientation de la porte
	 */
	private String orientPorte;
	/** 
	 * Chaine qui contient le nom de l'orientation de la porte2
	 */
	private String orientPorte2;
	/**
	 * Tableau qui contient les etapes de la zone.
	 */
	private Vector<Etape> zoneEtapes = new Vector<Etape>();
	/**
	 * Tableau qui contient les instructions de la zone.
	 */
	private Vector<String> zoneInstructions = new Vector<String>();
	/**
	 * Entier qui contient le numero de l'instruction.
	 */
	private int nbInstruction;
	
	/**
	 * Constructeur Zone, initialise les proprietes de Zone.
	 * @param zone le tableau de zones
	 * @param vEtapes le tableau des etapes
	 * @param vInstructions le tableau des instructions
	 */
	public Zone( Vector<String> zone,  Vector<Etape> vEtapes, Vector<String> vInstructions) {
		this.nom = zone.get(0);
		this.imgSource = zone.get(0);
		this.dimZone.add(Integer.parseInt(zone.get(1)));
		this.dimZone.add(Integer.parseInt(zone.get(2)));
		this.dimZone.add(Integer.parseInt(zone.get(3)));
		this.dimZone.add(Integer.parseInt(zone.get(4)));
		this.dimPorte.add(Integer.parseInt(zone.get(6)));
		this.dimPorte.add(Integer.parseInt(zone.get(7)));
		this.orientPorte = zone.get(5);
		if(zone.size() > 8) {
			this.dimPorte2.add(Integer.parseInt(zone.get(9)));
			this.dimPorte2.add(Integer.parseInt(zone.get(10)));
			this.orientPorte2 = zone.get(8);
		}else {
			this.dimPorte2=null;
			this.orientPorte2 = null;
		}
		this.zoneEtapes = vEtapes;
		this.trierEtape();
		this.zoneInstructions = vInstructions;
		this.nbInstruction = 0;
		
	}
	
	/**
	 * Acceder au chemin de l'image en lecture.
	 * @return le chemin de l'image
	 */
	public String getImgSource() {
		return imgSource;
	}
	
	/**
	 * Acceder au nom de la zone en lecture.
	 * @return le nom de la zone
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Acceder aux dimensions de la zone en lecture.
	 * @return un tableau des dimensions de la zone
	 */
	public Vector<Integer> getDim(){
		return this.dimZone;
	}
	
	/**
	 * Acceder au dimensions de la porte en lecture
	 * @return un tableau des dimensions de la porte
	 */
	public Vector<Integer> getDimPorte() {
		return this.dimPorte;
	}
	
	/**
	 * Acceder a l'orientation de la porte en lecture
	 * @return orientation de la porte
	 */
	public String getOrientPorte() {
		return this.orientPorte;
	}
	
	/**
	 * Acceder aux dimensions de la porte2 en lecture
	 * @return orientation de la porte
	 */
	public Vector<Integer> getDimPorte2() {
		return this.dimPorte2;
	}
	
	/**
	 * Acceder a l'orientation de la porte2 en lecture
	 * @return orientation de la porte
	 */
	public String getOrientPorte2() {
		return this.orientPorte2;
	}
	
	/**
	 * Permet de recuperer l'instruction correspondant a la zone.
	 * @return la chaine contenant l'instruction
	 */
	public String getInstructions() {
		String inst = this.zoneInstructions.get(this.nbInstruction);
		if(this.nbInstruction < this.zoneInstructions.size()-1) {
			this.nbInstruction++;
		}
		else {
			this.nbInstruction = 0;
		}
		
		return inst;
	}
	
	/**
	 * Trie le tableau d'etapes
	 */
	private void trierEtape() {
		if(this.zoneEtapes!=null) {
			Etape tampon;
			for(int j = 0; j < this.zoneEtapes.size() - 1 ; j++ ) {
				for(int i = j; i < this.zoneEtapes.size() - 1; i++) {
					if(this.zoneEtapes.get(i).getNumEtape() > this.zoneEtapes.get(i+1).getNumEtape() ) {
						tampon = this.zoneEtapes.get(i);
						this.zoneEtapes.add(i, this.zoneEtapes.get(i+1));
						this.zoneEtapes.add(i+1, tampon);
					}
	
				}
			}
		}			
	}
	
	/**
	 * Teste s'il y a une etape pour la zone.
	 */
	public String isEtape() {
		if(this.zoneEtapes!=null && this.zoneEtapes.size()!= 0) {
			if(Jeu.getJoueur().getCurrentEtape() == this.zoneEtapes.get(0).getNumEtape()) {
				this.zoneEtapes.get(0).launchEnigme();
				this.zoneEtapes.remove(0);
				return("oui");
			}
			else {
				return "perdu";						
			}
		}
		return "non";
	}			 
}