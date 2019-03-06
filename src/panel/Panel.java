package panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import elmtJeu.Joueur;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	String image;
	int largeur;
	int longueur;
	Joueur joueur;
	
	public Panel(String nvImage,int nvLargeur, int nvLongueur, Joueur nvJoueur) {
		this.image = "src/images/"+nvImage+".jpg";
		this.longueur = nvLongueur;
		this.largeur = nvLargeur;
		this.setPreferredSize(new Dimension(this.longueur, this.largeur));
		this.joueur = nvJoueur;
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
	}
	
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
