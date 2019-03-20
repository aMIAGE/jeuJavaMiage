package panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * <b>Panel est une classe heritant de JPanel</b>
 * <p>
 * Panel est la classe representant 
 * Panel est donc caracterise par les identifiants de JPanel en plus de ceux la:
 * <ul>
 * <li>Une constante qui permet d'affecter un numero de version a la classe</li>
 * <li>Une image</li>
 * <li>Une largeur</li>
 * <li>Une longueur</li>
 * </ul>
 * </p>
 *
 */
public class Panel extends JPanel {
	/**
	 * Le serialVersionUID (qui permet d'affecter un numero de version a la classe)
	 * est une propriete de classe et une constante (donc non modifiable)
	 */
	private static final long serialVersionUID = 1L;
	String image;
	int largeur;
	int longueur;
	
	/**
	 * Constructeur Panel
	 * <p>
	 * A la constructiond d'un objet Panel,
	 * une image lui est affecte (image du fichier de meme nom en fonction de sa denommination)
	 * une longueur et une largeur lui sont affectes
	 * sa taille est donc mise a jour
	 * </p>
	 * @param nvImage
	 * 					l'image du Panel
	 * @param nvLargeur
	 * 					la largeur du Panel
	 * @param nvLongueur
	 * 					la longueur du Panel
	 */
	public Panel(String nvImage,int nvLargeur, int nvLongueur) {

		if(nvImage.contentEquals("les bureaux de l'administration")) nvImage = "adm";
		if(nvImage.contentEquals("le bureau du directeur")) nvImage = "dir";
		if(nvImage.contentEquals("la maison du gardien")) nvImage = "gardien";
		if(nvImage.contentEquals("au guichet")) nvImage = "guichet";
		if(nvImage.contentEquals("le hall d'entrée")) nvImage = "entree";
		if(nvImage.contentEquals("la reserve")) nvImage = "reserve";
		if(nvImage.contentEquals("la salle d'attente")) nvImage = "attente";
		if(nvImage.contentEquals("la salle des coffres")) nvImage = "coffres";
		if(nvImage.contentEquals("la salle d'impression")) nvImage = "imprimerie";
		if(nvImage.contentEquals("le couloir")) nvImage = "couloir";
		
		
		this.image = "src/images/"+nvImage+".jpg";
		this.longueur = nvLongueur;
		this.largeur = nvLargeur;
		this.setPreferredSize(new Dimension(this.longueur, this.largeur));
		if(this.largeur == 500 && this.longueur == 350) {
			this.setBorder(new BevelBorder(BevelBorder.RAISED));
		}
	}
	
	/**
	 * Parametre la disposition du contenu du Panel
	 */
	 public void paintComponent(Graphics g){
		 super.repaint();
		 		try {
				      Image img = ImageIO.read(new File(this.image));
				      g.drawImage(img, 0,0 , this.largeur, this.longueur, this);
				     
				    } catch (IOException e) {
				      e.printStackTrace();
				    }   
				 
	}

}
