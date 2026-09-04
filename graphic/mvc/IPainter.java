/*
 * File Name: IPainter.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

/**
 * Title: IPainter<p>
 * Description:
 * Interface for a View or Painter Object
 * Could also be named IDrawAble or IShape
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IPainter {

	/** paint Routine called by #paint(Graphics)
	 * @param gText the Graphics Context, 
	 * if null the Painter tries to acquire it itself and repaints.
	 * Thus the repaint() Event Method is saved.  
	 */
	void draw(final IGraphText gText);
	
}
