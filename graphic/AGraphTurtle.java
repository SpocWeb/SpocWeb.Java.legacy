/*
 * File Name: AGraphTurtle.java
 * Created on: 16.09.2003
 *
 */
package graphic;


/**
 * Title: AGraphTurtle<p>
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
public abstract class AGraphTurtle 
extends AGraph2DOut 
implements ITurtle {

	////////////////////////////////////////////////////////////////////////////
	/// Turtle Graphics
	////////////////////////////////////////////////////////////////////////////
	
	/**Current Direction of the Position, for Turtle Graphics.
	 * Made public, because it is not necessary to monitor the Range from 0 to 360	 */
	public int Direction;

	/** switches the trailing on or off */
	public boolean trail;

	/**Moves the Turtle the given number of Steps
	 * in the current Direction	 */
	public void forwd (int Steps) {
		Point2D dir = PolyTrigon.EllipseRadius(Direction, new Point2D(Steps, Steps));
		dir.x += P.x;
		dir.y += P.y;
		if (trail) { this.drawLine(dir);
		} else { P = dir; }
	}

	/** turns the Turtle by the given Angle into the given Direction 
	 * 
	 * @param dAngle the Angle to turn by 
	 * @return the resulting Direction 
	 */
	public int turn(int dAngle) {
		return Direction += dAngle; }

	/** moves the Turtle to the given Location without drawing */
	public void moveTo(int x, int y) {
		P.setLocation(x, y) ; }

	/** moves the Turtle to the given Location without drawing */
	public void moveTo(Point2D Pt) {
		P.setLocation(Pt) ; }

	/**
	 * @return the current Position of the Turtle
	 */
	public Point2D getPosition() {
		return P;
	}

}
