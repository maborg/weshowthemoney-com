package com.weshowthemoney;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.util.ui.BrowserLauncher;
import prefuse.visual.VisualItem;

/**
 * Control that enables a tooltip display for items based on mouse hover.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PopupControl extends ControlAdapter  implements ActionListener{

private static final String CLEAR = Messages.getString("PopupControl.Clear"); //$NON-NLS-1$


private static final String GOOGLE = "Google"; //$NON-NLS-1$


String label = null;


private VisualItem item;

private JPopupMenu popup;
private GraphView graphview;
    public PopupControl(String label,GraphView graphview) {
        this.label = label;
        this.graphview=graphview;
    }
    
    /**
     * 
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
    	JPopupMenu.setDefaultLightWeightPopupEnabled(true);
    	this.item = item;
        Display d = (Display)e.getSource();
        
        if (popup != null ){
        	d.setComponentPopupMenu(popup);
        	popup.setVisible(false);
        	
        }
        else
        	popup = new JPopupMenu();
        
        
        
        popup.removeAll();
        JMenuItem menuItem1 = new JMenuItem(GOOGLE + " \"" + item.getString(label) +"\"" );       //$NON-NLS-1$ //$NON-NLS-2$
        popup.add(menuItem1);
        menuItem1.addActionListener(this);
        //--
        if (((String)item.get("type")).contains(Messages.getString("PopupControl.5"))) //$NON-NLS-1$ //$NON-NLS-2$
        {
        	menuItem1 = new JMenuItem(Messages.getString("PopupControl.AZ") );       //$NON-NLS-1$
        	popup.add(menuItem1);
        	menuItem1.addActionListener(this);

//        	menuItem1 = new JMenuItem("Azionisti" );      
//	        popup.add(menuItem1);
//	        menuItem1.addActionListener(this);
        }
        //--
        if (((String)item.get("type")).contains("AZ") //$NON-NLS-1$ //$NON-NLS-2$
        		||((String)item.get("type")).contains("DIR") //$NON-NLS-1$ //$NON-NLS-2$
        )
        {
	        menuItem1 = new JMenuItem(Messages.getString("PopupControl.SOC") );       //$NON-NLS-1$
	        popup.add(menuItem1);
	        menuItem1.addActionListener(this);
        }
        //--
        Icon i = new ImageIcon("resources/stock_clear_24.GIF"); //$NON-NLS-1$
        menuItem1 = new JMenuItem(CLEAR,i);
        popup.add(menuItem1);
        menuItem1.addActionListener(this);
        Point p = e.getPoint();
        d.setComponentPopupMenu(null);
        
        popup.show(d,p.x+20,p.y+20);
        
        d.setComponentPopupMenu(popup);
    }
    
    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited(VisualItem item, MouseEvent e) {
    	//popup.setVisible(false);
    	
//        Display d = (Display)e.getSource();
//        d.setComponentPopupMenu(null);
    }

    public void actionPerformed(ActionEvent e) {
    	String cmd= e.getActionCommand();
    	if (cmd.startsWith(GOOGLE))
    	{
    		BrowserLauncher.showDocument("http://www.google.it/search?hl=it&q=" + cmd.substring(GOOGLE.length()).replace(",","+").replace(" ","+") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    	}
    	
    	else if (cmd.startsWith(Messages.getString("PopupControl.AZ"))) //$NON-NLS-1$
    	{
    		graphview.expand(item,"DIR"); //$NON-NLS-1$
    	}
    	
    	else if (cmd.startsWith(Messages.getString("PopupControl.SOC"))) //$NON-NLS-1$
    	{
    		graphview.expand(item,"SOC"); //$NON-NLS-1$
    	}
    	 

    	
    	else if (cmd.startsWith(CLEAR))
    	{
    		graphview.clearitem(item);
    	}
    	    	
    }
    @Override
    public void itemClicked(VisualItem item, MouseEvent e) {
    	graphview.expand(item,"DIR"); //$NON-NLS-1$

		graphview.expand(item,"SOC"); //$NON-NLS-1$
    }
   
}