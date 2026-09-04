/*
 * File Name: ITurtle.java
 * Created on: 14.09.2003
 *
 */
package graphic;

/**
 * Title: ITurtle<p>
 * Description:
 * Purpose:
 * Defines the Interface for a stateful colored Turtle 
 * holding a Pen Position and Orientation. 
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

	/** @return the current Position of the Turtle	 */
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