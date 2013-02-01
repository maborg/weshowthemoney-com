package com.weshowthemoney;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.BreadthFirstIterator;
import prefuse.data.util.FilterIterator;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;

public class LocalGraphFilter extends GraphDistanceFilter {
	
	public LocalGraphFilter(String group,int distance) {
		super(group, distance);
	}
    public void run(double frac) {
    	run(frac,null);
    }
    public void run(double frac,String type) {
        // mark the items
//        Iterator items = m_vis.visibleItems(m_group);
//        while ( items.hasNext() ) {
//            VisualItem item = (VisualItem)items.next();
//            item.setDOI(Constants.MINIMUM_DOI);
//        }
//        
        // set up the graph traversal
        TupleSet src = m_vis.getGroup(m_sources);
        
     
        
        Iterator srcs = new FilterIterator(src.tuples(), m_groupP);
        m_bfs.init(srcs, m_distance, Constants.NODE_AND_EDGE_TRAVERSAL);
        
        // traverse the graph
        while ( m_bfs.hasNext() ) {
            VisualItem item = (VisualItem)m_bfs.next();
            int d = m_bfs.getDepth(item);
            
            if (type == null || 
            		(item.getSourceTuple().getColumnIndex("type")>=0 && 
            		item.getSourceTuple().getString("type").contains(type)))
            {
	            PrefuseLib.updateVisible(item, true);
	            
	            item.setDOI(-d);
	            item.setExpanded(d < m_distance);
	            if ( type!= null){
	            	TableNodeItem ti = (TableNodeItem) item;
	            	
	            	Iterator it = ti.outEdges();
	            	
	            	while (it.hasNext()) {
						EdgeItem edge = (EdgeItem) it.next();
						NodeItem source = edge.getSourceItem();
						NodeItem target = edge.getTargetItem();
						
						if (source!=null && 
							source.getSourceTuple().getString("type").contains(type) &&
							target.isVisible()
								
						)
						{
				            PrefuseLib.updateVisible(edge, true);
				            
				            item.setDOI(-d);
				            item.setExpanded(d < m_distance);
							
						}
					}
	            	//--------------------------------
	            	it = ti.inEdges();
	            	
	            	while (it.hasNext()) {
						EdgeItem edge = (EdgeItem) it.next();
						NodeItem source = edge.getSourceItem();
						NodeItem target = edge.getTargetItem();
						
						if (target!=null && 
							target.getSourceTuple().getString("type").contains(type) &&
							source.isVisible()
								
						)
						{
				            PrefuseLib.updateVisible(edge, true);
				            
				            item.setDOI(-d);
				            item.setExpanded(d < m_distance);
							
						}
					}

	            }
            }
        }
        
    }
    BreadthFirstIterator getBFS(){
    	return m_bfs;
    }
	
public void clean(boolean cleanAlways ){
    // mark unreached items
  Iterator items = m_vis.visibleItems(m_group);
  while ( items.hasNext() ) {
      VisualItem item = (VisualItem)items.next();
      
      if (cleanAlways ||   item.getDOI() == Constants.MINIMUM_DOI ) {
          PrefuseLib.updateVisible(item, false);
          item.setExpanded(false);
          item.setDOI(Constants.MINIMUM_DOI);
      }
  }

}
public void showall(){
    // mark unreached items
  Iterator items = m_vis.items(m_group);
  while ( items.hasNext() ) {
      VisualItem item = (VisualItem)items.next();
      item.setDOI(-1);
          PrefuseLib.updateVisible(item, true);
          item.setExpanded(true);
  }

}

}
