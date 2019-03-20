package utils;

import java.util.Hashtable;

import main.Jeu;
import panel.JeuPanel;

//le panel du chrono ainsi que les méthodes utiles à l'affichage du temps de jeu
/**
 * <b>Chrono est une classe heritant de Thread</b>
 * 
 * Comprend le panel du chronometre ainsi que les methodes utiles a l'affichage du temps du jeu.
 */
public class Chrono extends Thread{
	private long chrono;
	private long time;
	private JeuPanel jeuPanel;
	private Hashtable<String, Integer> hTime = new Hashtable<String, Integer>(3);

	/**
	 *Constructeur Chrono
	 *<p>
	 *A la construction d'un objet Chrono, sa valeur est s'exprime en millisecondes, 
	 *son temps est initialise a 0 et s'affiche dans le panel JeuPanel
	 *</p>
	 *@param jp le panel dans lequel sera affiche le bouton
	 */
	public Chrono(JeuPanel jp) {
		this.chrono = System.currentTimeMillis();
		this.jeuPanel = jp;
		this.time = 0;
		
	}
	
	/**
	 * <h1>Obtenir le temps</h1>
	 *Cette methode permet de recuperer le temps qui defile au chronometre.
	 */
	public Hashtable<String,Integer> getTime() {
		this.time = (System.currentTimeMillis()-this.chrono)/1000;
		this.hTime.put("h", (int) this.time/3600);
		this.hTime.put("m",(int)(this.time % 3600) / 60);
		this.hTime.put("s", (int)this.time%60);
		
		return hTime;
	}
	
	/**
	 * <h1>Obtenir le temps sous forme de chaine</h1>
	 *Cette methode permet de recuperer le temps qui defile au chronometre sous forme de chaine de caractere
	 *pour afficher de maniere convenable le temps (heure/minute/seconde).
	 */
	public String getStringTime() {
		this.getTime();
		String stringTime="";
        if(hTime.get("h")>0) {stringTime+=hTime.get("h")+" h ";}
        if(hTime.get("m")>0) {stringTime+=hTime.get("m")+" min ";}
        if(hTime.get("s")>0) {stringTime+=hTime.get("s")+" s";}
        if(hTime.get("h")<=0 && hTime.get("m")<=0 && hTime.get("s")<=0) {stringTime="0 s";}
        
        return stringTime;
	}
	
	/**
	 * <h1>Actualiser le chrono</h1>
	 *Cette methode permet de faire defiler le chronometre en temps reel.
	 */
	public void run() {
		String ancienTemps = this.getStringTime();
		while(this.time < Jeu.DUREE * 60) {
			this.getTime();
			if(!ancienTemps.contentEquals(this.getStringTime()))
			{
			ancienTemps = this.getStringTime();
			if(this.jeuPanel.getId()=="chrono") {
				this.jeuPanel.setTexte(ancienTemps,"chrono");
				this.jeuPanel.repaint();
			}
			
			}
			
		}
		
		
	}
	

}