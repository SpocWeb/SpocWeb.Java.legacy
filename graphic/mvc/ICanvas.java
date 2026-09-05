/*
 * File Name: ICanvas.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphImage;

import java.awt.Dimension;

/**
 * Interface for a passive drawing surface that exposes its graphics context and size,
 * and can be told to repaint.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:46:31Z
 * digest: 47c08a57f7be04a8d5ed1e88542cfbe7fa8e25733763474ef18fe0ce203cbab0
 * stale: false
 * tags: [code/gui]
 * concepts: [Canvas Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface ICanvas 
extends IRepainter
{

	//Graphics getGraphics();
	
	/** Returns the graphics context to draw onto; its ClipBounds may be smaller than the
	 * actual canvas size, which is retrieved separately via {@link #getSize()}.
	 */
	public abstract IGraphImage getIGraphImage();

	/**
	 * Shows the Canvas, 
	 * not necessarily repaints it. 
	 * TODO: check whether to reuse the repaint() Method
	 */
	//public abstract void show(); 

	/**
	 * Returns this canvas's actual dimensions.
	 * @return the actual Size of the Canvas
	 * as opposed to the ClipBounds of the Graphics Context.
	 */
	public abstract Dimension getSize();

}
