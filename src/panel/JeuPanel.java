package panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;
import main.Jeu;
import utils.LectureFichier;

/**
 * <b> JeuPanel est une classe heritant de Panel</b>
 * <p>
 * JeuPanel est la classe represantant l'espace dans lequel se trouve les informations du jeu comme le chrono (en haut a droite).
 * JeuPanel est donc caracterise par les identifiants de Panel en plus de ceux la:
 * <ul>
 * <li>Une constante qui permet d'affecter un numero de version a la classe</li>
 * <li>Un texte</li>
 * <li>Une image</li>
 * <li>Un identifiant</li>
 * <li>Un vecteur</li>
 * </ul>
 * </p>
 * <p>
 *  Un Panel fournit un espace dans lequel une application peut attacher tout autre composant, 
 *  y compris d'autres Panels.
 *  </p>
 * @see Panel
 */
public class JeuPanel extends Panel {
	/**
	 * Le serialVersionUID (qui permet d'affecter un numero de version a la classe)
	 * est une propriete de classe et une constante (donc non modifiable)
	 */
	private static final long serialVersionUID = 1L;
	private String txt;
	private String image;
	private String id;
	Vector<String> vScenario;
	
	/**
	 * Constructeur JeuPanel
	 * <p> 
	 * A la construction d'un objet JeuPanel, 
	 * un Panel est cree avec une image, une largeur et une longueur,
	 * un nouveau texte lui est affecte
	 * une image (du fichier du meme nom) lui est affectee
	 * un nouvel identifiant lui est affecte
	 * un scenario lui est affecte
	 * </p>
	 * @param nvImage
	 * 					l'image de fond du Panel
	 * @param nvLargeur
	 * 					la largeur du Panel
	 * @param nvLongueur
	 * 					la longueur du Panel
	 * @param nvTxt
	 * 					le texte du Panel 
	 * @param nvId
	 * 					l'identifiant du Panel
	 * @throws IOException Si jamais une exception d'Entree / Sortie s'est produite. 
	 * @see Fenetre
	 */
	public JeuPanel(String nvImage, int nvLargeur, int nvLongueur, String nvTxt, String nvId) throws IOException{
		super("blanc", nvLargeur, nvLongueur);
		this.txt = nvTxt;
		this.image = "src/images/"+nvImage+".jpg";
		this.id = nvId;
		this.createScenario();
		
	}
	
	/**
	 * Cree un scenario a partir du fichier du meme nom
	 * @throws IOException Si jamais une exception d'Entree / Sortie s'est produite. 
	 */
	public void createScenario() throws IOException {
		LectureFichier scenFichier = new LectureFichier("scenario", 17); //attention au nombre de ligne
		this.vScenario = scenFichier.getInfos();
	}
	
	/**
	 * Recupere l'identifiant du Panel
	 * @return l'identifiant du Panel
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Met a jour l'image du panel
	 * @param source
	 * 					le nom de l'image
	 */
	public void setImage(String source) {
		this.image= new String("src/images/"+source+".jpg");
	}
	
	/**
	 * Met a jour le texte du Panel
	 * @param texte
	 * 				le nouveau texte
	 * @param nvId
	 * 				le nouvel identifiant
	 */
	public void setTexte(String texte, String nvId) {
		this.txt = new String(texte);
		this.id = nvId;
	}
	
	
	/**
	 * Met a jour le chronometre
	 */
	public void setTime() {
		this.setTexte(Jeu.getJoueur().getChrono().getStringTime(), "chrono");
		this.setImage("chrono");
		this.repaint();
	}
	
	
	/**
	 * Parametre la disposition du contenu du Panel
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Image img;
		
		if(this.id.contentEquals("chrono")) {
			try {
				img = ImageIO.read(new File(this.image));
				g.drawImage(img, 0,0,this.largeur,this.longueur,this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			g.setColor(Color.black);
			Font police = new Font("Calibri", Font.BOLD, 25);
			g.setFont(police);
			g.drawString(this.txt, 230, 230);
		}
		if(this.id.contentEquals("java")) {
			try {
				img = ImageIO.read(new File(this.image));
				g.drawImage(img, 0,0,this.largeur/3,this.longueur/3,this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			g.setColor(Color.black);
			Font police = new Font("Calibri", Font.BOLD, 15);
			g.setFont(police);
			g.drawString(this.txt, 20, this.longueur/2);
		}
		if(this.id.contentEquals("scenar")) {
			g.setColor(Color.black);
			Font police = new Font("Calibri", Font.BOLD, 15);
			g.setFont(police);
			int nbLignes = Jeu.getJoueur().getCurrentEtape()+1*4;
			int j = 1;
			for(int i =Jeu.getJoueur().getCurrentEtape(); i < nbLignes ; i++) {
				if(i < vScenario.size()){
					g.drawString(vScenario.get(i), 10, 30+(20*(j)));
				}
				j++;
			}
			
			
		}
		
	}
	

}
