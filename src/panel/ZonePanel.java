package panel;
import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import elmtJeu.Etape;
import main.Jeu;
import utils.Zone;

//c'est le paneau qui contiendra les images des zones et qui permettra de les faire défiler 
public class ZonePanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static int nbZone=0;
	private Vector<Zone> vZone;

		
	public ZonePanel(String nvImage,int nvLargeur, int nvLongueur) throws IOException {
		super(nvImage,nvLargeur, nvLongueur);
		
		this.setLayout(new CardLayout());
		this.ajouterZone();
		
	}
	
	public void ajouterZone() throws IOException {
		//ajout de la zone couloir qui our l'instant represente tous ce qui n'est pas une piece
		this.vZone = new Vector<Zone>();
		Zone nvZone = new Zone("entree",ZonePanel.nbZone,"entree", 0, 0, 0, 0, null, null);
		this.vZone.add(nvZone);
		this.add(new Panel(nvZone.getImgSource(), this.largeur,this.longueur), nvZone.getNom());
		ZonePanel.nbZone ++;
		
		
		BufferedReader infoZones = new BufferedReader(new FileReader("src/files/zones.txt"));
		this.add(new Panel("blanc", 600,450), "blanc");
		String nom;
		Integer minX;
		Integer maxX;
		Integer minY;
		Integer maxY;
		String lineInfo = infoZones.readLine();
		
		BufferedReader etape = new BufferedReader( new FileReader("src/files/etapes.txt"));
		String lineEtape = etape.readLine();
		int numEtape;
		Vector<Etape> vEtapes;
		
		BufferedReader instructions = new BufferedReader( new FileReader("src/files/instructions.txt"));
		String lineInstructions = instructions.readLine();
		Vector<String> vInstructions;
		
		while(lineInfo != null) {
			//attention on suppose que tous les fichiers textes sont dans l'ordre et au bon format
			ZonePanel.nbZone ++;
			nom = lineInfo;
			lineInfo = infoZones.readLine();
			minX = (Integer)Integer.parseInt(lineInfo);
			lineInfo = infoZones.readLine();
			maxX = (Integer)Integer.parseInt(lineInfo);
			lineInfo = infoZones.readLine();
			minY = (Integer)Integer.parseInt(lineInfo);
			lineInfo = infoZones.readLine();
			maxY = (Integer)Integer.parseInt(lineInfo);
			
			vEtapes = new Vector<Etape>();
			while(lineEtape != null && nom.contentEquals(lineEtape)) {
				numEtape = Integer.parseInt(etape.readLine());
				lineEtape = etape.readLine();
				if(!lineEtape.contentEquals("vide")) {
					vEtapes.add(new Etape(nom, lineEtape, numEtape));
				}else {
					vEtapes = null;
				}
				lineEtape = etape.readLine(); // si on veut mettre plusieurs enigmes dans une étape on met plusieurs fois le nom
			}
			
			vInstructions = new Vector<String>();
			while(lineInstructions != null && nom.contentEquals(lineInstructions)) {
				lineInstructions = instructions.readLine();
				vInstructions.add(lineInstructions);
				lineInstructions = instructions.readLine(); 
			}
			
			
			nvZone = new Zone(nom,ZonePanel.nbZone,nom, minX, maxX, minY, maxY, vEtapes, vInstructions);
			this.vZone.add(nvZone);
			this.add(new Panel(nvZone.getImgSource(), this.largeur,this.longueur), nvZone.getNom());
			lineInfo = infoZones.readLine();
		}
		etape.close();
		instructions.close();
		infoZones.close();
	}
	
	//nomzonecourante
	public Zone getCurrentZone() {
		for(int i=1;i<ZonePanel.nbZone;i++) {
			if(Jeu.joueur.getPosX()>vZone.get(i).getDim().get(0) && Jeu.joueur.getPosX() < vZone.get(i).getDim().get(1) ) {
				if(Jeu.joueur.getPosY() > vZone.get(i).getDim().get(2) && Jeu.joueur.getPosY() < vZone.get(i).getDim().get(3)) {
					return vZone.get(i);
				}
			}
		}
		return vZone.get(0);
		//je suis dans un couloir dés que je ne suis pas dans une zone repertorié pour le moment
	}
	
	public void setZoneImage(String nom) {
		((CardLayout) this.getLayout()).show(this, nom);
	}
	
}
