/*
 * File Name: IController.java
 * Created on: 07.12.2003
 *
 */
package graphic.mvc;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Interface for a low-level controller that exposes AWT input-listener registration hooks.
 *
 * Known SubInterfaces:
 * @see graphic.mvc.Point2D.ICanvas 
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
 * mtime: 2026-09-05T12:42:15Z
 * digest: 2df85afa152d886d09ccf688118f1960b8d41a95ff0e6275f54adb924945f783
 * stale: false
 * tags: [code/event_handling, code/ui_control]
 * concepts: [Controller Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IController 
extends IPaintEventSource 
{

	// Hooks for registering the Controllers

	/** Registers a listener for mouse click/press/release events.
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener) */
	public void addMouseListener(final MouseListener listener);

	/** Unregisters a previously added mouse listener.
	 * @see java.awt.Component#removeMouseListener(java.awt.event.MouseListener) */
	public void removeMouseListener(final MouseListener listener);

	/** Registers a listener for mouse move/drag events.
	 * @see java.awt.Component#removeMouseMotionListener(java.awt.event.MouseMotionListener) */
	public void addMouseMotionListener(final MouseMotionListener listener);

	/** Unregisters a previously added mouse motion listener.
	 * @see java.awt.Component#removeMouseMotionListener(java.awt.event.MouseMotionListener) */
	public void removeMouseMotionListener(final MouseMotionListener listener);

	/** Registers a listener for mouse wheel events.
	 * @see java.awt.Component#addMouseWheelListener(java.awt.event.MouseWheelListener) */
	public void addMouseWheelListener(final MouseWheelListener listener);

	/** Unregisters a previously added mouse wheel listener.
	 * @see java.awt.Component#removeMouseWheelListener(java.awt.event.MouseWheelListener) */
	public void removeMouseWheelListener(final MouseWheelListener listener);

	/** Registers a listener for key events.
	 * @see java.awt.Component#addKeyListener(java.awt.event.KeyListener) */
	public void addKeyListener(final KeyListener listener);

	/** Unregisters a previously added key listener.
	 * @see java.awt.Component#removeKeyListener(java.awt.event.KeyListener) */
	public void removeKeyListener(final KeyListener listener);

}
