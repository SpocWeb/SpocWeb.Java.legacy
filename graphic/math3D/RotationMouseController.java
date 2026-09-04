/*
 * File Name: RotationMouseController.java
 * Created on: 13.12.2003
 *
 */
package graphic.math3D;

import graphic.mvc.BaseMouseController;
import graphic.mvc.IPainter;
import math.matrix.MatrixFloat;

/**
 * Title: RotationMouseController<p>
 * Description:
 * Rotates the given Vector proportional to the Mouse Drag Movement.  
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
public class RotationMouseController 
extends BaseMouseController {
	
	/** The Delta to move by */
	private final double delta = 0.01;

	/** The Point to rotate 	 */
	private final float[] point;
	
	/** the Painter to call when something has changed */
	private final IPainter painter;

	/**
	 * Constructor
	 * @param point_ the Point to move, typically shared by Reference 
	 */
	public RotationMouseController(final float[] point_, final IPainter painter_) {
		this.painter = painter_;
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
			double turnAngle = -delta * dy;
//			P.rotateView(turnAngle);	//rather than recalculating the whole Matrix!
			MatrixFloat.ROTATE_AT(point, turnAngle, 1, 2);
		}
		if (dx != 0) {
			double turnAngle = -delta * dx;
//			P.rotateView(turnAngle);	//rather than recalculating the whole Matrix!
			MatrixFloat.ROTATE_AT(point, turnAngle, 0, 1);
		}
		painter.draw(null); 
		return point; 
	}

}
