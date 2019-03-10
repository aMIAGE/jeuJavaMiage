package panel;

public class PanelListe{
	private PlanPanel planPanel;
	private ZonePanel zonePanel;
	private CommandePanel commPanel;
	private JeuPanel jeuPanel;
	
	public PanelListe(PlanPanel pp, ZonePanel zp, CommandePanel cp, JeuPanel jp) {
		this.planPanel =pp;
		this.zonePanel =zp;
		this.commPanel =cp;
		this.jeuPanel =jp;
	}
	
	public PlanPanel getPlanPanel() {
		return this.planPanel;
	}
	
	public ZonePanel getZonePanel() {
		return this.zonePanel;
	}
	
	public CommandePanel getCommPanel() {
		return this.commPanel;
	}
	
	public JeuPanel getJeuPanel() {
		return this.jeuPanel;
	}

}
