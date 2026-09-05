/*
 * File Name: FloatPointKeyController.java
 * Created on: 13.12.2003
 *
 */
package graphic.math3D;

import graphic.mvc.MultiPainter;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Title: FloatPointKeyController<p>
 * Description:
 * Moves the given Vector proportional to the Mouse Drag Movement.  
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
 * digest: 0a7776602a52eda30d4e4e800afb601685439486d5d67dc7573e6385819cf7cf
 * stale: false
 * tags: [code/keyboard_input]
 * concepts: [Keyboard-Driven Point Controller]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class FloatPointKeyController 
extends MultiPainter
implements KeyListener {

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** The Delta to move by */
	private double smallDelta = 0.02; 

	/** The Point to move 	 */
	private float[] point;

	/** The Modifier to block this Controller 	 */
	public int modifier = 0; 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor
	 * @param point_ the Point to move, typically shared by Reference 
	 */
	public FloatPointKeyController(final float[] point_) {
		this.point = point_;
		if (point.length < 2) {
			throw new ArrayIndexOutOfBoundsException(" The Point must have at least two Dimensions!");
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** 
	 * moves the 3D Point using Keystrokes
	 * @param key
	 */
	private void movePoint(final int key, final int modifiers) {
		double delta = smallDelta;
		if (0 != (modifiers & InputEvent.CTRL_DOWN_MASK)) { 
			delta *= 10; } 
		switch (key) {	//move the point without changing the Rotation Vector!
			case KeyEvent.VK_PAGE_UP: if (point.length > 2) point[2]+=delta; break;	//
			case KeyEvent.VK_PAGE_DOWN: if (point.length > 2) point[2]-=delta; break;	//
			case KeyEvent.VK_UP: 
			case KeyEvent.VK_KP_UP: point[1]+=delta; break;	//
			case KeyEvent.VK_DOWN: 
			case KeyEvent.VK_KP_DOWN: point[1]-=delta; break;	//
			case KeyEvent.VK_LEFT: 
			case KeyEvent.VK_KP_LEFT: point[0]-=delta; break;	//
			case KeyEvent.VK_RIGHT: 
			case KeyEvent.VK_KP_RIGHT: point[0]+=delta; break;	//
			default: return; 
		}
		draw(null);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// KeyListener Event Callback Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Does nothing; only key press and release move the point.
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)	 */
	public void keyTyped(KeyEvent e) { }

	/** Moves the point when the pressed key's modifiers match this controller's {@link #modifier}.
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)	 */
	public void keyPressed(KeyEvent e) {
		final int mod = e.getModifiersEx() & ~InputEvent.CTRL_DOWN_MASK;
		if ((modifier == 0) && (mod != 0)) {
			return; }
		if ((modifier & mod) != modifier) {
			return; } 
		movePoint(e.getKeyCode(), e.getModifiersEx()); 
	}

	/** Does nothing; only key press and release move the point.
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)	 */
	public void keyReleased(KeyEvent e) {}

}
