package elmtJeu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import main.Jeu;
import net.miginfocom.swing.MigLayout;
import panel.Panel;
import utils.MsgIntro;

/**
 * <b>Creuser est la classe representant le mini-jeu creuser.</b>
 * <p>
 * Un mini-jeu creuser est caracterise par les informations suivantes :
 * <ul>
 * <li>Une image.</li>
 * <li>Un texte.</li>
 * <li>Un compteur.</li>
 * </ul>
 * @see Etape
 */

public class Creuser extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
    /**
     * Chaine qui contient le chemin de l'image a afficher sur la fenetre.
     */
	private String image = "src/images/creuser.jpg";
	
    /**
     * Chaine qui contient le texte de la fenetre.
     */
	private String texte = "Appuie sur le bouton pour creuser !";
	
    /**
     * Entier correspondant au nombre de clics.
     */
	private int compteur = 0;
	
    /**
     * Constructeur Creuser, initialise les proprietes de la fenetre.
     * 
     */	
	public Creuser() {
		super(Jeu.getFenetre(), "Creuser le tunnel", true);
		this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
	}
	
    /**
     * Instancie un objet creuser et invoque la methode initComponent()
     * 
     * @see #initComponent()
     */
	public static void startCreuser() {
		Creuser creuser = new Creuser();
		creuser.initComponent();
	}
	
    /**
     * Instancie panel, textes, bouttons et zones de textes et initialise leurs proprietes. Gere l'événement de clic sur le bouton.
     * 
     * Incremente compteur de 1 a chaque clic, quand ce compteur atteint 10, le mini-jeu est termine.
     */
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