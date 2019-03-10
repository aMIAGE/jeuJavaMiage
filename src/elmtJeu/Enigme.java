package elmtJeu;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import panel.Panel;

public class Enigme extends JDialog {

	private static final long serialVersionUID = 1L;
	private String enigme;
	private String reponse;

	public Enigme(JFrame parent, String title, boolean modal, String nvEnigme, String nvReponse){
		    super(parent, title, modal);
		    this.enigme = nvEnigme;
		    this.reponse = nvReponse;
		    
		    this.setSize(600, 600);
		    this.setLocationRelativeTo(null);
		    this.setResizable(false);
		    this.initComponent();
		    this.setVisible(true);
		    
		   
		  }
	 
	 private void initComponent() {
		 //panel de base
		 Panel pan = new Panel("blanc", 600,600);
		 pan.setLayout(new MigLayout());
		 
		 //l'image
		 JLabel icon = new JLabel(new ImageIcon("src/images/enigme.jpg"));
		 icon.setPreferredSize(new Dimension(100,100));
		 pan.add(icon, "span 2 2");
		 

	    //L'enigme
	    JTextField enigme = new JTextField();
	    enigme.setPreferredSize(new Dimension(200,100));
	    enigme.setBorder(BorderFactory.createTitledBorder("Votre réponse à l'enigme :"));
	    
	    JLabel enigmeLabel = new JLabel(this.enigme);
	    enigmeLabel.setPreferredSize(new Dimension(200,100));
	    
	    pan.add(enigmeLabel, "wrap");
	    pan.add(enigme, "span");
	    


	
	    JButton okBouton = new JButton("OK");
	    okBouton.setPreferredSize(new Dimension(20,20));
	    
	    okBouton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {        
	        setVisible(false);
	        //ici on lancera la méthode de vérif de la réponse de l'enigme
	      }
	    });
	    pan.add(okBouton);


	    JButton cancelBouton = new JButton("Annuler");
	    cancelBouton.setPreferredSize(new Dimension(20,20));
	    cancelBouton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {
	        setVisible(false);
	      }      
	    });

	    
	    pan.add(cancelBouton);
	    /**
	    pan.add(new Panel("blanc", 20,20,null));
	    pan.add(control);
	    pan.add(new Panel("blanc", 20,20,null));
	    **/
	    this.getContentPane().add(pan);
	  }  
		 

}
