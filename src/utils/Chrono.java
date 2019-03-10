package utils;

import java.util.Hashtable;

import main.Jeu;
import panel.JeuPanel;

//le panel du chrono ainsi que les méthodes utiles à l'affichage du temps de jeu
public class Chrono extends Thread{
	private long chrono;
	private long time;
	private JeuPanel jeuPanel;
	private Hashtable<String, Integer> hTime = new Hashtable<String, Integer>(3);

	public Chrono(JeuPanel jp) {
		this.chrono = System.currentTimeMillis();
		this.jeuPanel = jp;
		this.time = 0;
		
	}
	

	public Hashtable<String,Integer> getTime() {
		this.time = (System.currentTimeMillis()-this.chrono)/1000;
		this.hTime.put("h", (int) this.time/3600);
		this.hTime.put("m",(int)(this.time % 3600) / 60);
		this.hTime.put("s", (int)this.time%60);
		
		return hTime;
	}
	
	public String getStringTime() {
		this.getTime();
		String stringTime="";
        if(hTime.get("h")>0) {stringTime+=hTime.get("h")+" h ";}
        if(hTime.get("m")>0) {stringTime+=hTime.get("m")+" min ";}
        if(hTime.get("s")>0) {stringTime+=hTime.get("s")+" s";}
        if(hTime.get("h")<=0 && hTime.get("m")<=0 && hTime.get("s")<=0) {stringTime="0 s";}
        
        return stringTime;
	}
	
	public void run() {
		String ancienTemps = this.getStringTime();
		while(this.time < Jeu.DUREE) {
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