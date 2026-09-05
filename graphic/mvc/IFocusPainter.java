/*
 * File Name: IFocusPainter.java
 * Created on: 12.12.2003
 *
 */
package graphic.mvc;

/**
 * A {@link IPainter} that tracks which of several drawn points currently has input focus,
 * and can locate the point nearest a given screen position.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:52:13Z
 * digest: 189ee3dbd4fc5cca98a47170a4a40b14408d1152ff02adf6d1003a2d3e73f258
 * stale: false
 * tags: [code/keyboard_focus_tracking, code/gui]
 * concepts: [Focus-Aware Painter Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IFocusPainter 
extends IPainter {

	/** Returns which point index currently has focus.
	 * @return the Index having the current Focus
	 */
	int getFocusIndex();

	/** Sets which point index currently has focus.
	 * @param index the Index to set the Focus to
	 */
	void setFocusIndex(final int index);

	/** Finds the point nearest the given position without changing the current focus.
	 * @param x
	 * @param y
	 * @return the Index nearest to the given Position
	 */
	int getFocusIndex(final int x, final int y);

	/** Moves focus to the point nearest the given position.
	 * @param x
	 * @param y
	 * @return the Index near the given Position
	 */
	public int setFocusIndex(final int x, final int y);

	/** Returns the radius used to draw and hit-test the focus point.
	 * @return the Radius of the Focus
	 */
	public char getPointRadius();
	
}
