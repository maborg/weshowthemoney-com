package com.weshowthemoney;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.SpringForce;
import prefuse.util.ui.BrowserLauncher;
import prefuse.util.ui.JPrefuseApplet;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class GraphView extends JPrefuseApplet {

	private static final long serialVersionUID = 1L;

	private static final String graph = "graph"; //$NON-NLS-1$

	private static final String nodes = "graph.nodes"; //$NON-NLS-1$

	private static final String edges = "graph.edges"; //$NON-NLS-1$

	static LocalGraphFilter filter;

	private static Graph mygraph;

	static Visualization vis;

	private static TupleSet focusGroup;

	private static VisualGraph vg;

	static JToolBar toolbar;

	public JComponent graphview;

	private JSplitPane jsplitpane;

	private JList jlQuoted;

	private JList jlStockHolder;

	private JScrollPane scrollpaneQ;

	private JScrollPane scrollpaneA;

	private JList jlBoards;

	private JScrollPane scrollpaneB;
	private Config config = new Config();

	public void init() {
		
		UILib.setPlatformLookAndFeel();

		// String file = this.getParameter("graphfile");
		// URL url = this.getClass
		// ().getResource("/resources/candidate_donors");
		InputStream is = getClass().getClassLoader().getResourceAsStream(			
				config.getGraphFilename());//$NON-NLS-1$

		
		graphview = null;
		try {

			mygraph = new GraphMLReader().readGraph(is);
			graphview = startQuotate(mygraph, "name"); //$NON-NLS-1$
		} catch (DataIOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		this.getContentPane().add(graphview);
		toolbar.setMaximumSize(this.getContentPane().getMaximumSize());
		this.getContentPane().validate();
	}

	public void stopQuotate() {
		mygraph.dispose();
	}

	public JComponent startQuotate(Graph g, String label) {
		vis = new Visualization();
		vg = vis.addGraph(graph, g);
		vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);

		focusGroup = vis.getGroup(Visualization.FOCUS_ITEMS);
		focusGroup.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
				for (int i = 0; i < rem.length; ++i)
					((VisualItem) rem[i]).setFixed(false);
				for (int i = 0; i < add.length; ++i) {
					((VisualItem) add[i]).setFixed(false);
					((VisualItem) add[i]).setFixed(true);
				}
				vis.run("draw"); //$NON-NLS-1$
			}
		});

		// set up the renderers
		LabelRenderer tr = new LabelRenderer(label, "icon"); //$NON-NLS-1$
		tr.setRoundedCorner(7, 8);
		tr.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);

		// choose edgerenderer to use
		AzionistiEdgeRenderer er;
		
		AzionistiEdgeRenderer.setPERCEDGE_DIV(config.getPERCEDGE_DIV());
		
		er = new AzionistiEdgeRenderer();
		er.setArrowType(Constants.EDGE_ARROW_FORWARD);
		er.setArrowHeadSize(10, 12);

		// color used for percent
		vis.setValue(edges, null, VisualItem.TEXTCOLOR, ColorLib
				.color(new Color(20, 20, 20)));
//		vis.setValue(edges, null, VisualItem.FILLCOLOR, ColorLib
//				.color(new Color(120, 120, 120)));
//		vis.setValue(edges, null, VisualItem.STROKECOLOR, ColorLib
//				.color(new Color(120, 120, 0)));

		// render factory
		vis.setRendererFactory(new DefaultRendererFactory(tr, er));

		// -- set up the actions ----------------------------------------------

		int hops = 5;

		// filter = new GraphDistanceFilter(graph, hops);
		filter = new LocalGraphFilter(graph, hops);
		filter.setEnabled(false);

		ActionList draw = new ActionList();
		draw.add(filter);

		draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,
				0, 0)));
		// todomab
		EdgeFillColorAction.setCOLORMAPRANGE(config.getCOLORMAPRANGE());//100.0);
		EdgeFillColorAction.setC1( config.getC1()); //ColorLib.rgb(200, 200, 255));
		EdgeFillColorAction.setC2( config.getC2() );//ColorLib.rgb(255, 100,100));
		EdgeFillColorAction.setPlaincolor(config.getPlaincolor());//ColorLib.rgb(200, 200,200));
		
		draw.add(new EdgeFillColorAction(edges));
		draw.add(new EdgeFillColorActionStroke(edges));
		
		draw.add(new FillColorAction(nodes));

		ForceDirectedLayout fdl = new ForceDirectedLayout(graph);
		ForceSimulator fsim = fdl.getForceSimulator();
		fsim.getForces()[0].setParameter(0, -1.2f);
		fsim.getForces()[2].setParameter(SpringForce.SPRING_LENGTH, 250.f);

		ActionList animate = new ActionList(ActionList.INFINITY);
		animate.add(fdl);
		animate.add(new RepaintAction());

		// finally, we register our ActionList with the Visualization.
		// we can later execute our Actions by invoking a method on our
		// Visualization, using the name we've chosen below.
		vis.putAction("draw", draw); //$NON-NLS-1$
		vis.putAction("layout", animate); //$NON-NLS-1$
		vis.runAfter("draw", "layout"); //$NON-NLS-1$ //$NON-NLS-2$

		// --------------------------------------------------------------------
		// STEP 4: set up a display to show the visualization

		Display display = new Display(vis);
		display.setSize(600, 500);
		setupDisplay(display);

		// --------------------------------------------------------------------
		// STEP 6: search panel
		SearchQueryBinding searchQ = new SearchQueryBinding(g.getNodeTable(),
				label);
		vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());

		searchQ.getPredicate().addExpressionListener(new UpdateListener() {
			public void update(Object src) {
				vis.run("draw"); //$NON-NLS-1$
			}
		});
		JSearchPanel search = searchQ.createSearchPanel();
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		// search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

		filter.clean(true);

		NodeItem focus = (NodeItem) vg.getNode(0);
		PrefuseLib.setX(focus, null, 400);
		PrefuseLib.setY(focus, null, 250);
		focusGroup.setTuple(focus);

		// SET FILTER
		filter.setDistance(1);
		filter.setEnabled(false);
		filter.run(0.0);
		vis.run("draw"); //$NON-NLS-1$

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(search, BorderLayout.SOUTH);

		// panel.setBorder(BorderFactory.createTitledBorder("Ricerca/Filtro"));

		toolbar = new AtlanteToolbar(this);
		toolbar.setOrientation(AtlanteToolbar.HORIZONTAL);
		toolbar.setFloatable(true);

		panel.add(toolbar, BorderLayout.NORTH);

		jsplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsplitpane.setRightComponent(display);
		jsplitpane.setLeftComponent(null);

		panel.add(jsplitpane, BorderLayout.CENTER);

		// now we run our action list and return
		return panel;
	}

	private void setupDisplay(Display display) {
		display.setForeground(Color.GRAY);
		display.setBackground(Color.WHITE);

		// // main display controls
		// display.addControlListener(new FocusControl(1));
		display.addControlListener(new DragControl());
		display.addControlListener(new PanControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new WheelZoomControl());
		display.addControlListener(new PopupControl("name", this)); //$NON-NLS-1$
		display.addControlListener(new NeighborHighlightControl());
		display.addControlListener(new ToolTipControl("weight")); //$NON-NLS-1$

		display.setForeground(Color.GRAY);
		display.setBackground(Color.WHITE);
	}

	static class MyNode implements Comparable{
		Tuple t = null;

		@Override
		public String toString() {

			return (String) t.get("name"); //$NON-NLS-1$
		}

		public boolean is(String type) {

			return ((String) t.get("type")).contains(type); //$NON-NLS-1$
		}

		public int compareTo(Object arg0) {
			return toString().compareToIgnoreCase(((MyNode)arg0).toString());
		}

	}

	Object[] addmenuAzionisti() {
		return addmenu("AZ"); //$NON-NLS-1$
	}

	Object[] addmenuQuotate() {
		return addmenu("SOC"); //$NON-NLS-1$
	}

	Object[] addmenuAmministratori() {
		return addmenu("DIR"); //$NON-NLS-1$
	}

	private Object[] addmenu(String type) {
		TupleSet ts = mygraph.getNodes();

		Collection<MyNode> as = new ArrayList<MyNode>();
		for (Iterator i = ts.tuples(); i.hasNext();) {
			Tuple t = (Tuple) i.next();
			MyNode m = new MyNode();
			m.t = t;
			if (m.is(type)) {
				m.t = t;
				as.add(m);
			}
		}
		Object[] t = as.toArray();
		Arrays.sort(t);
		return t;
	}

	// menu società
	public void menuQuoted() {

		jsplitpane.setLeftComponent(null);

		if (jlQuoted == null) {
			jlQuoted = new JList(addmenuQuotate());
			jlQuoted.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					displayNode(jlQuoted.getSelectedValue());
					jsplitpane.setLeftComponent(null);
				}
			});
			scrollpaneQ = new JScrollPane(jlQuoted);
			scrollpaneQ.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
			scrollpaneQ.setMinimumSize(new Dimension(150, Integer.MAX_VALUE));

		}
		jsplitpane.setLeftComponent(scrollpaneQ);
		jsplitpane.setVisible(true);
	}

	// menu azionisti
	public void menuStockHolder() {
		jsplitpane.setLeftComponent(null);
		if (jlStockHolder == null) {
			jlStockHolder = new JList(addmenuAzionisti());
			jlStockHolder.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					displayNode(jlStockHolder.getSelectedValue());
					jsplitpane.setLeftComponent(null);
				}
			});
			scrollpaneA = new JScrollPane(jlStockHolder);
			scrollpaneA.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
			scrollpaneA.setMinimumSize(new Dimension(150, Integer.MAX_VALUE));
		}
		jsplitpane.setLeftComponent(scrollpaneA);
		jsplitpane.setVisible(true);
	}

	public void menuBoard() {
		jsplitpane.setLeftComponent(null);
		if (jlBoards == null) {
			jlBoards = new JList(addmenuAmministratori());

			jlBoards.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					displayNode(jlBoards.getSelectedValue());
					jsplitpane.setLeftComponent(null);
				}
			});
			scrollpaneB = new JScrollPane(jlBoards);
			scrollpaneB.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
			scrollpaneB.setMinimumSize(new Dimension(150, Integer.MAX_VALUE));
		}
		jsplitpane.setLeftComponent(scrollpaneB);
		jsplitpane.setVisible(true);
	}
	
	public void displayNode(Object object) {
		// position and fix the default focus node
		Tuple ts = ((MyNode) object).t;
		displayNode(ts, true);
	}

	public void displayNode(Tuple ts, boolean fix) {
		// position and fix the default focus node
		if (ts == null) {
			System.out.println("null tuple "); //$NON-NLS-1$
			return;
		}
		VisualItem focus = null;

		TupleSet tset = vg.getNodes();
		for (Iterator iter = tset.tuples(); iter.hasNext();) {
			Tuple element = (Tuple) iter.next();
			if (element.getRow() == ts.getRow()) {
				focus = (VisualItem) element;
			}
		}
		if (focus == null) {
			System.out.println("focus tuple null " + ts); //$NON-NLS-1$
			return;
		}
		if (focus.getX() == 0) {
			PrefuseLib.setX(focus, null, 400);
			PrefuseLib.setY(focus, null, 250);
		}
		PrefuseLib.updateVisible(focus, true);

		if (fix) {
			focusGroup.setTuple(focus);
		}
		vis.run("draw"); //$NON-NLS-1$
	}

	public void diario() {

	}

	public void clear() {
		filter.clean(true);
	}

	public void showall() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		filter.setEnabled(false);
		Iterator items = vis.getGroup(graph).tuples();
		while (items.hasNext()) {
			VisualItem item = (VisualItem) items.next();
			item.setVisible(true);
			item.setExpanded(true);
		}

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void clearitem(VisualItem itemRoot) {
		focusGroup.removeTuple(itemRoot);
		PrefuseLib.updateVisible(itemRoot, false);
		itemRoot.setExpanded(false);

		Node a = (Node) itemRoot.getSourceTuple();

		for (Iterator iter = a.edges(); iter.hasNext();) {
			Edge edge = (Edge) iter.next();
			VisualItem ve = vis.getVisualItem(graph, edge);
			PrefuseLib.updateVisible(ve, false);
			ve.setExpanded(false);
		}

	}

	public void saveIMG() {
		ExportDisplayAction e = new ExportDisplayAction(vis.getDisplay(0));
		e.init();
		e.actionPerformed();

	}

	public void goWFTM() {
		BrowserLauncher.showDocument(Messages.getString("GraphView.siteAdress")); //$NON-NLS-1$

	}

	// public void saveSVG() throws IOException{
	// // Get a DOMImplementation.
	// DOMImplementation domImpl =
	// GenericDOMImplementation.getDOMImplementation();
	//		
	// // Create an instance of org.w3c.dom.Document.
	// String svgNS = "http://www.w3.org/2000/svg";
	// Document document = domImpl.createDocument(svgNS, "svg", null);
	//		
	// // Create an instance of the SVG Generator.
	// SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
	//		
	// // Ask the test to render into the SVG Graphics2D implementation.
	// //TestSVGGen test = new TestSVGGen();
	// //test.paint(svgGenerator);
	// vis.getDisplay(0).print(svgGenerator);
	//		
	// // Finally, stream out SVG to the standard output using
	// // UTF-8 encoding.
	// boolean useCSS = true; // we want to use CSS style attributes
	// //Writer out = new OutputStreamWriter(System.out, "UTF-8");
	//		
	// OutputStreamWriter f = new OutputStreamWriter(new
	// FileOutputStream("c:\\dati\\diario\\svg\\fileout.svg"),"UTF-8");
	// svgGenerator.stream(f, useCSS);
	// f.flush();
	// f.close();
	//		
	// try {
	// convertSVG2PDF(new File("c:\\dati\\diario\\svg\\fileout.svg"),new
	// File("c:\\dati\\diario\\svg\\fileout.jpg"));
	// } catch (IOException e) {
	//		
	// e.printStackTrace();
	// } catch (TranscoderException e) {
	//			
	// e.printStackTrace();
	// }
	//		
	// }
	// public void convertSVG2JPG(File svg, File jpg) throws IOException,
	// TranscoderException {
	//		
	// //Create transcoder
	// Transcoder transcoder = new JPEGTranscoder();
	// //Transcoder transcoder = new org.apache.fop.render.ps.PSTranscoder();
	//		
	// //Setup input
	// InputStream in = new java.io.FileInputStream(svg);
	// try {
	// TranscoderInput input = new TranscoderInput(in);
	//			
	// //Setup output
	// OutputStream out = new java.io.FileOutputStream(svg);
	// out = new java.io.BufferedOutputStream(out);
	// try {
	// TranscoderOutput output = new TranscoderOutput(out);
	//				
	// //Do the transformation
	// transcoder.transcode(input, output);
	// } finally {
	// out.close();
	// }
	// } finally {
	// in.close();
	// }
	// }
	// public void convertSVG2PDF(File svg, File pdf) throws IOException,
	// TranscoderException {
	//		
	// //Create transcoder
	// Transcoder transcoder = new PDFTranscoder();
	// //Transcoder transcoder = new org.apache.fop.render.ps.PSTranscoder();
	//		
	// //Setup input
	// InputStream in = new java.io.FileInputStream(svg);
	// try {
	// TranscoderInput input = new TranscoderInput(in);
	//			
	// //Setup output
	// OutputStream out = new java.io.FileOutputStream(pdf);
	// out = new java.io.BufferedOutputStream(out);
	// try {
	// TranscoderOutput output = new TranscoderOutput(out);
	//				
	// //Do the transformation
	// transcoder.transcode(input, output);
	// } finally {
	// out.close();
	// }
	// } finally {
	// in.close();
	// }
	// }

	public void expand(VisualItem item, String type) {

		// for (int i=0 ; i<count;i++)
		// {
		// Node n = ((Node)item).getChild(i);
		// displayNode(n,true);
		//
		// }
		displayNode(item, true);

		filter.run(0.0, type);
		vis.run("draw"); //$NON-NLS-1$

	}

} // end of class GraphView

