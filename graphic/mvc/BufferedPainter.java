/*
 * File Name: BufferedPainter.java
 * Created on: 06.01.2004
 *
 */
package graphic.mvc;

import graphic.IGraphImage;
import graphic.IGraphText;
import graphic.JavaGraphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

/**
 * Title: BufferedPainter<p>
 * Description:
 * Painter Object, receives or generates a Viewer Window 
 * to output the Result of it's Instructions.  
 * 
 * This Class buffers the Result of Graphics Operations as the Model.  
 * This allows to paint in the Background and prevents Flickering. 
 * It is initialized with the maximum Size and keeps it. 
 * Painting to the Screen may output only parts of the full Size. 
 *
 * Design Decisions / Implementation Details:
 * Uses java.awt.image.BufferedImage as the Cache, 
 * instead of graphic.MemoryImage, 
 * because that supports drawing Images. 
 * 
 * Unfortunately this BufferedPainter cannot easily be plugged 
 * between IPainter and ICanvas, 
 * because it doesn't know when drawing has finished! 
 * Use repaint() for flushing the Graphics! 
 * 
 * Additionally Buffering does NOT eliminate Flickering completely! 
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
public class BufferedPainter 
implements IPainter, IActiveCanvas {
	
	/** the Memory Buffer for cacheing and painting 	 */
	final public BufferedImage image; 

	/** the Canvas to draw to, since active Painting does not redraw the Picture 	*/
	final public ICanvas canvas; 

	/** 
	 * initializing Constructor
	 * @param painter_
	 */
	public BufferedPainter(final ICanvas canvas_) {//
		this.canvas = canvas_;
		final Dimension dim = canvas.getSize();
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas)canvas).addPainter(this);
		}
		this.image = new BufferedImage(dim.height, dim.width, BufferedImage.TYPE_INT_RGB); 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Interface ICanvas
	/////////////////////////////////////////////////////////////////////////////////////

	/** switches clearing the Canvas when fetching the Graphics Context 	 */
	public boolean clearOnDraw = true; 

	/** @see graphic.mvc.ICanvas#getIGraphImage()
	 */
	public IGraphImage getIGraphImage() {
		final Graphics graphics = image.getGraphics(); 
		graphics.setClip(0, 0, image.getWidth(), image.getHeight()); //make sure it has Clipping! 
		if (clearOnDraw) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, image.getWidth(), image.getHeight());} 
		final JavaGraphic ret = new JavaGraphic(graphics); //
		return ret; 
	}

	/** @see graphic.mvc.ICanvas#getSize()	 */
	public Dimension getSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	/**Actively update the GUI 
	 * @see graphic.mvc.IRepainter#repaint()
	 */
	public void repaint() {
		draw(null);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Interface IPainter
	/////////////////////////////////////////////////////////////////////////////////////

	/**Draws the buffered Image e.g. in Response to a repaint() Event  
	 * @see graphic.mvc.IPainter#draw(graphic.IGraphText)	 
	 */
	public void draw(final IGraphText gText) {
		if (gText == null) {
			canvas.repaint(); return;
		}
		((IGraphImage) gText).drawImage(image,0,0,null); 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Interface IActiveCanvas
	/////////////////////////////////////////////////////////////////////////////////////

	/** @see graphic.mvc.IController#addMouseListener(java.awt.event.MouseListener)	 */
	public void addMouseListener(MouseListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).addMouseListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#removeMouseListener(java.awt.event.MouseListener)	 */
	public void removeMouseListener(MouseListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).removeMouseListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#addMouseMotionListener(java.awt.event.MouseMotionListener)	 */
	public void addMouseMotionListener(MouseMotionListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).addMouseMotionListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#removeMouseMotionListener(java.awt.event.MouseMotionListener)	 */
	public void removeMouseMotionListener(MouseMotionListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).removeMouseMotionListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#addMouseWheelListener(java.awt.event.MouseWheelListener)	 */
	public void addMouseWheelListener(MouseWheelListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).addMouseWheelListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#removeMouseWheelListener(java.awt.event.MouseWheelListener)	 */
	public void removeMouseWheelListener(MouseWheelListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).removeMouseWheelListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#addKeyListener(java.awt.event.KeyListener)	 */
	public void addKeyListener(KeyListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).addKeyListener(listener); 
		}
	}

	/** @see graphic.mvc.IController#removeKeyListener(java.awt.event.KeyListener)	 */
	public void removeKeyListener(KeyListener listener) {
		if (canvas instanceof IActiveCanvas) {
			((IActiveCanvas) canvas).removeKeyListener(listener); 
		}
	}

	/** @see graphic.mvc.IPaintEventSource#addPainter(graphic.mvc.IPainter)	 */
	public boolean addPainter(IPainter painter_) {
/*		if (canvas instanceof IActiveCanvas) {
			return ((IActiveCanvas) canvas).addPainter(painter); 
		}
*/		return false;
	}

	/** @see graphic.mvc.IPaintEventSource#removePainter(graphic.mvc.IPainter)	 */
	public boolean removePainter(IPainter painter) {
/*		if (canvas instanceof IActiveCanvas) {
			return ((IActiveCanvas) canvas).removePainter(painter); 
		}
*/		return false;
	}

}
