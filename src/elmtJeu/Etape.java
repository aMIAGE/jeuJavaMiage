package elmtJeu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Timer;
import main.Jeu;

public class Etape {
	private String zone;
	private int numEtape;
	private Enigme enigme;
	private Boolean creuser;
	private Timer timer;
	
	public Etape( Vector<String> etape, Enigme enigme, Boolean creuser) {
		this.zone = etape.get(0);
		this.numEtape = Integer.parseInt(etape.get(1));
		this.enigme = enigme;
		this.creuser = creuser;
		
	}
	
	
	public String getNomZone() {
		return this.zone;
	}
	
	public int getNumEtape() {
		return this.numEtape;
	}
	
	
	public void launchEnigme() {
		Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Tiens toi prêt à résoudre une nouvelle enigme", "java");
		Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
		Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
		timer = new Timer(1*1000, new ActionListener() {
			  public void actionPerformed(ActionEvent a) {
				  if(creuser) {
					  Creuser.startCreuser();
				  }
				  else {
					  enigme.startEnigme();
				  }
				  Jeu.getFenetre().getPanels().getJeuPanel().setTime();
			      timer.stop();
			  }
		  });
		timer.start();
	}
	
}
