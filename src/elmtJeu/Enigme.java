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

public class Enigme extends JDialog {

	private static final long serialVersionUID = 1L;
	private String question;
	private String reponse;
	private String image;
	private String intituleEnigme;
	private String messagePerdant;
	private String messageGagnant;
	private boolean check;
	private int nbEssai;

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
		        }
	        }
	      }
	    });
	    pan.add(okBouton, "cell 0 3");
	    this.getContentPane().add(pan);
	    this.setVisible(true);
	  }  
	 
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
