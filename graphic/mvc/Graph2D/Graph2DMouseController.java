/*
 * File Name: Graph2DMouseController.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc.Graph2D;

import graphic.Point2D;
import graphic.mvc.IFocusPainter;
import graphic.mvc.Point2D.Point2DModel;
import graphic.mvc.Point2D.Point2DMouseController;
import graphs.Edge;
import graphs.IEdgeStreamIn;
import streamIO.Log;

/**
 * Mouse controller for {@link Graph2DModel}: extends {@link Point2DMouseController} with
 * dragging a released point onto another to add an edge, and double-clicking an edge to
 * remove it.
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
 * mtime: 2026-09-05T12:51:17Z
 * digest: 04fa73aaa9d93a9ddb38b0fd0e9baf28c69c1b14fe57c45090900c53271e79c3
 * stale: false
 * tags: [code/event_handling, code/interactive_editing]
 * concepts: [2D Graph Mouse Controller]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Graph2DMouseController 
extends Point2DMouseController {

	/** streamIO for Logging */
	private static final Log L = new Log(Graph2DMouseController.class, 1);

	/** Flag to allow adding Edges dynamically by Drag&Drop */
	public boolean allowAddingEdges = true;

	/** Creates a graph mouse controller wired to the given model and shared focus state.
	 * @param model_
	 * @param common_
	 */
	public Graph2DMouseController(final Point2DModel model_, final IFocusPainter common_) {
		super(model_, common_);
	}

	/** Store the Start Position	*/
	public int releasedMouse(final int x, final int y) {
		L.n("releasedMouse(").l(x).l(",").l(y).l(")");
		final int nearestNeighbor = super.releasedMouse(x, y);
		L.l("nearestNeighbor = ").l(nearestNeighbor); 
		if (allowAddingEdges) {
			if (nearestNeighbor >= 0) { 
				((Graph2DModel)model).edges.addEdge(common.getFocusIndex(), nearestNeighbor, true);
				model.mapPoints.getPointAt(common.getFocusIndex()).setLocation(lastFocusMap); //place the Point back to it's original Location
				model.repaint();
			}
		}
		return nearestNeighbor;
	}

	/** Remove or add Points at the double Clicked Position
	 * Remove an Edge at the double Clicked Position
	 * 
	 * @param x Position of the DoubleClick
	 * @param y Position of the DoubleClick
	 * @return the Index of the Point added, or the negative Index of the Point removed, MIN_VALUE when no Point was changed
	 */
	protected int doubleClickMouse(final int x, final int y) {
		//test whether an Edge has been clicked
		final Point2D p2 = new Point2D(x + x, y + y);
		IEdgeStreamIn edgeStream = ((Graph2DModel)model).edges.EdgeIterator();
		for (Edge edge; null != (edge = edgeStream.nextEdge());) {
			final Point2D start = model.mapPoints.getPointAt(edge.key);
			final Point2D stop = model.mapPoints.getPointAt(edge.val);
			if ((start != null) && (stop != null)) {
				if (p2
					.isNeighbour(
						start.getX() + stop.getX(),
						start.getY() + stop.getY(),
						common.getPointRadius()*2)) {
					((Graph2DModel)model).edges.removeEdge(edge.key, edge.val, edge.typ);
					model.repaint();
					return Integer.MIN_VALUE;
				}
			}
		}
		return super.doubleClickMouse(x, y);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	
	 */
	public static void main(final String[] args) throws Exception {
		Graph2DPainter.main(args);
	}

}
