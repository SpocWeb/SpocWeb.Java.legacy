/*
 * File Name: Point2DMouseController.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc.Point2D;

import graphic.Point2D;
import graphic.mvc.BaseMouseController;
import graphic.mvc.IFocusPainter;
import streamIO.Log;

/**
 * Mouse controller for {@link Point2DPainter}/{@link Point2DModel}: clicking adds or removes
 * a point, and dragging moves the focused point or pans the whole canvas.
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
 * mtime: 2026-09-05T12:46:06Z
 * digest: d53606260afcfb332bae03fbaf7adb2b67bc53fc42f2d796ea294aade1d8d722
 * stale: false
 * tags: [code/event_handling, code/interactive_editing]
 * concepts: [Point2D Mouse Controller]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Point2DMouseController 
extends BaseMouseController {
	
	/** streamIO for Logging */
	private static final Log L = new Log(Point2DMouseController.class, 1);
	
	/**stores the Position from where Dragging started to revert to it. 	 */
	protected Point2D lastFocusMap = new Point2D();
	
	/** Reference to the High-Level Controller */ 
	protected final Point2DModel model;
	
	/** Reference to the High-Level Controller */ 
	protected final IFocusPainter common;
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a mouse controller wired to the given model and the focus/view state it shares
	 * with the corresponding painter.
	 * @param model_ the model this controller edits
	 * @param common_ the shared focus state and view to redraw after an edit
	 */
	public Point2DMouseController(final Point2DModel model_, final IFocusPainter common_) { //
		common = common_; 
		model = model_; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// High Level Mouse Events (Clicking & Dragging)
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Remove or add Points at the double Clicked Position	
	 * 
	 * @param x Position of the DoubleClick
	 * @param y Position of the DoubleClick
	 * @return the Index of the Point added, or the negative Index of the Point removed, MIN_VALUE when no Point was changed
	 */
	protected int doubleClickMouse(final int x, final int y) {
		L.n("doubleClickMouse(").l(x).l(",").l(y).l(")");
		//int oldFocus = focusPointIndex;
		common.setFocusIndex(x, y);
		//if (oldFocus == focusPointIndex) { //remove the Point
		final int index = common.getFocusIndex();
		final int ret;
		if (index >= 0) { //remove the Point
			model.removePoint(index);
			ret = -index;
		} else { //add a new Point
			model.addPoint(x, y);
			ret = model.mapPoints.getInt()-1;
		}
		model.repaint(); //for both adding and removing
		return ret;
	}

	/** 
	 * Store the Start Position and Focus Point	
	 * 
	 * @param x
	 * @param y
	 * @return the focusPointIndex or -1 if no Point was selected
	 */
	protected int pressedMouse(int x, int y) {
		L.n("pressedMouse(").l(x).l(",").l(y).l(")");
		final int oldFocus = common.getFocusIndex();
		final int newFocus = common.setFocusIndex(x, y);
		L.n("focusPoint=" + newFocus);
		if (newFocus >= 0) { //recalc MapPoint
			lastFocusMap.copyAt(model.mapPoints.getPointAt(newFocus));
		}
		if (oldFocus != newFocus) { 
			common.draw(null); //repaint the associated View (asynchronously)
		} //not necessary to repaint everything!
		L.n("oldFocus =" + oldFocus + " focusPointIndex=" + newFocus);
		return newFocus;
	}

	/** Only calculates the targetPointIndex (usable for Drag&Drop)
	 * can be used for complex Operations, if no continuous moving is desired. 	
	 * 
	 * @param x
	 * @param y
	 * @return the targetPointIndex where the Button was released 
	 */
	public int releasedMouse(final int x, final int y) {
		L.n("releasedMouse(").l(x).l(",").l(y).l(")");
		if (common.getFocusIndex() >= 0) { //possible drag&drop
			int targetPointIndex =  common.getFocusIndex(x, y);
			if (targetPointIndex == common.getFocusIndex()) {
				//search for another Point (Overlap)
				targetPointIndex =
					model.mapPoints.findIndexOfLastNeighbour(x, y, common.getPointRadius(),
						targetPointIndex);
//				L.l("no Drag&Drop on itself!"); 
//				return -1; 
			}
			if (targetPointIndex >= 0) 
				return targetPointIndex;
		} else {
		}
		return -1;
	}
	
	/** called on the Mouse drag Event 
	 * 
	 * @param x Drag Destination Coordinate 
	 * @param y Drag Destination Coordinate 
	 * @return true when the Position actually changed! 
	 */
	protected boolean dragMouse(final int x, final int y) {
		L.n("dragMouse(", -1).l(x, -1).l(",", -1).l(y, -1).l(")", -1);
		final int index = common.getFocusIndex();
		if (index >= 0) {
			//loses the Information about the original Location.
			model.mapPoints.getPointAt(index).copyAt(x, y);
		} else { //draggingCanvas (rather than all Points)
			model.moveBy(x - lastPosition.getX(), y - lastPosition.getY(), 0, index);
		}
		lastPosition.setX(x);
		lastPosition.setY(y);
		model.repaint(); //repaint ALL Views due to Model Change
		return true;
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
		Point2DPainter.main(args);
	}

}
