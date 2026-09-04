/*
 * File Name: ICanvas.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphImage;

import java.awt.Dimension;

/**
 * Title: ICanvas<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public interface ICanvas 
extends IRepainter
{

	//Graphics getGraphics();
	
	/** This Graphics Context definitely has ClipBounds, 
	 * but these Bounds may be smaller than the Canvas Size, 
	 * which has to be retrieved by @see #getSize()
	 */
	public abstract IGraphImage getIGraphImage();

	/**
	 * Shows the Canvas, 
	 * not necessarily repaints it. 
	 * TODO: check whether to reuse the repaint() Method
	 */
	//public abstract void show(); 

	/**
	 * @return the actual Size of the Canvas 
	 * as opposed to the ClipBounds of the Graphics Context.   
	 */
	public abstract Dimension getSize();

}
