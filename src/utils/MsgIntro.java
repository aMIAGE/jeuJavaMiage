package utils;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import panel.Panel;

/**
 * <b>MsgIntro est une classe heritant de JDialog</b>
 * MsgIntro va creer une boite de dialogue au lancement du jeu
 */
public class MsgIntro extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 *Constructeur MsgIntro
	 *<p>
	 *A la creation d'un objet MsgIntro, le constructeur de la classe mere est appelle (JDialog),
	 *la taille de la fenetre est definie a 1000 par 700px
	 *</p>
	 *@param image l'image correspondant a l'introduction
	 *@param bouton le bouton pour fermer la fenetre
	 */
	public MsgIntro(String image, String bouton) {
		 	super();
		    this.setSize(1000, 700);
		   
		    
		    Panel pan = new Panel(image, 1000,700);
			pan.setLayout(new MigLayout());
			
			JLabel jl = new JLabel("");
			jl.setPreferredSize(new Dimension(200, 600));
			
			JButton okBouton = new JButton(bouton);
		    okBouton.setPreferredSize(new Dimension(20,20));
		    
		    okBouton.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent arg0) {  
		    	setVisible(false);
		    	if(image.contentEquals("javaFin")) {
		    		System.exit(0);
		    	}
		      }
		    });
		    
		    pan.add(jl, "cell 0 0 4 4");
		    pan.add(okBouton, "cell 0 4");
		    this.getContentPane().add(pan);
		    
		    this.setLocationRelativeTo(null);
		    this.setResizable(false);
		    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		    this.setVisible(true);
		    
		    
		   
		  }
		 

}