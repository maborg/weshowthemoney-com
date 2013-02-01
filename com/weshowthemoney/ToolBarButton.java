package com.weshowthemoney;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

/** Part of a small example showing basic use of JToolBar.
 *  The point here is that dropping a regular JButton in a
 *  JToolBar (or adding an Action) doesn't give you what
 *  you want -- namely a small button just enclosing the icon,
 *  and with text labels (if any) below the icon, not to the right of it.
 *  1999 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 */

public class ToolBarButton extends JButton {
  /**
	 * 
	 */
	private static final long serialVersionUID = -1846102978492552899L;
private static final Insets margins =
    new Insets(0, 0, 0, 0);

  public ToolBarButton(Icon icon) {
    super(icon);
    setMargin(margins);
    setVerticalTextPosition(BOTTOM);
    setHorizontalTextPosition(CENTER);
    setBackground(Color.WHITE);
  }

  public ToolBarButton(URL imageFile) {
	  this( new ImageIcon(imageFile) ) ;
    
  }

  public ToolBarButton(String imageFile, String text) {
    this(new ImageIcon(imageFile));
    setText(text);
  }
}
