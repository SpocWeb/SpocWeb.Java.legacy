/*
 * File Name: Point2DKeyController.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc.Point2D;

import graphic.mvc.IFocusPainter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import streamIO.Log;

/**
 * Title: Point2DKeyController<p>
 * Description:
 * High-Level Controller, 
 * used passively by the Point2DPainter to perform (modifying) Actions on the Model
 * like deleting the active Node, inserting a new at the Mouse Position
 * or changing the Name of the active Node. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Design Decision: 
 * This Controller implements KeyListener directly, 
 * but uses a second Level Controller for MouseListener, 
 * because these Events are too low Level! 
 * @see #focusPointIndex is kept here to allow for several concurrent Views 
 * and Controllers to work with the same Model. 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:45:39Z
 * digest: 0114a8ef2b3f544e6457867e38b060e48b42a09b8f744b01e33edec63709aaa8
 * stale: false
 * tags: [code/event_handling, code/interactive_editing]
 * concepts: [Point2D Keyboard Controller]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Point2DKeyController 
implements KeyListener {

	/** streamIO for Logging */
	private static final Log L = new Log(Point2DKeyController.class, 2);

	/* Key Codes for the Navigational Keys */
	/** Virtual key code for Page Up, moves the focus point/canvas backward on the z axis. */
	final static public int KEY_PgUp = 33; //1002;
	/** Virtual key code for Page Down, moves the focus point/canvas forward on the z axis. */
	final static public int KEY_PgDn = 34; //1003;
	/** Virtual key code for Up, moves the focus point/canvas up. */
	final static public int KEY_up = 38; //1004;
	/** Virtual key code for Down, moves the focus point/canvas down. */
	final static public int KEY_down = 40; //1005;
	/** Virtual key code for Left, moves the focus point/canvas left. */
	final static public int KEY_left = 37; //1006;
	/** Virtual key code for Right, moves the focus point/canvas right. */
	final static public int KEY_right = 39; //1007;

	/* Key Codes for the other Keys */
	/** Virtual key code for Backspace, deletes the last character of the focused label. */
	final static public int KEY_BackSpace = 8;
	/** Virtual key code for Delete, removes the focused point. */
	final static public int KEY_Delete = 127;
	/** Virtual key code for Enter, advances the focus to the next point. */
	final static public int KEY_Enter = 10;
	//final static public int KEY_Tab = 100; //not propagated!!!

	/** Unused reserved key code placeholder. */
	final static public int KEY_ = 100;

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Switches defaulting undefined Node Names to their Number. 
	 */
	public boolean defaultNameToPosition = true;

	/** Lightweight State shared between View and Controller */
	protected final IFocusPainter common;

	/** Reference to the basic Model */
	protected final Point2DModel model;  

	/** Creates a controller wired to the given model and the focus/view state it shares
	 * with the corresponding painter.
	 * @param model_ the model this controller edits
	 * @param common_ the shared focus state and view to redraw after an edit
	 */
	public Point2DKeyController(final Point2DModel model_, final IFocusPainter common_) {
		this.common = common_;
		this.model = model_; 
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Controller Methods (High-Level Events) 
	/////////////////////////////////////////////////////////////////////////////////

	/** Does nothing; this controller only reacts to key press/release, not typed characters.
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)	 */
	public void keyTyped(final KeyEvent e) {}

	/** 
	 * Moves the ViewPoint or the State with key Presses.
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)	 
	 */
	public void keyPressed(final KeyEvent evt) {
		//int key = evt.getKeyChar();  
		int key = evt.getKeyCode();
		//most granular, contains also Function Keys
		//		int key = evt.getKeyChar(); 
		L.n("processKeyEvent#").l(key);
		int stepSize = 1;
		if (evt.isControlDown()) {
			stepSize = 10;
		}
		switch (key) { //move the ViewPoint without changing the Rotation Vector!
			//case KEY_Enter: state++; Body3DG = null; break;	//Return
			case KEY_PgDn  : model.moveBy(0, 0, +stepSize, common.getFocusIndex()); break; //
			case KEY_PgUp  : model.moveBy(0, 0, -stepSize, common.getFocusIndex()); break; //
			case KEY_up    : model.moveBy(0, -stepSize, 0, common.getFocusIndex()); break; //
			case KEY_down  : model.moveBy(0, +stepSize, 0, common.getFocusIndex()); break; //
			case KEY_left  : model.moveBy(-stepSize, 0, 0, common.getFocusIndex()); break; //
			case KEY_right : model.moveBy(+stepSize, 0, 0, common.getFocusIndex()); break; //
			default : //editing of Point Labels
				editKey(evt.getKeyChar());
				return;
		}
	}

	/** Does nothing; this controller acts only on key press.
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)	 */
	public void keyReleased(final KeyEvent e) {}

	/** pressed a 'regular' Key  */
	protected void editKey(char key) {
		L.n("editKey(",1).l(key,1).l("=",1).l((int)key,1).l(")",1);
		final int index = common.getFocusIndex();
		if (key == KEY_Enter) { //flip though the Points
			if (index+1 >= model.mapPoints.getInt()) {
				common.setFocusIndex(-1); 
			} else {
				common.setFocusIndex(index+1); 
			}
			common.draw(null);
			return; 
		}
		if (index < 0) {
			return;
		}
		if (key == KEY_Delete) { //flip though the Points
			model.removePoint(index);
		} else if (key == KEY_BackSpace) { //
			String str = model.getLabel(index);
			int strLength = str.length();
			if (strLength > 0) {
				model.pointLabels.setAt(index, str.substring(0, strLength - 1));
			}
		} else if (Character.isJavaIdentifierPart((char) key)) {
			model.pointLabels.setAt(index,
				model.getLabel(index) + (char) key);
		} else {
			return;
		}
		model.repaint();
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
