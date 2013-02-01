package com.weshowthemoney;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.VisualItem;

/**
 * Swing component for configuring the parameters of the Force functions in a
 * given ForceSimulator. Useful for exploring different parameterizations when
 * crafting a visualization.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class MyPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1565692535587164105L;

	

	@SuppressWarnings("unused")
	private ForceSimulator fsim;

	private Visualization vis;

	

	private LocalGraphFilter filter;


	/**
	 * Create a new JForcePanel
	 * 
	 * @param fsim
	 *            the ForceSimulator to configure
	 * @param vis
	 * @param g
	 * @param filter
	 * @param label
	 */
	public MyPanel(ForceSimulator fsim, Visualization vis, Graph g,
			LocalGraphFilter filter, String label) {
		this.fsim = fsim;
		this.vis = vis;

		this.filter = filter;

		this.setBackground(Color.WHITE);
		initUI();
	}

	/**
	 * Initialize the UI.
	 */
	private void initUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		final JValueSlider slider = new JValueSlider("Distance", 0, 5, 1);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setDistance(slider.getValue().intValue());
				filter.clean(true);
				vis.run("draw");
			}
		});
		slider.setBackground(Color.WHITE);
		slider.setPreferredSize(new Dimension(300, 30));
		slider.setMaximumSize(new Dimension(300, 30));

		Box cf = new Box(BoxLayout.X_AXIS);
		cf.add(slider);

		JCheckBox jc = new JCheckBox("Filtro Distanza");
		cf.add(jc);
		jc.setSelected(true);
		jc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean sel = ((JCheckBox) e.getSource()).isSelected();
				filter.setEnabled(sel);

				// mark all visible
				if (!sel) {
					Iterator items = vis.items(Visualization.ALL_ITEMS);
					while (items.hasNext()) {
						VisualItem item = (VisualItem) items.next();

						PrefuseLib.updateVisible(item, true);
						item.setExpanded(true);

					}
				}
			}
		});

		this.add(cf);

	}

	// /**
	// * Create an entry for configuring a single parameter.
	// */
	// private static JValueSlider createField(Force f, int param) {
	// double value = f.getParameter(param);
	// double min = f.getMinValue(param);
	// double max = f.getMaxValue(param);
	// String name = f.getParameterName(param);
	//        
	// JValueSlider s = new JValueSlider(name,min,max,value);
	// s.setBackground(Color.WHITE);
	// s.putClientProperty("force", f);
	// s.putClientProperty("param", new Integer(param));
	// s.setPreferredSize(new Dimension(300,30));
	// s.setMaximumSize(new Dimension(300,30));
	// return s;
	// }

	/**
	 * Change listener that updates paramters in response to interaction.
	 */
//	private static class ForcePanelChangeListener implements ChangeListener {
//		public void stateChanged(ChangeEvent e) {
//			JValueSlider s = (JValueSlider) e.getSource();
//			float val = s.getValue().floatValue();
//			Force f = (Force) s.getClientProperty("force");
//			Integer p = (Integer) s.getClientProperty("param");
//			f.setParameter(p.intValue(), val);
//		}
//	} // end of inner class ForcePanelChangeListener

	/**
	 * Create and displays a new window showing a configuration panel for the
	 * given ForceSimulator.
	 * 
	 * @param fsim
	 *            the force simulator
	 * @return a JFrame instance containing a configuration interface for the
	 *         force simulator
	 */
	public static JFrame showForcePanel(ForceSimulator fsim) {
		JFrame frame = new JFrame("prefuse Force Simulator");
		frame.setContentPane(new JForcePanel(fsim));
		frame.pack();
		frame.setVisible(true);
		return frame;
	}

} // end of class JForcePanel
