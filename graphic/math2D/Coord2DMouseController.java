/*
 * File Name: Coord2DMouseController.java
 * Created on: 07.12.2003
 *
 */
package graphic.math2D;

import graphic.Point2D;
import graphic.mvc.BaseMouseController;
import graphic.mvc.IRepainter;
import streamIO.Log;

/**
 * Controls drag-and-drop panning of the view pane's {@link Coordinates2D}, with shrink on
 * single click and enlarge on double click.
 *
 * <p>Title: Coord2DMouseController<p>
 * Description:
 * Purpose:
 * Controls Drag & Drop of the ViewPane.
 * Shrinking with Click
 * Enlarging with DoubleClick
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
 * @see Coordinates2D the coordinate system this controller changes
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:54Z
 * digest: f0d75e45c453366785563fe3edb13b37f7fbbbeeb3dc50fa7c5aae47f45411ad
 * stale: false
 * tags: [code/view_model]
 * concepts: [2D Coordinate Mouse Controller]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Coord2DMouseController
extends BaseMouseController {

	/** streamIO for Logging */
	private static final Log L = new Log(Coord2DMouseController.class, 1);

	/** cached Position for Mouse Operations */
	protected Point2D lastPosition = new Point2D();

	/** Reference to the Coordinates Object 
	 * to be able to change it.
	 */
	protected final Coordinates2D coords;

	/** Reference to the Coordinates Object 
	 * to be able to change it.
	 */
	protected final IRepainter view;

	/**
	 * Constructor gets a Reference to the Coordinates Object
	 * @param coords_
	 */
	public Coord2DMouseController(final Coordinates2D coords_, final IRepainter view_) {
		this.coords = coords_;
		this.view = view_;
	}

	/** called on the Mouse drag Event */
	protected Object dragMouse(final int x, final int y, final int dx, final int dy) {
		L.n("x=").l(x).l("y=").l(y).l("dx=").l(dx).l("dy=").l(dy);
		coords.moveAt(dx, dy);
		view.repaint();
		return null;
	}
	
}
