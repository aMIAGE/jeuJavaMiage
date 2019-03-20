package panel;

/**
 * 
 * PanelListe est une classe regroupant tous les Panels du jeu
 * <p>
 * PanelListe est caracterise par les identifiants suivants:
 * <ul>
 * <li>Un PlanPanel</li>
 * <li>Un ZonePanel</li>
 * <li>Un CommandePanel</li>
 * <li>Un JeuPanel</li>
 * </ul>
 * </p>
 * @see PlanPanel
 * @see ZonePanel
 * @see CommandePanel
 * @see JeuPanel
 */
public class PanelListe{
	private PlanPanel planPanel;
	private ZonePanel zonePanel;
	private CommandePanel commPanel;
	private JeuPanel jeuPanel;
	
	/**
	 * Constructeur PanelListe
	 * <p>
	 * A la construction d'un objet PanelListe,
	 * un PlanPanel, un ZonePanel, un CommandePanel et un JeuPanel lui sont affectes
	 * </p>
	 * @param pp
	 * 			un objet PlanPanel
	 * @param zp
	 * 			un objet ZonePanel
	 * @param cp
	 * 			un objet CommandePanel
	 * @param jp
	 * 			un objet JeuPanel
	 */
	public PanelListe(PlanPanel pp, ZonePanel zp, CommandePanel cp, JeuPanel jp) {
		this.planPanel =pp;
		this.zonePanel =zp;
		this.commPanel =cp;
		this.jeuPanel =jp;
	}
	
	/**
	 * Recupere son PlanPanel 
	 * @return son PlanPanel
	 */
	public PlanPanel getPlanPanel() {
		return this.planPanel;
	}
	
	/**
	 * Recupere son ZonePanel 
	 * @return son ZonePanel
	 */
	public ZonePanel getZonePanel() {
		return this.zonePanel;
	}
	
	/**
	 * Recupere son CommandePanel 
	 * @return son CommandePanel
	 */
	public CommandePanel getCommPanel() {
		return this.commPanel;
	}
	
	/**
	 * Recupere son JeuPanel 
	 * @return son JeuPanel
	 */
	public JeuPanel getJeuPanel() {
		return this.jeuPanel;
	}

}
