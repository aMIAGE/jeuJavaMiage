package panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JeuPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private String txt;
	private String image;
	private String id;
		
	public JeuPanel(String nvImage, int nvLargeur, int nvLongueur, String nvTxt, String nvId){
		super("blanc", nvLargeur, nvLongueur,null);
		this.txt = nvTxt;
		this.image = "src/images/"+nvImage+".jpg";
		this.id = nvId;
		
	}
	
	public void setImage(String source) {
		this.image= new String("src/images/"+source+".jpg");
	}
	
	public void setTexte(String texte, String nvId) {
		this.txt = new String(texte);
		this.id = nvId;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(this.id == "chrono") {
			g.setColor(Color.blue);
			Font police = new Font("Arial", Font.BOLD, 40);
			g.setFont(police);
		}
		else {
			g.setColor(Color.black);
		}
		g.drawString(this.txt, this.largeur/2+50, this.longueur/2);
		Image img;
		try {
			img = ImageIO.read(new File(this.image));
			g.drawImage(img, 0,0,this.largeur/2,this.longueur/2,this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}