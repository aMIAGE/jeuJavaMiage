package elmtJeu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import main.Jeu;
import net.miginfocom.swing.MigLayout;
import panel.Panel;
import utils.MsgIntro;

public class Creuser extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private String image = "src/images/creuser.jpg";
	private String texte = "Appuie sur le bouton pour creuser !";
	private int compteur = 0;
	
	public Creuser() {
		super(Jeu.getFenetre(), "Creuser le tunnel", true);
		this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
	}
	
	public static void startCreuser() {
		Creuser creuser = new Creuser();
		creuser.initComponent();
	}
	
	private void initComponent() {
		
		//panel de base
		Panel pan = new Panel("blanc", 600,400);
		pan.setLayout(new MigLayout());
		 
		JTextArea intituleLabel = new JTextArea(this.texte);
	    intituleLabel.setLineWrap(true);
	    intituleLabel.setWrapStyleWord(true);
	    intituleLabel.setPreferredSize(new Dimension(600,50));
	    pan.add(intituleLabel, "cell 0 0 3 1");
		 
		//l'image
		JLabel icon = new JLabel(new ImageIcon(this.image));
		icon.setPreferredSize(new Dimension(200,200));
		pan.add(icon, "cell 0 1 2 2");
		
		JTextArea etat = new JTextArea();
	    etat.setLineWrap(true);
	    etat.setWrapStyleWord(true);
	    etat.setPreferredSize(new Dimension(200,100));
	    pan.add(etat, "cell 2 1");
	
	    JButton creuserBouton = new JButton("Creuse !");
	    creuserBouton.setPreferredSize(new Dimension(20,20));
	    
	    creuserBouton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {  
	    	compteur++;
	    	etat.setText("Vous avez creusé " + Integer.toString(compteur) + " fois sur 10 !");
	    	if(compteur >= 10) {
	    		JOptionPane.showMessageDialog(null, "Tu as fini de creuser. Assez travaillé, reviens plus tard !", "Java De Papel", JOptionPane.INFORMATION_MESSAGE);
	    		Jeu.getJoueur().setCurrentEtape();
				 Jeu.getFenetre().getPanels().getCommPanel().setEtapeLabel();
				 setVisible(false);
				 if(Jeu.getFenetre().getPanels().getZonePanel().getNbEtape() == Jeu.getJoueur().getCurrentEtape()) {
		        		@SuppressWarnings("unused")
						MsgIntro msg = new MsgIntro("javaFin", "C'etait génial");
		    		}
	    		
	    	}
	      }
	    });
	    pan.add(creuserBouton, "cell 0 3");
	    this.getContentPane().add(pan);	
	    this.setVisible(true);
	  }

}