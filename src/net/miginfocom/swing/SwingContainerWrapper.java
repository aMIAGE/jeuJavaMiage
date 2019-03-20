package net.miginfocom.swing;


import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

import java.awt.*;

/**
 */
public final class SwingContainerWrapper extends SwingComponentWrapper implements ContainerWrapper
{
	/** Debug color for cell outline.
	 */
	private static final Color DB_CELL_OUTLINE = new Color(255, 0, 0);

	public SwingContainerWrapper(Container c)
	{
		super(c);
	}

	public ComponentWrapper[] getComponents()
	{
		Container c = (Container) getComponent();
		ComponentWrapper[] cws = new ComponentWrapper[c.getComponentCount()];
		for (int i = 0; i < cws.length; i++)
			cws[i] = new SwingComponentWrapper(c.getComponent(i));
		return cws;
	}

	public int getComponentCount()
	{
		return ((Container) getComponent()).getComponentCount();
	}

	public Object getLayout()
	{
		return ((Container) getComponent()).getLayout();
	}

	public final void paintDebugCell(int x, int y, int width, int height)
	{
		Component c = (Component) getComponent();
		if (c.isShowing() == false)
			return;

		Graphics2D g = (Graphics2D) c.getGraphics();
		if (g == null)
			return;

		g.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, new float[] {2f, 3f}, 0));
		g.setPaint(DB_CELL_OUTLINE);
		g.drawRect(x, y, width - 1, height - 1);
	}

	public int getComponetType(boolean disregardScrollPane)
	{
		return TYPE_CONTAINER;
	}
}
