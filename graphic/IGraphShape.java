/*
 * File Name: IGraphShape.java
 * Created on: 14.09.2003
 *
 */
package graphic;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Title: IGraphShape<p>
 * Description:
 * Defines the Interface for painting complex Shapes (Polygons etc.)
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
public interface IGraphShape 
extends IGraph2DOut, ITurtle, IGraphAddtl {

	/** Flushes any asynchronously drawn Graphics 	 */
	//public void flush();

	/** @see java.awt.Graphics#translate(int, int)	 */
	//public abstract void translate(int x, int y);

	/** Draws a filled Polygon in the current or given Colors
	 * This is a comprehensive Interface for drawing Pixels, 
	 * Lines and Triangles; larger Polygons must not have Colors,
	 * otherwise they should be split up into Triangles, 
	 * because the Algorithm for Triangularization is not implemented! 
	 * For 1 and 2 Points identical to setPixel and PolyLine 
	 * 
	 * The first Array Dimension consists of the individual Points. 
	 * The second Dimension holds the x and y Positions. 
	 * Colors are optional and can be given in the 3rd Component. 
	 */
	//public abstract void fillPolygon(int[][] PointsColors, int nPoints);

	/////////////////////////////////////////////////////////////////////////////////////
	/// #range: Clipping
	/////////////////////////////////////////////////////////////////////////////////////

	/** @see java.awt.Graphics#getClipBounds(java.awt.Rectangle)	 */
	public abstract Rectangle getClipBounds(Rectangle r);

	/** @see java.awt.Graphics#getClipRect()	 */
	public abstract Rectangle getClipRect();

	/** @see java.awt.Graphics#getClipBounds()	 */
	public abstract Rectangle getClipBounds();

	/** @see java.awt.Graphics#clipRect(int, int, int, int)	 */
	public abstract void clipRect(int x, int y, int width, int height);

	/** @see java.awt.Graphics#setClip(int, int, int, int)	 */
	public abstract void setClip(int x, int y, int width, int height);

	/** @see java.awt.Graphics#getClip()	 */
	public abstract Shape getClip();

	/** @see java.awt.Graphics#setClip(java.awt.Shape)	 */
	public abstract void setClip(Shape clip);

	/** @see java.awt.Graphics#hitClip(int, int, int, int)	 */
	public abstract boolean hitClip(int x, int y, int width, int height);

	/**Sets a Pixel in the current Color at the current Position,
	 * but only if it is within the current clipping area	 */
	public abstract void setClipPixel();

	/////////////////////////////////////////////////////////////////////////////////////
	/// #range: Shapes, defined by the jawa.awt.Graphics Class
	/////////////////////////////////////////////////////////////////////////////////////

	/** @see java.awt.Graphics#drawLine(int, int, int, int)	 */
	public abstract void drawLine(int x1, int y1, int x2, int y2);

	/**Optimization: Sides are parallel to the Graphics Raster 
	 * @see java.awt.Graphics#fillRect(int, int, int, int)	 */
	public abstract void fillRect(int x, int y, int width, int height);

	/** @see java.awt.Graphics#clearRect(int, int, int, int)	 */
	public abstract void clearRect(int x, int y, int width, int height);

	/** @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)	 */
	public abstract void drawRoundRect(
		int x,
		int y,
		int width,
		int height,
		int arcWidth,
		int arcHeight);

	/** @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)	 */
	public abstract void fillRoundRect(
		int x,
		int y,
		int width,
		int height,
		int arcWidth,
		int arcHeight);

	/** @see java.awt.Graphics#drawOval(int, int, int, int)	 */
	public abstract void drawOval(int x, int y, int width, int height);

	/** @see java.awt.Graphics#fillOval(int, int, int, int)	 */
	public abstract void fillOval(int x, int y, int width, int height);

	/** @see java.awt.Graphics#drawArc(int, int, int, int, int, int)	 */
	public abstract void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	/** @see java.awt.Graphics#fillArc(int, int, int, int, int, int)	 */
	public abstract void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	/** 
	 * Draws an open Polyline
	 * For a single Point identical to setPixel 
	 * @see java.awt.Graphics#drawPolyline(int[], int[], int)	 
	 */
	public abstract void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

	/**Draws a closed Polygon 
	 * @see java.awt.Graphics#drawPolygon(int[], int[], int)
	 */
	public abstract void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);

	/** Draws a filled Polygon in the current Color
	 * For 1 and 2 Points identical to setPixel and PolyLine 
	 * @see java.awt.Graphics#fillPolygon(int[], int[], int)	 
	 */
	public abstract void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

	/** @see java.awt.Graphics#draw3DRect(int, int, int, int, boolean)	 */
	public abstract void draw3DRect(int x, int y, int width, int height, boolean raised);

	/** @see java.awt.Graphics#drawPolygon(java.awt.Polygon)	 */
	public abstract void drawPolygon(Polygon p);

	/** @see java.awt.Graphics#drawRect(int, int, int, int)	 */
	public abstract void drawRect(int x, int y, int width, int height);

	/** @see java.awt.Graphics#fill3DRect(int, int, int, int, boolean)	 */
	public abstract void fill3DRect(int x, int y, int width, int height, boolean raised);

	/** @see java.awt.Graphics#fillPolygon(java.awt.Polygon)	 */
	public abstract void fillPolygon(Polygon p);

	/////////////////////////////////////////////////////////////////////////////////////

	/**Fills a whole horizontal Row with the interpolated Value between z0 and z1.
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void drawHLine (final short[] z0, final short[] z1, final IPalette palette); 
	
	/** Fills a Triangle with the Line x1-x2 parallel to the y-Axis
	 * thus x1 and x2 must have the same y Value
	 * @param x0 Tip Corner
	 * @param x1 Base1 Corner
	 * @param x2 Base2 Corner
	 */
	public void fillHTriangle(final short[] x0, final short[] x1, final short[] x2, final IPalette palette);

	/**Draws the given Polygon, no matter if it is...
	 * empty
	 * a single Point 
	 * a filled Line 
	 * a filled Triangle 
	 * a higher Polygon, which is drawn as a Series of Triangles
	 * originating from the last Point, which should be a Star Point. 
	 * Actually this last Point can be added artificially 
	 * by enlarging the Polygon Line with an annihilating Double-Line
	 * to and from this Star Point!
	 * 
	 * Fills the whole Polygon row-wise with the current Color. 
	 * Row-wise Filling may be advantageous due to the physical Graphichs Memory Architecture. 
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. 
	 * all other Elements are interpolated, since they can contain: 
	 * z-Values for Depth Painting 
	 * z Values for Scalar Interpolation
	 * Color Codes (either Palette or r,g,b Values )
	 * u,v Values for Texture Mapping
	 * Cosinusses or Normals for Gouraud or Phong Shading
	 * etc.
	 */
	public void fillPolygon(final short[][]xy, final IPalette palette);

	/**Fills a whole Triangle ((xy0[0], xy0[1]), (xy1[0], xy1[1]), (xy2[0],xy2[1]))
	 * row-wise with the current Color. 
	 * Row-wise Filling may be advantageous due to the physical Graphichs Memory Architecture. 
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. 
	 * all other Elements are interpolated, since they can contain: 
	 * z-Values for Depth Painting 
	 * z Values for Scalar Interpolation
	 * Color Codes (either Palette or r,g,b Values )
	 * u,v Values for Texture Mapping
	 * Cosinusses or Normals for Gouraud or Phong Shading
	 * etc.
	 */
	public void fillTriangle (final short[] xy0, final short[] xy1, final short[] xy2, final IPalette palette);

	/**Draws the Line between any two p0 and p1 
	 * with the interpolated Values.	 */
	public void drawLine (final short[] p0, final short[] p1, final IPalette palette); 
	
	/**Fills a whole horizontal Row with the interpolated Value between z0 and z1.
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void drawHLine (final short[] x0, final short x1, final IPalette palette); 
	
	/**Sets a Pixel with the given Coordinates and y Value 
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'. 
	 * The Interpretation of the different Coordinates depends on the Context. 
	 *
	 * Design Decisions:
	 * The y Component is tracked separately Instead of in x0. 
	 * Also the Changes in the x-Coordinate are collected until one of the Components changes.
	 */
	public void setPixel (final short[]x0, final IPalette palette);
	
}
