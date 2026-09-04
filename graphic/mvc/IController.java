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
 * Title: IController<p>
 * Description:
 * Purpose:
 * Interface for the low Level Controller Event Sources
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
 */
public interface IController 
extends IPaintEventSource 
{

	// Hooks for registering the Controllers

	/** @see java.awt.Component#addMouseListener(java.awt.event.MouseListener) */	
	public void addMouseListener(final MouseListener listener);
	
	/** @see java.awt.Component#removeMouseListener(java.awt.event.MouseListener) */	
	public void removeMouseListener(final MouseListener listener);
	
	/** @see java.awt.Component#removeMouseMotionListener(java.awt.event.MouseMotionListener) */	
	public void addMouseMotionListener(final MouseMotionListener listener);

	/** @see java.awt.Component#removeMouseMotionListener(java.awt.event.MouseMotionListener) */	
	public void removeMouseMotionListener(final MouseMotionListener listener);

	/** @see java.awt.Component#addMouseWheelListener(java.awt.event.MouseWheelListener) */	
	public void addMouseWheelListener(final MouseWheelListener listener);

	/** @see java.awt.Component#removeMouseWheelListener(java.awt.event.MouseWheelListener) */	
	public void removeMouseWheelListener(final MouseWheelListener listener);

	/** @see java.awt.Component#addKeyListener(java.awt.event.KeyListener) */	
	public void addKeyListener(final KeyListener listener);

	/** @see java.awt.Component#removeKeyListener(java.awt.event.KeyListener) */	
	public void removeKeyListener(final KeyListener listener);

}
