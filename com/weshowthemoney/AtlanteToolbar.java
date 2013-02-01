package com.weshowthemoney;


import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JToolBar;


public class AtlanteToolbar extends JToolBar {
	private static final String NULLSTRING = ""; //$NON-NLS-1$

	private static final String SEPARATOR = Messages.getString("AtlanteToolbar.SEPARATOR"); //$NON-NLS-1$

	private static final long serialVersionUID = -4496750952529476130L;

	

	static final String buttontxts[]={
		Messages.getString("AtlanteToolbar.title"),"goWFTM",// TODO apre link a isbn o sito  //$NON-NLS-1$ //$NON-NLS-2$
		SEPARATOR,NULLSTRING,			
		//"Azionisti","menuStockHolder",			// TODO 2 Gestione finestre/menu:  Azionisti						
		Messages.getString("AtlanteToolbar.menuQuoted"),"menuQuoted", 	// TODO  2 Gestione finestre/menu:  Soc. quotate	 //$NON-NLS-1$ //$NON-NLS-2$
		SEPARATOR,NULLSTRING,
		Messages.getString("AtlanteToolbar.menuBoard"),"menuBoard",			// TODO 2 Gestione finestre/menu:  Manager //$NON-NLS-1$ //$NON-NLS-2$
		//"Istituzioni",NULLSTRING,		// TODO 2 Gestione finestre/menu:  Istituzioni
		//"x Settore",NULLSTRING,
		SEPARATOR,NULLSTRING,
		Messages.getString("AtlanteToolbar.clear"),"clear",				// TODO 3 completare gestione filtro/espansine delle voci e cancellazioen //$NON-NLS-1$ //$NON-NLS-2$
		//"Mappa Completa","showall",		 
		//"Trova Connessioni",NULLSTRING, // TODO connessioni
		SEPARATOR,NULLSTRING,
		Messages.getString("AtlanteToolbar.saveIMG"),"saveIMG",		 //$NON-NLS-1$ //$NON-NLS-2$
		//"Appunti",NULLSTRING,
		//"Salva pagina web",NULLSTRING,	
		//"Stampa",NULLSTRING,			// TODO print
		SEPARATOR,NULLSTRING
		//"Help",NULLSTRING,				// TODO help
		//"go to weshowthemoney.com",NULLSTRING			// TODO chi siamo
		
	};
 
	AtlanteToolbar(Object owner){
	   
		Insets i = new Insets(0,0,0,0);
		for (int j = 0; j < buttontxts.length; j+=2) {
			
			MyAction a=null;
			try {
				if (buttontxts[j].equals(SEPARATOR)){
					addSeparator();
				}
				else
				{	
				a = new MyAction(buttontxts[j],buttontxts[j+1],owner);
				a.putValue("Text",buttontxts[j]); //$NON-NLS-1$
				
				JButton b = new JButton(a);
				b.setMargin(i);
				add(b);
				}
			} catch (SecurityException e) {
				
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				
				e.printStackTrace();
			}
			
		}

 }
	public void actionPerformed(ActionEvent e) {
//		if (e.getSource()==diario ){
//			GraphView.diario();
//		}else
//		if (e.getSource()==listaQuotate ){
//		}else
//		if (e.getSource()==clear ){
//			GraphView.clear();	
//		}else	
//		if (e.getSource()==addQuotate ){
//			GraphView.lista(listaQuotate.getSelectedItem());	
//		}	
//		if (e.getSource()==addAzionisti ){
//			GraphView.lista(listaAzionisti.getSelectedItem());	
//		}	
	}

}
