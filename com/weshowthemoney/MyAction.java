/**
 * 
 */
package com.weshowthemoney;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;

class MyAction extends AbstractAction{
	private Object owner;
	private static final long serialVersionUID = 2734499685524942590L;
	private Method actionMethod;
	
	MyAction(String buttonName,String methodName, Object owner2) throws SecurityException, NoSuchMethodException{
		super(buttonName);
		this.owner=owner2;
		if (methodName!=null && methodName.length()>0)
			actionMethod = GraphView.class.getDeclaredMethod(methodName );
		
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			if (actionMethod!=null)
				actionMethod.invoke(owner);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
}