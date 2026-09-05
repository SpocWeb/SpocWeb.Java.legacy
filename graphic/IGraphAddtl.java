/*
 * File Name: IGraphAddtl.java
 * Created on: 14.09.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Defines additional graphic primitives not in {@link java.awt.Graphics},
 * for optimized speed: direct line/rectangle/triangle/polygon drawing built
 * on {@code setPixel()} instead of general path filling.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:02:02Z
 * digest: 7510e05fc5f58a85706ce66b470bd447dde1f28bd318ae228f76d59f705b39de
 * stale: false
 * tags: [code/graphics, code/line_rasterization]
 * concepts: [Additional Fast Primitives Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IGraphAddtl {

	////////////////////////////////////////////////////////////////////////////////
	///additional Methods

	/**Draws a horizontal Line from the current x Position to the given x1 Position using setPixel(),
	 * considers ActBrushPattern, but does not clip anymore!
	 */
	public abstract void drawHLine(int x1);

	/**Draws a horizontal Line using drawClipHLine() */
	public abstract void drawHLine(int x0, int x1, int y_);

	/**Draws a horizontal Line using drawClipHLine() */
	public abstract void drawHLine(Point2D P_, int x1);

	/**Draws a vertical Line using setPixel(), also considers Clipping */
	public abstract void drawVLine(int y1);

	/**Draws a horizontal Line using drawVLine() */
	public abstract void drawVLine(int x_, int y0, int y1);

	/**Draws a horizontal Line using drawVLine() */
	public abstract void drawVLine(Point2D P_, int y1);

	/**Draws a Line from L.getStart() to L.getStop() using setPixel()	 */
	public abstract void drawLine(Line2D L);

	/**Draws a Line from P1 to P2 using setPixel()	 */
	public abstract void drawLine(Point2D P0, Point2D P1);

	/**Draws a Line using setPixel()	 */
	public abstract void drawLine(int x1, int y1);

	/**Draws a filled fillRect with L as the Diagonal.	 */
	public abstract void fillRect(Line2D L);

	/**Draws a filled fillRect from the current Position to (x1, y1).	 */
	public abstract void fillRect(Point2D P0, Point2D P1);

	/**Draws a filled fillRect from the current Position to (x1, y1).	 */
	public abstract void fillRect(Point2D P1);

	/**Draws a filled fillRect from the current Position (P.x, P.y) to (x1, y1).	 */
	public abstract void fillRect(int x1, int y1);

	/**Draws a Rectangle with L as the Diagonal.	 */
	public abstract void drawRect(Line2D L);

	/**Draws a Rectangle from the current Position to (x1, y1).	 */
	public abstract void drawRect(int x1, int y1);

	/**Draws a Rectangle from (x0, y0) to (x1, y1).	 */
	public abstract void drawRect(Point2D P0, Point2D P1);

	/**Draws a Rectangle from the current Position to (x1, y1).	 */
	public abstract void drawRect(Point2D P1);

	/**Draws a 3-D highlighted outline of the specified rectangle.
	 * The edges of the rectangle are highlighted so that they
	 * appear to be beveled and lit from the upper left corner.
	 * <p>
	 * The colors used for the highlighting effect are determined
	 * based on the current color.
	 * The resulting rectangle covers an area that is
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1<code> pixels tall.
	 * @param x the <i>x</i> coordinate of the rectangle to be drawn.
	 * @param y the <i>y</i> coordinate of the rectangle to be drawn.
	 * @param width the width of the rectangle to be drawn.
	 * @param height the height of the rectangle to be drawn.
	 * @param raised a boolean that determines whether the rectangle
	 *            appears to be raised above the surface
	 *            or sunk into the surface.
	 * @see   java.awt.Graphics#fill3DRect
	 * @since JDK1.0
	 */
	public abstract void draw3DRect(Point2D P0, Point2D P1, boolean raised);

	/**Paints a 3-D highlighted rectangle filled with the current color.
	 * The edges of the rectangle will be highlighted so that it appears
	 * as if the edges were beveled and lit from the upper left corner.
	 * The colors used for the highlighting effect will be determined from
	 * the current color.
	 * @param x the <i>x</i> coordinate of the rectangle to be filled.
	 * @param y the <i>y</i> coordinate of the rectangle to be filled.
	 * @param width the width of the rectangle to be filled.
	 * @param height the height of the rectangle to be filled.
	 * @param raised a boolean value that determines whether the
	 *           rectangle appears to be raised above the surface
	 *           or etched into the surface.
	 * @see   java.awt.Graphics#draw3DRect
	 * @since JDK1.0
	 */
	public abstract void fill3DRect(Point2D P0, Point2D P1, boolean raised);

	/**Paints a 3-D highlighted rectangle optionally filled with the current color.
	 * The edges of the rectangle will be highlighted so that it appears
	 * as if the edges were beveled and lit from the upper left corner.
	 * The colors used for the highlighting effect will be determined from
	 * the current color.
	 * @param x the <i>x</i> coordinate of the rectangle to be filled.
	 * @param y the <i>y</i> coordinate of the rectangle to be filled.
	 * @param width the width of the rectangle to be filled.
	 * @param height the height of the rectangle to be filled.
	 * @param raised a boolean value that determines whether the
	 *        rectangle appears to be raised above the surface
	 *        or etched into the surface.
	 * @see   java.awt.Graphics#draw3DRect
	 * @since JDK1.0
	 */
	public abstract void Rect3D(
		int x,
		int y,
		int width,
		int height,
		boolean raised,
		boolean filled);

	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public abstract void drawPolygon(final int[] xP, final int[] yP, final boolean closed);

	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public abstract void drawPolygon(final int[] xP, final int[] yP, final boolean closed, int n);

	/**Draws the Polygon and adds a closing Line2D, if wanted
	 * The Algorithm with null as Separator can be extended
	 * to the drawLine Routine, which would see null as an indicator
	 * not to connect to the last Point, but only set the Location!	 */
	public abstract void drawPolygon(Point2D[] Points, boolean closed);

	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public abstract void drawPolygons(Point2D[][] Polygons, boolean closed);

	/**Draws those half of the Polygon Lines where the Indices are rising
	 * and adds a closing Line. More efficient for closed Bodies	 */
	public abstract void drawPolygon(Point2D[] Points, int[] Polygon, boolean closed);

	/**Draws those half of the Polygons' Lines where the Indices are rising
	 * and adds a closing Line. More efficient for closed Bodies	 */
	public abstract void drawPolygons(Point2D[] Points, int[][] Polygons, boolean closed);

	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public abstract void drawPolygon(int[][] Pol, boolean closed);

	/**Draws a whole Triangle (P0, P1, P2) using the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void drawTriangle(Point2D P1, Point2D P2);

	/**Draws a whole Triangle (P0, P1, P2) using the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void drawTriangle(Point2D P0, Point2D P1, Point2D P2);

	/**Draws a whole Triangle ((x0, y0), (x1, y1), (x2,y2)) using the current Color.  */
	public abstract void drawTriangle(int x1, int x2, int y1, int y2);

	/**Draws a whole Triangle ((x0, y0), (x1, y1), (x2,y2)) using the current Color.  */
	public abstract void drawTriangle(int x0, int x1, int x2, int y0, int y1, int y2);

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void fillPolygon(Point2D P1, Point2D P2);

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void fillTriangle(Point2D P0, Point2D P1, Point2D P2);

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void fillTriangle(int x1, int x2, int y1, int y2);

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public abstract void fillTriangle(int x0, int x1, int x2, int y0, int y1, int y2);

	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public abstract void fillPolygon(int[] xP, int[] yP, Color BorderColor, Color InnerColor);

	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public abstract void fillPolygon(int[] xP, int[] yP, Color BorderColor);

	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public abstract void fillPolygon(final int[] xP, final int[] yP);

	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public abstract void fillPolygon(Point2D[] P0, Color BorderColor, Color InnerColor);

	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public abstract void fillPolygon(Point2D[] P0, Color BorderColor);

	/**Fills the Polygon with the current color	 */
	public abstract void fillPolygon(Point2D[] P0);

	/** 
	 * Draws an Ellipse which fits into the Rectangle defined by L
	 * @param L the Rectangle to fit an Ellipse into.
	 */
	public abstract void drawEllipse(Line2D L);

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public abstract void drawEllipse(int r);

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public abstract void drawEllipse(Point2D M, int r);

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public abstract void drawEllipse(Point2D M, Point2D R);

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public abstract void drawEllipse(Point2D R);

	/**Draws a regular Polygon with n Corners
	 * between the Start and End Angles in W.
	 */
	public abstract void drawRegPoly(int n, Point2D R, Point2D W);

	/**Draws an Arc with Radiuses R and the Start and End Angles in W	 */
	public abstract void drawArc(Point2D R, Point2D W);

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public abstract void drawArc(Point2D M, Point2D R, Point2D W);

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	public abstract void drawSector(Point2D M, Point2D R, Point2D W);

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public abstract void drawRoundRect(Line2D L, int r);

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public abstract void drawRoundRect(Line2D Li, Point2D R);

	/** Draws a Rectangle with rounded borders with Radiuses R.	 */
	//public abstract void fillRoundRect(Point2D P1, Point2D P2, Point2D R);

	/** Draws a Rectangle with rounded borders with Radiuses R.	 */
	//public abstract void drawRoundRect(Point2D P1, Point2D P2, Point2D R);

	/** 
	 * Fills an Ellipse which fits into the Rectangle defined by L
	 * @param L the Rectangle to fit an Ellipse into.
	 */
	public abstract void fillEllipse(Line2D L);

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public abstract void fillEllipse(int r);

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public abstract void fillEllipse(Point2D M, int r);

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public abstract void fillEllipse(Point2D M, Point2D R);

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public abstract void fillEllipse(Point2D R);

	/**Draws a regular Polygon with n Corners
	 * between the Start and End Angles in W.
	 */
	public abstract void fillRegPoly(int n, Point2D R, Point2D W);

	/**Draws an Arc at the current Point with Radiuses R 
	 * and the Start and End Angles in W	 
	 */
	public abstract void fillArc(Point2D R, Point2D W);

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public abstract void fillArc(Point2D M, Point2D R, Point2D W);

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	public abstract void fillSector(Point2D M, Point2D R, Point2D W);

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public abstract void fillRoundRect(Line2D L, int r);

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public abstract void fillRoundRect(Line2D Li, Point2D R);
	
	/////////////////////////////////////////////////////////////////////////////////

	/** Sets the Color based on a Palette
	 * If the Palette is null, standard Conversion to RGB is used 
	 */
	public void setColor(final int paletteIndex);

}