package com.weshowthemoney;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class AzionistiEdgeRenderer extends EdgeRenderer
{
	private static double PERCEDGE_DIV = 100000.0;
	private double m_angle;
	private float label_x;
	private float label_y;
	private boolean showValue = true; 
	

	@Override
	protected double getLineWidth(VisualItem item)
	{
		double p = 0.0;
		String perc = (String) item.getSourceTuple().get("perc");
		
		if (perc!=null){
			try{
				p = Double.parseDouble(perc.split(" ")[0].replace("-",""));
			}catch(Exception e){
				return super.getLineWidth(item);
			}
		}	
		Double w = p / PERCEDGE_DIV;
		if (w<0)
			return  2;
		else
			return w + 2;
	}
	
	@Override
	protected Shape getRawShape(VisualItem item) {
		Shape s = super.getRawShape(item);
		if (s!=null)
		{
			String edgelabel= (String) ((EdgeItem)item).get("perc");
			
			
			if (showValue && edgelabel!=null){	
				setEdgeType(Constants.EDGE_TYPE_LINE);
				label_x = (float)((m_line.getX1()-m_line.getX2()));
				label_y = (float)((m_line.getY1()-m_line.getY2()));
				
				double d = Math.sqrt(label_x*label_x+label_y*label_y);
				double nx = label_x /d;
				
				m_angle = Math.acos(nx);
				
				if (label_y<0.0 && m_angle>0)
					m_angle = m_angle * -1.0; 
				
				if (label_x<0.0 )
					m_angle = m_angle + Math.PI; 				
				
				label_x = (float)((m_line.getX1()+m_line.getX2()));
				label_y = (float)((m_line.getY1()+m_line.getY2()));
				
				label_x = (float) (label_x /2.0);
				label_y = (float) (label_y /2.0);
			}else{
				setEdgeType(Constants.EDGE_TYPE_CURVE);
			}
		}
		return s;
	}
	
	@Override
	public void render(Graphics2D g, VisualItem item) {

		String edgelabel= (String) ((EdgeItem)item).get("perc");
		if (showValue==false || edgelabel==null){	
			setEdgeType(Constants.EDGE_TYPE_CURVE);
			setRenderType(RENDER_TYPE_DRAW) ;	
		}else{
			setEdgeType(Constants.EDGE_TYPE_LINE);
			setRenderType(RENDER_TYPE_DRAW_AND_FILL) ;
		}
		super.render(g, item);
		
		if (showValue && edgelabel!=null && item.isHighlighted())
		{
			AffineTransform a = g.getTransform();
			
			g.rotate(m_angle,label_x,label_y);
			
			int c = item.getTextColor();
			g.setColor(new Color(c,true));	
			g.drawString((String) edgelabel,label_x,label_y);
			g.setTransform(a);
			
			
		}
	}
	@Override
	public int getRenderType(VisualItem item) {
		String edgelabel= (String) ((EdgeItem)item).get("perc");
		if (showValue==false || edgelabel==null){	

			return RENDER_TYPE_DRAW ;	
		}else{
			return RENDER_TYPE_DRAW_AND_FILL ;

		}

	}

	public static void setPERCEDGE_DIV(double percedge_div) {
		PERCEDGE_DIV = percedge_div;
	}
}
