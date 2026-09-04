/*
 * File Name: Map2DMouseController.java
 * Created on: 06.12.2003
 *
 */
package graphic.math2D;

import graphic.mvc.IFocusPainter;
import graphic.mvc.Graph2D.Graph2DMouseController;
import graphic.mvc.Point2D.Point2DModel;

/**
 * Title: Map2DMouseController<p>
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
public class Map2DMouseController extends Graph2DMouseController {

	/**stores the Position from where Dragging started to revert to it. 
	 * Using float, because it is more (accurate) adaptive. 
	 */
	private float[] lastFocusPosition = new float[2];

	/**
	 * @param model_
	 * @param common_
	 */
	public Map2DMouseController(Point2DModel model_, IFocusPainter common_) {
		super(model_, common_);
	}

	/** Remove or add Points at the double Clicked Position
	 * Remove an Edge at the double Clicked Position
	 * 
	 * @param x Position of the DoubleClick
	 * @param y Position of the DoubleClick
	 * @return the Index of the Point added, or the negative Index of the Point removed, MIN_VALUE when no Point was changed
	 */
	protected int doubleClickMouse(int x, int y) {
		final int ret = super.doubleClickMouse(x, y); 
		if (ret >= 0) { //Point was added
			((Map2DModel)model).points.addItem(((Map2DModel)model).coordTrafo.unMap(x, y));
		} else if (ret == Integer.MIN_VALUE){
		} else {
			((Map2DModel)model).points.setAt(-ret, null);
		}
		return ret; //Point was removed
	}

	/** Store the Start Position	*/
	protected int pressedMouse(final int x, final int y) {
		final int focusPointIndex = super.pressedMouse(x, y);
		if (focusPointIndex >= 0) {
			System.arraycopy(
				((Map2DModel)model).points.getVectorAt(focusPointIndex),
				0, lastFocusPosition, 0, 2);
		}
		return focusPointIndex;
	}

	/** Store the Start Position	*/
	public int releasedMouse(final int x, final int y) {
		final int ret = super.releasedMouse(x, y);
		if (ret >= 0) { //also move the Original Coordinate of a Point
			System.arraycopy(
				lastFocusPosition, 0,
				((Map2DModel)model).points.getVectorAt(common.getFocusIndex()), 0,
				2);
		}
		return ret;
	}

	/** called on the Mouse drag Event */
	protected boolean dragMouse(int x, int y) {
		if (((Map2DModel)model).coordTrafo == null) return false; 
		if (super.dragMouse(x, y)) { //draggingCanvas
			if (common.getFocusIndex() >= 0) {
				//loses the Information about the original Location.
				((Map2DModel)model).coordTrafo.unMap(((Map2DModel)model).points.getVectorAt(common.getFocusIndex()), x, y);
			} else { //draggingCanvas (rather than all Points)
				((Map2DModel)model).coordTrafo.moveAt(x - lastPosition.getX(), y - lastPosition.getY());
				((Map2DModel)model).calcPoints(); //necessary!
			}
			return true;
		}
		return false;
	}

}
