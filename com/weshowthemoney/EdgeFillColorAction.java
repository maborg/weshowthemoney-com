/**
 * 
 */
package com.weshowthemoney;

import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class EdgeFillColorAction extends ColorAction {
	
	private static  double COLORMAPRANGE = 100000.0;

	public EdgeFillColorAction(String group, String field) {
		super(group, field);
	}
	static private int c1 = ColorLib.rgb(210, 200, 200);
	static private int c2 = ColorLib.rgb(255, 100,100);
	static private int plaincolor = ColorLib.rgb(100, 100,100);
	
	private ColorMap cmap = new ColorMap(
			ColorLib.getInterpolatedPalette(10, c1, c2),0.0,COLORMAPRANGE);

	public EdgeFillColorAction(String group) {
		super(group, VisualItem.FILLCOLOR);
	}
	 public int getColor(VisualItem item) {
		
		String perc= (String) ((EdgeItem)item).get("perc");
		if (perc==null)
			return plaincolor;
		return cmap.getColor(Double.parseDouble(perc));
		
	}
	@Override
	public int getDefaultColor() {
		// TODO Auto-generated method stub
		return super.getDefaultColor();
	}
	public static void setCOLORMAPRANGE(double colormaprange) {
		COLORMAPRANGE = colormaprange;
	}
	public static void setC1(int c1) {
		EdgeFillColorAction.c1 = c1;
	}
	public static void setC2(int c2) {
		EdgeFillColorAction.c2 = c2;
	}
	public static void setPlaincolor(int plaincolor) {
		EdgeFillColorAction.plaincolor = plaincolor;
	}

}