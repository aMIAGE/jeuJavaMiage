package elmtJeu;
import main.Jeu;
import panel.ZonePanel;
import utils.Chrono;

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
		this.currentZone = "extérieur";

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
		 this.posY = this.posY-10;
		
	}
	 
	 public void reculer() {
		 this.posY = this.posY+10;
	 }
	 
	 public void allerADroite() {
		 this.posX = this.posX + 10;
	 }
	 
	 public void allerAGauche() {
		 this.posX = this.posX - 10;
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
	
	public boolean isChangementZone() {
		//renvoie vraie si le joueur essaye de changer de zone 
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
