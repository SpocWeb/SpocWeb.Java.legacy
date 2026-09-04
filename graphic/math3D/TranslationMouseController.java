/*
 * File Name: TranslationMouseController.java
 * Created on: 13.12.2003
 *
 */
package graphic.math3D;

import graphic.mvc.BaseMouseController;
import graphic.mvc.IPainter;

/**
 * Title: TranslationMouseController<p>
 * Description:
 * Moves the given 2D or 3D Vector proportional to the Mouse Drag Movement.  
 *
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
public class TranslationMouseController 
extends BaseMouseController {
	
	/** The Delta to move by 
	 * for Rotation with rad, 0.01 is appropriate! 
	 */
	private double delta = 0.01;

	/** The Point to translate 	 */
	private float[] point;

	/** The limits of Translation 
	 * Negative Values lead to cyclic Rotation between [limit,-limit] 
	 * Positive Values lead to clipping, i.e. no further Movement. 
	 */
	private float[] limits;

	/** the Painter to call when something has changed */
	final IPainter painter;

	/** The actual Dimensions to move 	 */
	final public int[] dimensions = { 0, 1, 2}; 

	/**
	 * Constructor
	 * @param point_ the Point to move, typically shared by Reference 
	 */
	public TranslationMouseController(final float[] point_, final IPainter painter_) {
		this(point_, null, painter_);
	}

	/**
	 * Constructor
	 * @param point_ the Point to move, typically shared by Reference 
	 */
	public TranslationMouseController(final float[] point_, final float[] limits_, final IPainter painter_) {
		this.painter = painter_;
		this.limits = limits_;
		this.point = point_;
		if (point.length < 2) {
			throw new ArrayIndexOutOfBoundsException(" The Point must have at least two Dimensions!");
		}
	}

	/** called on the Mouse drag Event 
	 * 
	 * @param x Drag Destination Coordinate 
	 * @param y Drag Destination Coordinate 
	 * @return true when the Position actually changed! 
	 */
	protected Object dragMouse(final int x, final int y, final int dx, final int dy) {
		if (dy == 0) {
			if (dx == 0) {
				return null; 
			}
		} else {
			final int dim = dimensions[1]; 
			point[dim] += delta * dy;
			if (limits != null) {
				if (limits[dim] > 0) { //positive, limit
					if(Math.abs(point[dim]) > limits[dim]) {
						point[dim] -= delta * dy;
					}
				} else { //negative, rotate
					if(Math.abs(point[dim]) > -limits[dim]) {
						point[dim] += limits[dim]+limits[dim];
					}
				}
			}
		}
		if (dx != 0) {
			point[dimensions[0]] += delta * dx;
		}
		painter.draw(null); //trigger a Repaint... 
		return point; 
	}

}
