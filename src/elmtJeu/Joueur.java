package elmtJeu;

import utils.Chrono;

//un joueur avec sa position sur la carte, 
public class Joueur{
	private Chrono chrono;
	private int posX;
	private int posY;
	private int currentEtape;
	private String currentZone;
	
	public Joueur(int x, int y) {
		this.posX = x;
		this.posY = y;
		this.chrono = null;
		this.currentEtape = 0;
		this.currentZone = "ext";
	}
	
	
	 public int getPosX() {
		 return this.posX;
	 }
	 
	 public int getPosY() {
		 return this.posY;
	 }
	 
	 public void setPosX(int x) {
		 this.posX = x;
	 }
	 
	 public void setPosY(int y) {
		 this.posY = y;
	 }
	 
	 public void setChrono(Chrono nvChrono) {
		 this.chrono = nvChrono;
	 }
	 
	 public Chrono getChrono() {
		 return this.chrono;
	 }
	 
	 public void avancer() {
		  this.setPosY(this.getPosY() - 20);
		
	}
	 
	 public void reculer() {
		 this.setPosY(this.getPosY()+20);
	 }
	 
	 public void allerADroite() {
		 this.setPosX(this.getPosX()+20);
	 }
	 
	 public void allerAGauche() {
		 this.setPosX(this.getPosX()-20);
	 }
	 
	public int getCurrentEtape() {
		return this.currentEtape;
	}
	
	public void setCurrentEtape() {
		this.currentEtape ++;
	}
	
	public String getCurrentZone() {
		return this.currentZone;
	}
		
	public void setCurrentZone(String nomZone) {
		this.currentZone = nomZone;
	}
	
}
