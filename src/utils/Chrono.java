package utils;

import java.util.Hashtable;

import panel.Panel;

//le panel du chrono ainsi que les méthodes utiles à l'affichage du temps de jeu
public class Chrono{
	private long chrono;
	private long time;
	private Hashtable<String, Integer> hTime = new Hashtable<String, Integer>(3);

	public Chrono(long nvChrono) {
		this.chrono = nvChrono;
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
	

}