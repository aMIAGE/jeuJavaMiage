package utils;
import javax.swing.JButton;

public class Bouton extends JButton{	
	private static final long serialVersionUID = 1L;
	private String name;
	
	  public Bouton(String str){
	    super(str);
	    this.name = str;
	  }
}