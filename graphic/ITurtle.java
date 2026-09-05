/*
 * File Name: ITurtle.java
 * Created on: 14.09.2003
 *
 */
package graphic;

/**
 * Defines the contract for a stateful colored turtle holding a pen position
 * and orientation.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:44Z
 * digest: b7414c76e8ed3e5fb465fb76695cdcddc4b77f26c017de5467c9b800b0b4ca2d
 * stale: false
 * tags: [code/geometry]
 * concepts: [Turtle Graphics Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface ITurtle {

	/**Moves the Turtle the given number of Steps
	 * in the current Direction	 */
	public abstract void forwd(int Steps);

	/** turns the Turtle by the given Angle into the given Direction 
	 * 
	 * @param dAngle the Angle to turn by 
	 * @return the resulting Direction 
	 */
	public abstract int turn(int dAngle);

	/**
	 * Returns the turtle's current pen position.
	 *
	 * @return the current Position of the Turtle
	 */
	public Point2D getPosition();

	/** moves the Turtle to the given Location without drawing */
	public abstract void moveTo(int x, int y);

	/** moves the Turtle to the given Location without drawing */
	public abstract void moveTo(Point2D Pt);

	/**Draws a Line to the Point P1 using setPixel()
	 * Also takes 'null' as an Indicator for breaking the Line
	 * between the previous and the next Point.	 */
	public abstract void drawLine(Point2D P1);

}