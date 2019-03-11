package utils;

import java.util.Vector;

import elmtJeu.Etape;

//servent à creer des objets zones : leur nom, l'image associé, les etapes qu'elles contiennent et les objets 
public class Zone {
	private String imgSource;
	private String nom;
	private int num;
	private Vector<Integer> dimZone = new Vector<Integer>();
	//private Vector<Objet> zoneObjects;
	private Vector<Etape> zoneEtapes = new Vector<Etape>();
	private Vector<String> zoneInstructions = new Vector<String>();
	private int numInstruction;
	private int nbEtape;
	
	public Zone(String nvImgSource, int numZone, String nvNom, Integer minX, Integer maxX, Integer minY, Integer maxY, Vector<Etape> vEtapes, Vector<String> vInstructions) {
		this.nom = nvNom;
		this.num = numZone;
		this.imgSource = nvImgSource;
		this.dimZone.add(minX);
		this.dimZone.add(maxX);
		this.dimZone.add(minY);
		this.dimZone.add(maxY);
		this.zoneEtapes = vEtapes;
		this.zoneInstructions = vInstructions;
		this.numInstruction = 0;
		this.nbEtape = 0;
		
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
	
	public String getInstructions() {
		String inst = this.zoneInstructions.get(this.numInstruction);
		if(this.numInstruction < this.zoneInstructions.size()-1) {
			this.numInstruction++;
		}
		else {
			this.numInstruction = 0;
		}
		
		return inst;
	}
	
	private void trierEtape() {
		if(this.zoneEtapes!=null) {
			Vector<Etape> nvVect = new Vector<Etape>();
			Etape minEtape = this.zoneEtapes.get(0);
			int index = 0;
			while(this.zoneEtapes.size() != 0)
			for(int i = 1; i < this.zoneEtapes.size(); i++) {
				if(this.zoneEtapes.get(i).getNumEtape() < minEtape.getNumEtape()) {
					minEtape = this.zoneEtapes.get(i);
					index = i;
				}
				nvVect.add(minEtape);
				this.zoneEtapes.remove(index);
			}
			
			this.zoneEtapes = nvVect;
		}
		
		
	}
	
	public Etape getEtapes(int currentEtape) {
		//si il reste des etapes a faire dans cette zone
		if(this.zoneEtapes != null) {
				if(this.zoneEtapes.get(0).getNumEtape() == currentEtape) {
					Etape etape = this.zoneEtapes.get(0);
					this.zoneEtapes.remove(0);
					return etape;
				}
				else {
					return new Etape("","",-1);
				
			}
		}
		else {
			return null;
		}
	}
	
}
