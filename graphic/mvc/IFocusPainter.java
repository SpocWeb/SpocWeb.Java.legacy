/*
 * File Name: IFocusPainter.java
 * Created on: 12.12.2003
 *
 */
package graphic.mvc;

/**
 * Title: IFocusPainter<p>
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
public interface IFocusPainter 
extends IPainter {

	/** 
	 * @return the Index having the current Focus
	 */
	int getFocusIndex();

	/**
	 * @param index the Index to set the Focus to
	 */
	void setFocusIndex(final int index);

	/** 
	 * @param x
	 * @param y
	 * @return the Index nearest to the given Position
	 */
	int getFocusIndex(final int x, final int y);

	/** 
	 * @param x
	 * @param y
	 * @return the Index near the given Position
	 */
	public int setFocusIndex(final int x, final int y);

	/**
	 * @return the Radius of the Focus
	 */
	public char getPointRadius();
	
}
