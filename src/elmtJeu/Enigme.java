package elmtJeu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.Jeu;
import net.miginfocom.swing.MigLayout;
import panel.Panel;
import utils.MsgIntro;
import utils.MsgPerdu;

/**
 * <b>Enigme est la classe représentant une fenetre Enigme.</b>
 * <p>
 * Une fenetre enigme est caracterisee par les proprietes suivantes :
 * <ul>
 * <li>Une image.</li>
 * <li>Une question.</li>
 * <li>Une reponse.</li>
 * <li>Un intitule.</li>
 * <li>Un message si l'on gagne.</li>
 * <li>Une message si l'on perd.</li>
 * <li>Un booleen indiquant si on a repondu juste.</li>
 * <li>Un entier comptant le nombre d'essais par enigme.</li>
 * </ul>
 */

public class Enigme extends JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * Chaine qui contient la question de l'enigme.
	 */
	private String question;
	/**
	 * Chaine qui contient la reponse a l'enigme.
	 */
	private String reponse;
	/** 
	 * Chaine qui contient le chemin de l'image de l'enigme.
	 */
	private String image;
	/**
	 * Chaine qui contient l'intitule de l'enigme
	 */
	private String intituleEnigme;
	/**
	 * Chaine qui contient le message si on repond faux.
	 */
	private String messagePerdant;
	/**
	 * Chaine qui contient le message si on repond juste.
	 */
	private String messageGagnant;
	/**
	 * Booleen ayant pour valeur true si on a repondu juste, faux sinon.
	 */
	private boolean check;
	/**
	 * Entier qui compte le nombre d'essais par enigme. Ne peut etre superieur a 3.
	 */
	private int nbEssai;
	
	
	/**
	 * Constructeur Enigme, initialise la fenztre et ses proprietes.
	 * @param title titre de la fenetre
	 * @param intituleEnigme intitule de l'énigme pour l'etape
	 * @param question question de l'enigme
	 * @param reponse reponse de l'enigme
	 * @param image chemin de l'image
	 * @param msgPerdant message si on perd
	 * @param msgGagnant message si on gagne
	 */
	public Enigme(String title,String intituleEnigme, String question, String reponse, String image, String msgPerdant, String msgGagnant){
		    super(Jeu.getFenetre(), title, true);
		    this.question = question;
		    this.reponse = reponse;
		    this.image = image;
		    this.intituleEnigme = intituleEnigme;
		    this.messagePerdant = msgPerdant;
		    this.messageGagnant = msgGagnant;
		    this.check = false;
		    this.nbEssai = 0;
		    
		    this.setSize(600, 400);
		    this.setLocationRelativeTo(null);
		    this.setResizable(false);
		    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);	    
		  }
	
	/**
	 * Initialise les objets a afficher sur la fenetre. Gere l'evenement de la saisie de la reponse par l'utilisateur.
	 * Instancie les differents objets <code>Panel, JTextArea, JLabel, JTextField, JButton</code>. Gere l'evenement de la saisie de reponse par le joueur. Si la reponse est juste, une fenetre dialog apparait avec le message gagnant. S'il s'agit de la derniere etape du jeu, une autre fenetre dialog s'affiche avec un message de fin. Sinon, si la reponse est fausse ou que le nombre d'essais a la question est inferieur a 3, une fenetre dialog s'affiche avec un message pour recommencer, sinon une fenetre dialog s'affiche pour quitter le jeu.
	 */
	 public void startEnigme() {
		 
		 //panel de base
		 Panel pan = new Panel("blanc", 600,400);
		 pan.setLayout(new MigLayout());
		 
		 JTextArea intituleLabel = new JTextArea(this.intituleEnigme);
	    intituleLabel.setLineWrap(true);
	    intituleLabel.setWrapStyleWord(true);
	    intituleLabel.setPreferredSize(new Dimension(600,50));
	    pan.add(intituleLabel, "cell 0 0 3 1");
		 
		 //l'image
		 JLabel icon = new JLabel(new ImageIcon(this.image));
		 icon.setPreferredSize(new Dimension(200,200));
		 pan.add(icon, "cell 0 1 2 2");
	    
	    JTextArea question = new JTextArea(this.question);
	    question.setLineWrap(true);
	    question.setWrapStyleWord(true);
	    question.setPreferredSize(new Dimension(200,100));
	    pan.add(question, "cell 2 1");
	    

	    JTextField reponse = new JTextField();
	    reponse.setPreferredSize(new Dimension(200,100));
	    reponse.setBorder(BorderFactory.createTitledBorder("Votre réponse à l'enigme :"));
	    pan.add(reponse, "cell 2 2");
	    
	    JButton okBouton = new JButton("OK");
	    okBouton.setPreferredSize(new Dimension(20,20));
	    
	    okBouton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {  
	    	String currentReponse = reponse.getText();
	        checkEnigme(currentReponse);
	        if(check) {
	        	JOptionPane.showMessageDialog(null, messageGagnant, "Java De Papel", JOptionPane.INFORMATION_MESSAGE);
	        	setVisible(false);
	        	if(Jeu.getFenetre().getPanels().getZonePanel().getNbEtape() == Jeu.getJoueur().getCurrentEtape()) {
	        		@SuppressWarnings("unused")
					MsgIntro msg = new MsgIntro("javaFin", "C'etait génial");
	    		}
	        }
	        else{
	        	if(!check && nbEssai < 3)
		        {
		        	JOptionPane.showMessageDialog(null, "Olala... Essaye encore", "Java De Papel", JOptionPane.INFORMATION_MESSAGE);
		        }
		        else {
		        	MsgPerdu.createMessage(messagePerdant + " Voulez vous recommencer ?");
				setVisible(false);
		        }
	        }
	      }
	    });
	    pan.add(okBouton, "cell 0 3");
	    this.getContentPane().add(pan);
	    this.setVisible(true);
	  }  
	 
	 /**
	  * Compare la reponse saisie par l'utilisateur avec la reponse à l'enigme.
	  * Si les reponses sont egales, l'etape est finie et check vaut true, sinon check vaut false et on incremente nbEssai.
	  * @param rep la reponse saisie par le joueur
	  * @see Etape
	  */
	 public void checkEnigme(String rep) {
		 rep = rep.toLowerCase();
		 rep = rep.replace(" ", "");
		 this.reponse = this.reponse.toLowerCase();
		 this.reponse = this.reponse.replace(" ", "");
		 if(rep.contentEquals(this.reponse)) {
			 Jeu.getJoueur().setCurrentEtape();
			 Jeu.getFenetre().getPanels().getCommPanel().setEtapeLabel();
			this.check = true;
		 }else {
			 this.check = false;
			 this.nbEssai ++;
		 }
	 }
}
