/**
 * 
 */
package com.weshowthemoney;

import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.data.Tuple;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class FillColorAction extends ColorAction {

	public FillColorAction(String group) {
		super(group, VisualItem.FILLCOLOR);
	}

	public int getColor(VisualItem item) {
		if (item instanceof NodeItem) {
			NodeItem nitem = (NodeItem) item;
			boolean isDir = nitem.getString("type").compareTo("DIR") == 0; //$NON-NLS-1$ //$NON-NLS-2$
			String name = nitem.getString("name"); //$NON-NLS-1$
			Iterator it = m_vis.getGroup(Visualization.SEARCH_ITEMS)
					.tuples();

			for (; it.hasNext();) {
				Tuple element = (Tuple) it.next();
				if (element.getString("name").compareTo(name) == 0) //$NON-NLS-1$
					return ColorLib.rgb(255, 0, 0);
			}

			if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
				return ColorLib.rgb(255, 0, 0);
			else if (nitem.isHighlighted() && isDir)
				return ColorLib.rgb(235,242,222);
			else if (nitem.isHighlighted())
				return ColorLib.rgb(235,242,222);
			if (isDir)
				return ColorLib.rgb(242,242,242);// cmap.getColor(nitem.getInt("weight")
													// );
			else
				return ColorLib.rgb(235,242,222);// cmapdir.getColor(nitem.getInt("weight")
													// );
		} else {
			return ColorLib.rgb(207, 237, 163);
		}
	}

}