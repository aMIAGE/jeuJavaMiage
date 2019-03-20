package utils;

import java.io.IOException;
import javax.swing.JOptionPane;
import main.Jeu;

/**
 * <b>MsgPerdu est une classe heritant de JOptionPane</b>
 * MsgPerdu ouvre une boite de dialogue pour indiquer au joueur qu'il a perdu
 */
public class MsgPerdu extends JOptionPane{
	private static final long serialVersionUID = 1L;
	
	/**
	 *Constructeur MsgPerdu
	 *<p>
	 *A la creation de l'objet MsgPerdu, un Message est cree avec le texte passe en parametre
	 *</p>
	 *@param txt texte qui figure dans le message
	 */
	public MsgPerdu(String txt) {		
    	createMessage(txt);
	}
	
	/**
	 * <h1>Creation d'un message</h1>
	 *Cette methode ouvre une boite de dialogue contenant un texte de defaite.
	 */
	public static void createMessage(String txt) {
		int option = JOptionPane.showConfirmDialog(null, txt, "Java De Papel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
    	if(option == JOptionPane.OK_OPTION){
    	  	Jeu.getFenetre().dispose();
    	  	try {
				Jeu.creationJeu();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		System.exit(0);
    	}
	}
}
