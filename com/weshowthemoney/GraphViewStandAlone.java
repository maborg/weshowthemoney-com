package com.weshowthemoney;

import java.applet.Applet;
import java.awt.Event;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


	public class GraphViewStandAlone extends GraphView {
		
		
	static GraphViewStandAlone applet;
	String filetoload = "resources\\candidate_donors";//"/resources/archivio_totale2006.xml";
	private static final long serialVersionUID = 1L;
	
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
		public static void createAndShowGUI() {
	        applet = new GraphViewStandAlone();
	      
	        Frame frame = new GraphViewStandAloneFrame(Messages.getString("GraphViewStandAlone.siteAddress"), applet, 500, 400); //$NON-NLS-1$
	        frame.setVisible(true);
	        frame.addWindowListener(new WindowAdapter() {
        	    public void windowClosing(WindowEvent e) {
        	        System.exit(0);
        	    }
        	});
	    }
	    @Override
	    public String getParameter(String name) {
	    	
	    	
			return filetoload ;
	    }
		public void setFiletoload(String filetoload) {
			this.filetoload = filetoload;
		}
	}

	class GraphViewStandAloneFrame extends JFrame  {

		private static final long serialVersionUID = 1L;

		public GraphViewStandAloneFrame(String title, Applet applet, int width, int height) { 
	        // create the Frame with the specified title.
	        super(title); 
	        
//	        // Add a menubar, with a File menu, with a Quit button.
//	        MenuBar menubar = new MenuBar();
//	       
//	        Menu file = new Menu("File", true);
//	        menubar.add(file);
//	        file.add("Carica Azionisti");
//	        file.add("Carica Amministratori");
//	        file.add("Quit");
//	        this.setMenuBar(menubar);
	        
	        // Add the applet to the window.  Set the window size.  Pop it up.
	        this.add("Center", applet); //$NON-NLS-1$
	        this.setSize(width, height);

	        // Start the applet.
	        applet.init();
	        applet.start();
	    }
		public boolean action(Event e, Object arg)
	    {
	        if (e.target instanceof MenuItem) {
	            String label = (String) arg;
	           
	            if (label.equals("Carica Azionisti")){  //$NON-NLS-1$
//	            	GraphViewStandAlone.applet.stop();
	            	restart("resources/archivio_azionariato2006.xml"); //$NON-NLS-1$
//	            	GraphViewStandAlone.applet.start();
	            };
	            if (label.equals("Carica Amministratori")){  //$NON-NLS-1$
	            	restart("resources/socialnet.xml"); //$NON-NLS-1$
	            };
	            if (label.equals("Quit")) System.exit(0); //$NON-NLS-1$
	        }
	        return false;
	    }


	    @SuppressWarnings("static-access") //$NON-NLS-1$
		private void restart(String file) {
			GraphViewStandAlone.applet.graphview.removeAll();
			GraphViewStandAlone.applet.toolbar.removeAll();
			GraphViewStandAlone.applet.remove(GraphViewStandAlone.applet.toolbar);
			GraphViewStandAlone.applet.remove(GraphViewStandAlone.applet.graphview);
			GraphViewStandAlone.applet.setFiletoload( file );
			GraphViewStandAlone.applet.init();
			GraphViewStandAlone.applet.validate();
		
			
			
		}


	
	}
