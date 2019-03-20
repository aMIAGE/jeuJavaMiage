package panel;
import java.awt.CardLayout;
import java.io.IOException;
import java.util.Vector;

import elmtJeu.Enigme;
import elmtJeu.Etape;
import elmtJeu.Zone;
import exception.HorsZoneException;
import main.Jeu;
import utils.LectureFichier;

/**
 * <b>ZonePanel est une classe heritant de Panel</b>
 * <p>
 * ZonePanel est la classe representant l'espace dans lequel se trouve l'image de la zone courante du joueur (en bas, a gauche).
 * ZonePanel est donc caracterise par les identifiants de Panel en plus de ceux la:
 * <ul>
 * <li>Une constante qui permet d'affecter un numero de version a la classe</li>
 * <li>Un vecteur de Zone</li>
 * <li>Un numero d'etape</li>
 * </ul>
 * </p>
 * <p>
 *  Un Panel fournit un espace dans lequel une application peut attacher tout autre composant, 
 *  y compris d'autres Panels.
 *  </p>
 * @see Panel
 * @see Zone
 */
public class ZonePanel extends Panel {
	/**
	 * Le serialVersionUID (qui permet d'affecter un numero de version a la classe)
	 * est une propriete de classe et une constante (donc non modifiable)
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Zone> vZone;
	private int nbEtape;

	/**
	 * Constructeur ZonePanel
	 * <p>
	 * A la construction d'un objet ZonePanel, 
	 * un Panel est cree avec une image, une largeur et une longueur,
	 * le nombre numero d'etape est initialise a 0, 
	 * une disposition lui est affectee,
	 * une zone lui est affectee,
	 * </p>
	 * @param nvImage
	 * 					l'image de fond du Panel
	 * @param nvLargeur
	 * 					la largeur du Panel
	 * @param nvLongueur
	 * 					la longueur du Panel
	 * 
	 * @throws IOException Si jamais une exception d'Entree / Sortie s'est produite. 
	 */
	public ZonePanel(String nvImage,int nvLargeur, int nvLongueur) throws IOException {
		super(nvImage,nvLargeur, nvLongueur);
		this.nbEtape = 0;
		this.setLayout(new CardLayout());
		this.ajouterZone();
		this.setZoneImage("le hall d'entrée");
		
	}
	
	/**
	 * Cree une zone a partir du fichier du meme nom
	 * @throws IOException Si jamais une exception d'Entree / Sortie s'est produite. 
	 */
	public void ajouterZone() throws IOException {
		this.vZone = new Vector<Zone>();
		Zone nvZone;
		String currentName;
		
		LectureFichier fZones = new LectureFichier("zones", 8);
		Vector<String> infosZones;
		LectureFichier fEtapes = new LectureFichier("etapes", 5);
		Vector<String> infosEtapes;
		Vector<Etape> vEtapes;
		LectureFichier fInstructions = new LectureFichier("instructions", 2);
		Vector<String> infosInstructions;
		Vector<String> vInstructions;
		LectureFichier fEnigmes = new LectureFichier("enigmes", 3);
		Vector<String> infosEnigmes;
		
		while(!fZones.isEmpty()) {
			currentName = fZones.getName();
			infosZones = fZones.getInfos();
			vEtapes = new Vector<Etape>();
			
			while(fEtapes.getName() != null && fEtapes.getName().contentEquals(currentName)){
				infosEtapes = fEtapes.getInfos();
				if(!infosEtapes.get(2).contentEquals("vide") && !infosEtapes.get(2).contentEquals("creuser")) {
					infosEnigmes = fEnigmes.getInfos();
					Enigme enigme = new Enigme("L'enigme de "+ currentName,infosEtapes.get(2), infosEnigmes.get(0), infosEnigmes.get(1), infosEnigmes.get(2),infosEtapes.get(4), infosEtapes.get(3));
					vEtapes.add(new Etape(infosEtapes, enigme, false));
					nbEtape ++;
				}
				else {
					if(infosEtapes.get(2).contentEquals("creuser")) {
						vEtapes.add(new Etape(infosEtapes, null, true));
						nbEtape ++;
					}
					else {
						vEtapes = null;
					}
				}
			}
				
			vInstructions = new Vector<String>();
			while(fInstructions.getName() != null && fInstructions.getName().contentEquals(currentName)) {
				infosInstructions = fInstructions.getInfos();
				vInstructions.add(infosInstructions.get(1));
			}
			
			nvZone = new Zone(infosZones, vEtapes, vInstructions);
			this.vZone.add(nvZone);
			this.add(new Panel(nvZone.getImgSource(), this.largeur,this.longueur), nvZone.getNom());
		
		}
			
		fZones.closeFile();
		fInstructions.closeFile();
		fEnigmes.closeFile();
		fEtapes.closeFile();
		
		
	}
	
	/**
	 * recupere le numero de l'etape
	 * @return le numero de l'etape
	 */
	public int getNbEtape() {
		return this.nbEtape;
	}
	
	/**
	 * recupere la zone courante du joueur
	 * @return la zone courante
	 */
	public Zone getCurrentZone() {
		for(int i=0;i<vZone.size();i++) {
			if(Jeu.getJoueur().getPosX()>=vZone.get(i).getDim().get(0) && Jeu.getJoueur().getPosX() <= vZone.get(i).getDim().get(1) ) {
				if(Jeu.getJoueur().getPosY() >= vZone.get(i).getDim().get(2) && Jeu.getJoueur().getPosY() <= vZone.get(i).getDim().get(3)) {
					return vZone.get(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Met a jour la zone courante du joueur
	 * @return la zone courante
	 * @throws HorsZoneException si le joueur se trouve en dehors de toutes zones predefinies
	 */
	public Zone setZone() throws HorsZoneException{
		Zone currentZone = this.getCurrentZone();
		if(currentZone == null) {
			throw new HorsZoneException("Vous n'etes pas dans une zone repertoriée");
		}
		this.setZoneImage(currentZone.getNom());
		Jeu.getJoueur().setCurrentZone(currentZone.getNom()); 
		Jeu.getFenetre().getPanels().getCommPanel().setZoneLabel(currentZone.getNom());
		return currentZone;
	}
	
	/**
	 * Met a jour l'image de la zone courante du joueur
	 * @param nom
	 * 				image du meme nom que la zone courante
	 */
	public void setZoneImage(String nom) {
		((CardLayout) this.getLayout()).show(this, nom);
	}
	
}
