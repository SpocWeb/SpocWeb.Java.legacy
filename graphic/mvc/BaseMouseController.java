/*
 * File Name: BaseMouseController.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;

import graphic.Point2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Title: BaseMouseController<p>
 * Description:
 * Purpose:
 * Basic Helper Class for low Level Mouse Events
 * Determines the Distance of a Drag&Drop Event
 *
 * Known SubClasses: <none>
 * @see graphic.math2D.Coord2DMouseController
 * @see graphic.mvc.Point2D.Point2DMouseController
 *
 * Known Uses: <none>
 * @see graphic.svg.SvgApplet
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:44:45Z
 * digest: 54b32bc1d43cd045b3b37a4f8122df0336e610f7b66da921f67e42dd26eda195
 * stale: false
 * tags: [code/event_handling, code/ui_control]
 * concepts: [Mouse Controller Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class BaseMouseController 
implements MouseListener, MouseMotionListener, MouseWheelListener {

	/** cached Position for Moving Mouse Operations */
	protected Point2D lastPosition = new Point2D();

	/** The Modifier to block this Controller 	 */
	public int modifier; 

	/////////////////////////////////////////////////////////////////////////////////////
	/// high Level Mouse Callback Methods to overwrite...
	/////////////////////////////////////////////////////////////////////////////////////

	/** Remove or add Points at the double Clicked Position	
	 * 
	 * @param x Position of the DoubleClick
	 * @param y Position of the DoubleClick
	 * @return the Index of the Point added, or the negative Index of the Point removed, MIN_VALUE when no Point was changed
	 */
	protected int doubleClickMouse(final int x, final int y) { 
		return 0; 
	}

	/** 
	 * Store the Start Position and Focus Point	
	 * 
	 * @param x
	 * @param y
	 * @return the focusPointIndex or -1 if no Point was selected
	 */
	protected int pressedMouse(int x, int y) {
		return 0; 
	}

	/** Only calculates the targetPointIndex (usable for Drag&Drop)
	 * can be used for complex Operations, if no continuous moving is desired. 	
	 * 
	 * @param x
	 * @param y
	 * @return the targetPointIndex where the Button was released 
	 */
	public int releasedMouse(int x, int y) {
		return 0; 
	}

	/** called on the Mouse drag Event 
	 * 
	 * @param x Drag Destination Coordinate 
	 * @param y Drag Destination Coordinate 
	 * @return true when the Position actually changed! 
	 */
	protected Object dragMouse(int x, int y, int dx, int dy) {
		dragMouse(x, y);
		return null;
	}

	/** called on the Mouse drag Event 
	 * TODO remove this Method
	 * @param x Drag Destination Coordinate 
	 * @param y Drag Destination Coordinate 
	 * @return true when the Position actually changed! 
	 * @deprecated rather use #dragMouse(int x, int y, int dx, int dy)
	 */
	protected boolean dragMouse(int x, int y) {
		return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Low Level MouseListener Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Does nothing; clicks are handled via {@link #mousePressed(MouseEvent)}/{@link #mouseReleased(MouseEvent)} instead.
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)	 */
	public void mouseClicked(final MouseEvent e) { }

	/** Does nothing; no hover behaviour is implemented at this level.
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)	 */
	public void mouseEntered(final MouseEvent e) { }

	/** Does nothing; no hover behaviour is implemented at this level.
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)	 */
	public void mouseExited(MouseEvent e) { }

	/** Records the press position and delegates to {@link #pressedMouse(int, int)}.
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)	 */
	public void mousePressed(final MouseEvent evt) {
		final int x = evt.getX();
		final int y = evt.getY();
		pressedMouse(x, y);
		lastPosition.x = x;
		lastPosition.y = y;
	}

	/** Delegates to {@link #doubleClickMouse(int, int)} on a double click, otherwise to
	 * {@link #releasedMouse(int, int)}, and records the release position.
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)	 */
	public void mouseReleased(final MouseEvent evt) {
		final int x = evt.getX();
		final int y = evt.getY();
		if (evt.getClickCount() == 2) {
			doubleClickMouse(x, y);
		} else {
			releasedMouse(x, y);
		}
		lastPosition.x = x;
		lastPosition.y = y;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Low Level MouseMotionListener Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Does nothing; passive mouse movement without a button held is not tracked.
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)	 */
	public void mouseMoved(final MouseEvent e) { }

	/** Computes the delta since the last recorded position and delegates to
	 * {@link #dragMouse(int, int, int, int)}, skipping the call when the position is unchanged.
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)	 */
	public void mouseDragged(final MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();
		final int dx = (x - lastPosition.x);
		final int dy = (y - lastPosition.y);
		if ((dx == 0) && (dy == 0)) { //draggingCanvas
			return;
		}
		dragMouse(x, y, dx, dy);
		lastPosition.x = x;
		lastPosition.y = y;
	}

	/** Does nothing; mouse wheel input is not currently wired to any drag behaviour.
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		//dragMouse(e.getWheelRotation()); 
	}

}
