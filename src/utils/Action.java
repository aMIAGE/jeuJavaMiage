package utils;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import elmtJeu.Zone;
import exception.HorsZoneException;
import main.Jeu;
import panel.JeuPanel;
import panel.ZonePanel;

/**
 * <b>Action est une classe heritant de ActionListener</b>
 * Action va jouer le role de gestionnaire d'evenements
 */
public class Action implements ActionListener{
	private String act;
	Timer timer;
	
	/**
	 * Constructeur Action
	 * @param action l'action demandee par le joueur
	 */
	public Action(String action) {
		this.act = action;
	}
	

	/**
	 * <h1>Verifier que le joueur reste dans la fenetre du jeu</h1>
	 *Cette methode indique si le joueur a depasse la fenetre ou non.
	 *<br/>
	 *Elle prend les positions du joueur sur les 2 axes et les compare avec la taille du panel du plan.
	 *Si le joueur rencontre un des bords du panel, la methode retourne "vrai" : il y a eu collision. 
	 */
	public boolean checkCollision() {
		if (Jeu.getJoueur().getPosX()< 4)
			return true;
		if (Jeu.getJoueur().getPosX()> 490)
			return true;
		if (Jeu.getJoueur().getPosY() > 346 -20)
			return true;
		if (Jeu.getJoueur().getPosY()< 4)
			return true;
		if((Jeu.getJoueur().getPosX() < 191||Jeu.getJoueur().getPosX() >300) && Jeu.getJoueur().getPosY() > 250)
			return true;
		else 
			return false;
	}
	
	
	/**
	 * <h1>Verifier que le joueur emprunte correctement les portes</h1>
	 *Cette methode indique si le joueur rencontre un mur sur le plan.
	 *</n>
	 *Si le joueur desire changer de zone :
	 *<li>On recupere l'orientation de la porte de la zone ou le joueur souhaite entrer</li>
	 *<li>On recupere les positions x et y du joueur</li>
	 *<li>On s'assure que la position du joueur (celle sur l'axe correspondant a l'orientation de la porte), est compris entre le minimum et le maximum des dimensions de la porte</li>
	 *<br/>
	 *Si le joueur passe correctement par la porte, le deplacement est autorise et vaut "vrai".
	 */
	public boolean checkPorte(String sensMvt) {
		boolean result = false;
		ZonePanel zonePanel = Jeu.getFenetre().getPanels().getZonePanel();
		Zone currentZone = zonePanel.getCurrentZone();
		
		if(Jeu.getJoueur().isChangementZone()) {
			if(!currentZone.getOrientPorte().contentEquals(sensMvt)) {
				Boolean cdt1 =Jeu.getJoueur().getPosX()>currentZone.getDimPorte().get(0) && Jeu.getJoueur().getPosX()<currentZone.getDimPorte().get(1);
				Boolean cdt2 = Jeu.getJoueur().getPosY()>currentZone.getDimPorte().get(0) && Jeu.getJoueur().getPosY()<currentZone.getDimPorte().get(1);
				if (sensMvt.contentEquals("y") && cdt1 || sensMvt.contentEquals("x")&&cdt2) {
					Boolean cdt3 = zonePanel.getCurrentZone().getNom().contentEquals("la salle d'attente") && Jeu.getJoueur().getCurrentZone().contentEquals("la salle des coffres");
					Boolean cdt4 = zonePanel.getCurrentZone().getNom().contentEquals("la salle d'impression") && Jeu.getJoueur().getCurrentZone().contentEquals("la maison du gardien");
					Boolean cdt5 = (Jeu.getJoueur().getPosX() < 222 || Jeu.getJoueur().getPosX() > 270)  && zonePanel.getCurrentZone().getNom().contentEquals("au guichet") && Jeu.getJoueur().getCurrentZone().contentEquals("les bureaux de l'administration");
					if(!cdt3 && !cdt4 && !cdt5)
					result =  true;
				}
			}
			if(currentZone.getOrientPorte2() != null && !currentZone.getOrientPorte2().contentEquals(sensMvt)) {
				Boolean cdt1 =Jeu.getJoueur().getPosX()>currentZone.getDimPorte2().get(0) && Jeu.getJoueur().getPosX()<currentZone.getDimPorte2().get(1);
				Boolean cdt2 = Jeu.getJoueur().getPosY()>currentZone.getDimPorte2().get(0) && Jeu.getJoueur().getPosY()<currentZone.getDimPorte2().get(1);
				if (sensMvt.contentEquals("y") && cdt1 || sensMvt.contentEquals("x")&&cdt2)
					result =  true;
			}
		}
		else result = true;
		
		return result;
	}
			
				
	/**
	 * <h1>Faire Avancer le joueur (vers le haut)</h1>
	 *Cette methode permet de modifier la position du joueur vers le haut du plan.
	 *<br/>
	 *On fait avancer la position du joueur, on vérifie ensuite si cette nouvelle position ne creee pas de collision avec un mur
	 *et si le joueur passe correctement par la porte de la zone : si ce n'est pas le cas, la position est reinitialiser a son etat precedent.
	 *<br/>
	 *Ensuite, si ces conditions sont respectees et que le joueur souhaite changer de zone, 
	 *sa zone courante est modifiee et son etape est mise a jour.
	 *<br/>
	 *Si l'etape est "vide", Java indique qu'il n'y a rien a faire dans la zone et le point est redessine a la nouvelle position.
	 *Si il ne s'agit pas de la bonne etape, le joueur a perdu
	 *<br/>
	 *A la suite de ca, le point de position du joueur est redessine a la nouvelle position.
	 *
	 * 
	 */
	public void avancer() throws HorsZoneException {
		Jeu.getJoueur().avancer();
		if(this.checkCollision() || !(this.checkPorte("y")) ){
			Jeu.getJoueur().reculer();
		}
		if(Jeu.getJoueur().isChangementZone()) {
				Zone zone = Jeu.getFenetre().getPanels().getZonePanel().setZone();
				String checkEtape = zone.isEtape();
				if(checkEtape.contentEquals("non")) {
					Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Il n'y a rien a faire ici, change de piéce ou appelle moi pour de l'aide", "java");
					Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
					Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
				}
				if(checkEtape.contentEquals("perdu")) {
					MsgPerdu.createMessage("Oh non, vous avez perdu...Voulez vous recommencer ?");
				}
			
		}
		Jeu.getFenetre().getPanels().getPlanPanel().repaint();
		
	}


	/**
	 * <h1>Faire aller le joueur à Gauche</h1>
	 *Cette methode permet de modifier la position du joueur vers la gauche du plan.
	 *<br/>
	 *On fait avancer la position du joueur, on vérifie ensuite si cette nouvelle position ne creee pas de collision avec un mur
	 *et si le joueur passe correctement par la porte de la zone : si ce n'est pas le cas, la position est reinitialiser a son etat precedent.
	 *<br/>
	 *Ensuite, si ces conditions sont respectees et que le joueur souhaite changer de zone, 
	 *sa zone courante est modifiee et son etape est mise a jour.
	 *<br/>
	 *Si l'etape est "vide", Java indique qu'il n'y a rien a faire dans la zone et le point est redessine a la nouvelle position.
	 *Si il ne s'agit pas de la bonne etape, le joueur a perdu
	 *<br/>
	 *A la suite de ca, le point de position du joueur est redessine a la nouvelle position.
	 *
	 * 
	 */
	public void allerAGauche() throws HorsZoneException {
		Jeu.getJoueur().allerAGauche();
		if(this.checkCollision() || !(this.checkPorte("x") ) ) {
			Jeu.getJoueur().allerADroite();
		}
		if(Jeu.getJoueur().isChangementZone()) {
				Zone zone = Jeu.getFenetre().getPanels().getZonePanel().setZone();
				String checkEtape = zone.isEtape();
				if(checkEtape.contentEquals("non")) {
					Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Il n'y a rien a faire ici, change de piéce ou appelle moi pour de l'aide", "java");
					Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
					Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
				}
				if(checkEtape.contentEquals("perdu")) {
					MsgPerdu.createMessage("Oh non, vous avez perdu...Voulez vous recommencer ?");
				}
			
		}
		Jeu.getFenetre().getPanels().getPlanPanel().repaint();
		
	}
	


	/**
	 * <h1>Faire Reculer le joueur (vers le bas)</h1>
	 *Cette methode permet de modifier la position du joueur vers le bas du plan.
	 *<br/>
	 *On fait avancer la position du joueur, on vérifie ensuite si cette nouvelle position ne creee pas de collision avec un mur
	 *et si le joueur passe correctement par la porte de la zone : si ce n'est pas le cas, la position est reinitialiser a son etat precedent.
	 *<br/>
	 *Ensuite, si ces conditions sont respectees et que le joueur souhaite changer de zone, 
	 *sa zone courante est modifiee et son etape est mise a jour.
	 *<br/>
	 *Si l'etape est "vide", Java indique qu'il n'y a rien a faire dans la zone et le point est redessine a la nouvelle position.
	 *Si il ne s'agit pas de la bonne etape, le joueur a perdu
	 *<br/>
	 *A la suite de ca, le point de position du joueur est redessine a la nouvelle position.
	 *
	 * 
	 */
	public void reculer() throws HorsZoneException {
		Jeu.getJoueur().reculer();
		if(this.checkCollision() || !(this.checkPorte("y") )) {
			Jeu.getJoueur().avancer();
		}
		if(Jeu.getJoueur().isChangementZone()) {
				Zone zone = Jeu.getFenetre().getPanels().getZonePanel().setZone();
				String checkEtape = zone.isEtape();
				if(checkEtape.contentEquals("non")) {
					Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Il n'y a rien a faire ici, change de piéce ou appelle moi pour de l'aide", "java");
					Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
					Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
				}
				if(checkEtape.contentEquals("perdu")) {
					MsgPerdu.createMessage("Oh non, vous avez perdu...Voulez vous recommencer ?");
				}
			
		}
		Jeu.getFenetre().getPanels().getPlanPanel().repaint();
		
	}
	


	/**
	 * <h1>Faire aller le joueur vers la Droite</h1>
	 *Cette methode permet de modifier la position du joueur vers la droite du plan.
	 *<br/>
	 *On fait avancer la position du joueur, on vérifie ensuite si cette nouvelle position ne creee pas de collision avec un mur
	 *et si le joueur passe correctement par la porte de la zone : si ce n'est pas le cas, la position est reinitialiser a son etat precedent.
	 *<br/>
	 *Ensuite, si ces conditions sont respectees et que le joueur souhaite changer de zone, 
	 *sa zone courante est modifiee et son etape est mise a jour.
	 *<br/>
	 *Si l'etape est "vide", Java indique qu'il n'y a rien a faire dans la zone et le point est redessine a la nouvelle position.
	 *Si il ne s'agit pas de la bonne etape, le joueur a perdu
	 *<br/>
	 *A la suite de ca, le point de position du joueur est redessine a la nouvelle position.
	 *
	 * 
	 */
	public void allerADroite() throws HorsZoneException {
		Jeu.getJoueur().allerADroite();
		if(this.checkCollision() || !(this.checkPorte("x"))) {
			Jeu.getJoueur().allerAGauche();
		}
		if(Jeu.getJoueur().isChangementZone()) {
				Zone zone = Jeu.getFenetre().getPanels().getZonePanel().setZone();
				String checkEtape = zone.isEtape();
				if(checkEtape.contentEquals("non")) {
					Jeu.getFenetre().getPanels().getJeuPanel().setTexte("Il n'y a rien a faire ici, change de piéce ou appelle moi pour de l'aide", "java");
					Jeu.getFenetre().getPanels().getJeuPanel().setImage("java");
					Jeu.getFenetre().getPanels().getJeuPanel().repaint();  
				}
				if(checkEtape.contentEquals("perdu")) {
					MsgPerdu.createMessage("Oh non, vous avez perdu...Voulez vous recommencer ?");
				}
			
		}
		Jeu.getFenetre().getPanels().getPlanPanel().repaint();
		
	}
	
	/**
	 * <h1>Demander de l'aide a Java</h1>
	 *Cette methode permet au joueur d'appeler Java pour recevoir des instructions.
	 *<br/>
	 *On verifie que le joureur se trouve bien dans une piece. Si c'est le cas, on lui fournit les instructions conrrespondantes à sa zone courante.
	 *Sinon, Java lui indiquera d'entrer dans une pièce pour pouvoir l'aider.
	 */
	public void aideJava() {
		ZonePanel zonePanel = Jeu.getFenetre().getPanels().getZonePanel();
		JeuPanel jeuPanel = Jeu.getFenetre().getPanels().getJeuPanel();
		Zone currentZone = zonePanel.getCurrentZone();
		
		if(currentZone != null) {
			jeuPanel.setTexte(currentZone.getInstructions(), "java");
			jeuPanel.setImage("java");
		}
		else {
			jeuPanel.setTexte("Entre dans une piece, je t'aiderais ensuite", "java");
			jeuPanel.setImage("java");
		}
		jeuPanel.repaint();
	}
	
	
	/**
	 * <h1>Gestionnaire d'actions</h1>
	 *Cette methode permet de gerer les actions demandees par le joueur a l'aide des boutons.
	 *<br/>
	 */
	public void actionPerformed(ActionEvent e) {
		if(this.act.contentEquals("av")) {
		  try {
			this.avancer();
		} catch (HorsZoneException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		
		if(this.act.contentEquals("gauche")) {
			try {
				this.allerAGauche();
			} catch (HorsZoneException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		if(this.act.contentEquals("droite")) {
			try {
				this.allerADroite();
			} catch (HorsZoneException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		if(this.act.contentEquals("rec")) {
			try {
				this.reculer();
			} catch (HorsZoneException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		if(this.act.contentEquals("chrono")) {
			JeuPanel jeuPanel = Jeu.getFenetre().getPanels().getJeuPanel();
			jeuPanel.setTexte(Jeu.getJoueur().getChrono().getStringTime(), "chrono");
			jeuPanel.setImage("chrono");
			jeuPanel.repaint();
		}
		
		if(this.act.contentEquals("scenar")) {
				JeuPanel jeuPanel = Jeu.getFenetre().getPanels().getJeuPanel();
				int nbScenar = Jeu.getFenetre().getPanels().getCommPanel().getNbScenar();
				if(nbScenar < 5) {
					Jeu.getFenetre().getPanels().getCommPanel().setNbScenar();
					jeuPanel.setTexte("", "scenar");
					jeuPanel.repaint();	
					Jeu.getFenetre().getPanels().getCommPanel().setScenarLabel();
				}
				else {
					jeuPanel.setTexte("Je ne peux plus parler, nos appels vont être interceptés", "java");
					jeuPanel.setImage("java");
					jeuPanel.repaint();	
				}
			
		}
		
		
		if(this.act.contentEquals("java")) {
			this.aideJava();
		}
		
		
		
			
	}
}