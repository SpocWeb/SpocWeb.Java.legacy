/*
 * File Name: AGraphTurtle.java
 * Created on: 16.09.2003
 *
 */
package graphic;


/**
 * Extends {@link AGraph2DOut} with turtle-graphics style movement:
 * relative moves, turns and optional line trailing driven by a current
 * direction and pen position.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see AGraph2DOut
 * @see ITurtle
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:55Z
 * digest: 7455e2b2f89860219c0028706ddaf7d454e549a74ac8461fcd0fca39499d4d87
 * stale: false
 * tags: [code/geometry]
 * concepts: [Turtle Graphics Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
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
	 * Returns the turtle's current pen position.
	 *
	 * @return the current Position of the Turtle
	 */
	public Point2D getPosition() {
		return P;
	}

}
