package elmtJeu;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.Zone;

public class Etape {
	private String enigme;
	private String zone;
	private boolean check;
	private int numEtape;
	
	public Etape(String nvZone, String nvEnigme, int num) {
		this.enigme = nvEnigme;
		this.zone = nvZone;
		this.check = false;
		this.numEtape = num;
	}
	
	public String getEnigme() {
		return this.enigme;
	}
	
	public String getNomZone() {
		return this.zone;
	}
	
	public int getNumEtape() {
		return this.numEtape;
	}
	
	public void setCheckStatut() {
		this.check = true;
	}
	
	public void launchEnigme(Fenetre fenetre) {
		 Enigme enigme = new Enigme(fenetre, "le titre", false, this.getEnigme(), "");
	}
	
	
}
