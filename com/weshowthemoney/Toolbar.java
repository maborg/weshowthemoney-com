//package com.weshowthemoney;
//
//
//import java.awt.Dimension;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JComboBox;
//import javax.swing.JToolBar;
//
//
//@SuppressWarnings("serial")
//public class Toolbar extends JToolBar implements ActionListener{
//	private ToolBarButton diario;
//	private JComboBox listaQuotate;
//	private ToolBarButton clear;
//	private ToolBarButton addQuotate;
//	private ToolBarButton addAzionisti;
//	private JComboBox listaAzionisti;
//	
//	
//	
//	
//	public Toolbar(Class mainclass) {
//		
//		
//		Insets i = new Insets(0,0,0,0);
//		System.err.println("URL "+mainclass.getResource("/resources/testata.gif"));
//		diario = new ToolBarButton(mainclass.getResource("/resources/testata.gif"));
//		diario.setToolTipText("vai a Diario.it");
//		diario.setMargin(i);
//		add(diario);
//		
//		
//		diario.addActionListener(this);
//		
//		clear = new ToolBarButton(mainclass.getResource("/resources/stock_clear_24.GIF"));
//		clear.setToolTipText("cancella tutto");
//		clear.setMargin(i);
//		add(clear);
//		clear.addActionListener(this);
//		
////		
////		Object[] as = GraphView.addmenuQuotate();
////		
////		if (as!=null && as.length>0)
////		{
////			
////			listaQuotate = new JComboBox( as );
////			addQuotate = new ToolBarButton(mainclass.getResource("/resources/stock_add_24.GIF"));
////			addCmbAndButton(as,listaQuotate,addQuotate, "scegli un societa' quotata da aggiungere al grafico");
////			
////		}
////		
////		as = GraphView.addmenuAzionisti();
////		
////		if (as!=null && as.length>0)
////		{
////			listaAzionisti = new JComboBox( as );
////			addAzionisti = new ToolBarButton(mainclass.getResource("/resources/stock_add_24.GIF"));
////			addCmbAndButton(as,listaAzionisti,addAzionisti, "scegli un azionista da aggiungere al grafico");
////		}
////		else{
////			as = GraphView.addmenuAmministratori();
////			if (as!=null && as.length>0)
////			{
////				listaAzionisti = new JComboBox( as );
////				addAzionisti = new ToolBarButton(mainclass.getResource("/resources/stock_add_24.GIF"));
////				addCmbAndButton(as,listaAzionisti,addAzionisti, "scegli un amministratore da aggiungere al grafico");
////			}
////		}
////		
////		
////		
//		validate();
//	}
//	private void addCmbAndButton(Object[] as, JComboBox list, ToolBarButton button, String tooltip){
//		
//		list.setToolTipText(tooltip);
//		
//		list.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
//		list.setMinimumSize(new Dimension(5,5));
//		add(list);
//		
//		button.setToolTipText("aggiungi la selezione al grafico");
//		
//		add(button);
//		button.addActionListener(this);	
//		
//	}
//	public void actionPerformed(ActionEvent e) {
////		if (e.getSource()==diario ){
////		GraphView.diario();
////		}else
////		if (e.getSource()==listaQuotate ){
////		}else
////		if (e.getSource()==clear ){
////		GraphView.clear();	
////		}else	
////		if (e.getSource()==addQuotate ){
////		GraphView.lista(listaQuotate.getSelectedItem());	
////		}	
////		if (e.getSource()==addAzionisti ){
////		GraphView.lista(listaAzionisti.getSelectedItem());	
////		}	
//	}
//	
//}
