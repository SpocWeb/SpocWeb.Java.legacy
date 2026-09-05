/*
 * File Name: IPainter.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

/**
 * Interface for a View/Painter object that renders itself onto a graphics context.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:42:05Z
 * digest: 10256133fce5514114689ea94983ee890c94d4827625f47a7d216411a219428a
 * stale: false
 * tags: [code/gui]
 * concepts: [Painter/View Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPainter {

	/** Draws this painter's content, called by paint(Graphics).
	 * @param gText the Graphics Context,
	 * if null the Painter tries to acquire it itself and repaints.
	 * Thus the repaint() Event Method is saved.
	 */
	void draw(final IGraphText gText);

}
