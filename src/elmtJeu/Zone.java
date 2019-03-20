package elmtJeu;

import java.util.Vector;

import main.Jeu;

public class Zone {
	private String imgSource;
	private String nom;
	private Vector<Integer> dimZone = new Vector<Integer>();
	private Vector<Integer> dimPorte = new Vector<Integer>();
	private Vector<Integer> dimPorte2 = new Vector<Integer>();
	private String orientPorte;
	private String orientPorte2;
	private Vector<Etape> zoneEtapes = new Vector<Etape>();
	private Vector<String> zoneInstructions = new Vector<String>();
	private int nbInstruction;
	
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
	
	public String getImgSource() {
		return imgSource;
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public Vector<Integer> getDim(){
		return this.dimZone;
	}
	public Vector<Integer> getDimPorte() {
		return this.dimPorte;
	}
	
	public String getOrientPorte() {
		return this.orientPorte;
	}
	public Vector<Integer> getDimPorte2() {
		return this.dimPorte2;
	}
	
	public String getOrientPorte2() {
		return this.orientPorte2;
	}
	
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
