/*
 * File Name: ClearingPainter.java
 * Created on: 13.12.2003
 *
 */
package graphic.mvc;

import graphic.IGraphText;

import java.awt.Rectangle;

/**
 * Title: ClearingPainter<p>
 * Description:
 * A Painter that only clears the Graphics Context up to its ClipBorders. 
 * Usually the first Painter in a chained Sequence of Painters. 
 * For lightweight Components like Applets, the Canvas needs to be prepared, 
 * for Forms this is usually done by the Form. 
 * When the whole Picture needs to be filled anyway, 
 * it is not necessary to clear it. 
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
 * mtime: 2026-09-05T12:43:04Z
 * digest: ac56bf661d2904d30266a0f443e08d361ee27dc09af08b5e15dbf61a9fc1f87a
 * stale: false
 * tags: [code/gui]
 * concepts: [Background-Clearing Painter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
final public class ClearingPainter implements IPainter {

	/** Clears the given graphics context's clip rectangle.
	 * @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public void draw(final IGraphText g) {
		final Rectangle rect = g.getClipBounds();
		g.clearRect(rect.x, rect.y, rect.width, rect.height);
		//gText.fillRect();
	}

}
