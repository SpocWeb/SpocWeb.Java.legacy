/*
 * File Name: AGraph2DOut.java
 * Created on: 14.09.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Title: AGraph2DOut<p>
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
public abstract class AGraph2DOut 
implements IGraph2DOut {

	//	Local Pen Variables

	/**Current Position of this Pen	 */
	public Point2D P = new Point2D();

	/** Default Value for setting the Color using an int Variable */
	public boolean hasAlphaByDefault = true;

	/** Reference to the current Color */
	protected Color col;

	/**
	 * @see graphic.IGraph2DOut#setColor(java.awt.Color)
	 */
	public void setColor(final Color color) {
		if ((col != color) && //Optimization
			!col.equals(color)) {
			col = color;  
		}
	}

	/**
	 * @see graphic.IGraph2DOut#getColor()
	 */
	public Color getColor() { return col; }

	public void setColor(final int color) { setColor(new Color(color)); }

	public void setColor(final int red, final int green, final int blue) {
		setColor(new Color(red, green, blue)); }

	/**
	 * Sets a Pixel at the given Position (x, y) in the given color. 
	 * SetPixel Routine for the Hidden Line Algorithm.
	 * Conditionally sets the Pixel and updates the Buffer.
	 */
	public void setPixel(final int x, final int y, final int colorOrZ) {
		setPixel(x, y, colorOrZ, hasAlphaByDefault); }

	/**
	 * Sets a Pixel at the given Position (x, y) in the given color. 
	 * SetPixel Routine for the Hidden Line Algorithm.
	 * Conditionally sets the Pixel and updates the Buffer.
	 */
	public void setPixel(final int x, final int y, final int colorOrZ, final boolean hasAlpha_) {
		setPixel(x, y, new Color(colorOrZ, hasAlpha_)); }

	/**
	 * Sets a Pixel at the given Position (x, y) in the current color.  
	 * SetPixel Routine for the Hidden Line Algorithm.
	 * Conditionally sets the Pixel and updates the Buffer.
	 */
	public void setPixel(final int x, final int y, final Color color_) {
		P.setLocation(x, y); 
		setPixel(color_); 
	}

	/**Sets a Pixel in the current Color	 */
	public void setPixel(final int x, final int y) {
		P.setLocation(x, y); 
		setPixel (col); 
	}

	/**Sets a Pixel in the current Color	 
	 * @see graphic.IGraph2DOut#setPixel(graphic.Point2D)
	 */
	public void setPixel(final Point2D Pt) {
		P.setLocation(Pt); 
		setPixel(col); 
	}

	/**
	 * Changes the Color and sets a Pixel at the given Position	 
	 * @see graphic.IGraph2DOut#setPixel(graphic.Point2D, java.awt.Color)
	 */
	public void setPixel(final Point2D Pt, final Color color_) {
		P.copyAt(Pt); 
		setPixel(color_); 
	}

	/**Sets a Pixel in the current Color at the current Position	 */
	public void setPixel() { setPixel (col); }


	/////////////////////////////////////////////////////////////////////////////////////
	///#region: abstract Methods 
	/////////////////////////////////////////////////////////////////////////////////////
	

	/**Changes the Color and sets a Pixel at the current Position	 */
	//public abstract void setPixel(final Color color); //{ setClipPixel (color); }

}
