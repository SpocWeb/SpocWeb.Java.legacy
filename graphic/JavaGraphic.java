package graphic;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;

/**
  * Extends {@link Graph2D} with optimized methods that delegate to AWT's
  * possibly native routines for simple graphical objects like lines,
  * rectangles, ovals and arcs.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-08-2002, 11:04 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see graphic.GraphicsAdapter does the same, but does not carefully
  * overwrite each method of the new interface
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T12:09:12Z
  * digest: 4a5457bb942b01c40253be3c42f2129f8f49d5b5da31627bbdf00ccc85e7425f
  * stale: false
  * tags: [code/graphics]
  * concepts: [AWT-Delegated Rendering]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
  * -->
  */
final public class JavaGraphic 
extends Graph2D { //implements IGraphics {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Draws the source rectangle (sx1, sy1)-(sx2, sy2) of the image scaled
	 * into the destination rectangle (dx1, dy1)-(dx2, dy2), delegating to AWT.
	 *
	 * @param img the Image to draw.
	 * @param x the Top Left Corner of the Image
	 * @param y the Top Left Corner of the Image
	 * @param width the Width of the Image
	 * @param height the Height of the Image
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	/**
	 * Returns the font metrics for the current font.
	 *
	 * @return a FontMetrics Object allowing to determine the String Sizes with a given Font
	 */
	public FontMetrics getFontMetrics(){
		return g.getFontMetrics();
	}

	/**
	 * Returns the font currently used for drawing text.
	 *
	 * @see graphic.IGraphics#getFont()
	 */
	public Font getFont() {
		return g.getFont();
	}

	/**
	 * Sets the font subsequently used for drawing text.
	 *
	 * @see graphic.IGraphics#getFont()
	 */
	public void setFont(Font font) {
		g.setFont(font);
	}
	
	/**Draws a String at the specified Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final String S) {
		g.drawString(S, P.getX(), P.getY()); 
	}

	/**Draws a String at the specified Position	 */
	public void drawString(final String S, final Point2D P) {
		this.P = P;
		drawString(S); 
	}

	/**Draws a String at the specified Position	 */
	public void drawString(final String S, final int x, final int y) {
		this.P.setX(x);
		this.P.setY(y);
		drawString(S); 
	}

	/**Draws a String at the specified Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final StringBuffer S) {
		g.drawString(S.toString(), P.getX(), P.getY()); 
	}

	/**Draws a String at the specified Position	 */
	public void drawString(final StringBuffer S, final Point2D P) {
		drawString(S.toString(), P); 
	}

	/**Draws a String at the specified Position	 */
	public void drawString(final StringBuffer S, final int x, final int y) {
		drawString(S.toString(), x, y); 
	}
	
	/**
	 * Draws a single character at the current position.
	 *
	 * @see graphic.IGraphText#drawChar(char)
	 */
	public void drawChar(final char c) {
		drawString(new String(new char[] { c }));
	}

	/**
	 * Draws a single character at the given position.
	 *
	 * @see graphic.IGraphText#drawChar(char, graphic.Point2D)
	 */
	public void drawChar(final char c, Point2D p) {
		drawString(new String(new char[] { c }), p); }

	/**
	 * Draws the given character range at the given position.
	 *
	 * @see graphic.IGraphText#drawChars(char[], int, int, int, int)
	 */
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		drawString(new String(data, offset, length), x, y); } 
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**initializing Constructor with Default Clipping Area:	 */
	public JavaGraphic(Graphics gr) {
		super(gr);
	}

	/**Constructor that defines a Clipp�ng Area	 */
	public JavaGraphic(Graphics gr, Point2D ClipTL, Point2D ClipBR) {
		super(gr, ClipTL, ClipBR);
	}

	////////////////////////////////////////////////////////////////////////////
	//	Optimizations:
	////////////////////////////////////////////////////////////////////////////

	/**Draws a horizontal Line  */
	public void drawHLine(final int x1) {
		g.drawLine(P.getX(), P.getY(), x1, P.getY());
		P.setX(x1);
	} //Now the Endpoint is the actual Coordinate

	/**Draws a vertical Line  */
	public void drawVLine(int y1) {
		g.drawLine(P.getX(), P.getY(), P.getX(), y1);
		P.setY(y1);
	} //Now the Endpoint is the actual Coordinate

	/**Draws a Line using setPixel()	 */
	public void drawLine(int x1, int y1) {
		if (connect) {
			g.drawLine(P.getX(), P.getY(), x1, y1);
		}
		connect = true;
		P.setX(x1);
		P.setY(y1);
	} //Now the Endpoint is the actual Coordinate

	/**Draws a filled Bar from the current Position to (x1, y1).	 */
	public void fillRect(int x1, int y1) {
		orderedMethod(x1, y1, MethodFillRect);
	}

	/**Draws a Rectangle from the current Position to (x1, y1).	 */
	public void drawRect(int x1, int y1) {
		orderedMethod(x1, y1, MethodDrawRect);
	}

	/**Draws the Polygon and adds a closing Line, if wanted.
	 * Should not be used, it requires a conversion to the java.awt.Polygon Type.	 */
	public void drawPolygon(
		Point2D[] Pol,
		boolean closed) { //Also here the Possibility of a 'null' in the Pol has to be taken care of!
		int Length, Start = 0;
		int[][] Points = new int[2][Pol.length];
		do { //here the Polygon is split up at 'null's into several Polygons!
			Length = convertPolygon(Pol, Start, Points); //
			if (closed) {
				g.drawPolygon(Points[0], Points[1], Length); //
			} else {
				g.drawPolyline(Points[0], Points[1], Length);
			}
		} while ((Start += (Length + 1)) < Pol.length);
	}

	/**Draws the Polygon and adds a closing Line, if wanted	 */
	public void drawPolygon(int[] xP, int[] yP, boolean closed) {
		if (closed)
			g.drawPolygon(xP, yP, xP.length);
		else
			g.drawPolyline(xP, yP, xP.length);
	}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void fillTriangle (int x0, int x1, int x2, int y0, int y1, int y2) {
		final int[][] points = {{x0,x1,x2},{y0,y1,y2}};
		g.fillPolygon(points[0], points[1], 3);
	}

	/**Fills the Polygon with the current color
	 * Should not be used, it requires a conversion to the java.awt.Polygon Type.	 */
	public void fillPolygon(Point2D[] P0) {
		int Start = 0;
		final int[][] Points = convertPolygon(P0, Start);
		g.fillPolygon(Points[0], Points[1], Points[0].length);
	}

	/**Draws the Polygon and adds a closing Line, if wanted	 */
	public void fillPolygon(int[] xP, int[] yP, boolean closed) {
		g.fillPolygon(xP, yP, xP.length);
	}

	/**Draws the outline of a circular or elliptical arc
	 * covering the specified rectangle.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees, using the current color.
	 * Angles are interpreted such that 0&nbsp;degrees
	 * is at the 3&nbsp;o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * The resulting arc covers an area
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
	 * @param        x the <i>x</i> coordinate of the
	 *                    upper-left corner of the arc to be drawn.
	 * @param        y the <i>y</i>  coordinate of the
	 *                    upper-left corner of the arc to be drawn.
	 * @param        width the width of the arc to be drawn.
	 * @param        height the height of the arc to be drawn.
	 * @param        startAngle the beginning angle.
	 * @param        arcAngle the angular extent of the arc,
	 *                    relative to the start angle.
	 * @see         java.awt.Graphics#fillArc
	 * @since       JDK1.0
	 */
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	/**Fills a circular or elliptical arc covering the specified rectangle.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees.
	 * Angles are interpreted such that 0&nbsp;degrees
	 * is at the 3&nbsp;o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * The resulting arc covers an area
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
	 * @param        x the <i>x</i> coordinate of the
	 *                    upper-left corner of the arc to be filled.
	 * @param        y the <i>y</i>  coordinate of the
	 *                    upper-left corner of the arc to be filled.
	 * @param        width the width of the arc to be filled.
	 * @param        height the height of the arc to be filled.
	 * @param        startAngle the beginning angle.
	 * @param        arcAngle the angular extent of the arc,
	 *                    relative to the start angle.
	 * @see         java.awt.Graphics#drawArc
	 * @since       JDK1.0
	 */
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	/**Draws the outline of an oval.
	 * The result is a circle or ellipse that fits within the
	 * rectangle specified by the <code>x</code>, <code>y</code>,
	 * <code>width</code>, and <code>height</code> arguments.
	 * <p>
	 * The oval covers an area that is
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * and <code>height&nbsp;+&nbsp;1<code> pixels tall.
	 * @param       x the <i>x</i> coordinate of the upper left
	 *                     corner of the oval to be drawn.
	 * @param       y the <i>y</i> coordinate of the upper left
	 *                     corner of the oval to be drawn.
	 * @param       width the width of the oval to be drawn.
	 * @param       height the height of the oval to be drawn.
	 * @see         java.awt.Graphics#fillOval
	 * @since       JDK1.0
	 */
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	/**Fills an oval bounded by the specified rectangle with the
	 * current color.
	 * @param       x the <i>x</i> coordinate of the upper left corner
	 *                     of the oval to be filled.
	 * @param       y the <i>y</i> coordinate of the upper left corner
	 *                     of the oval to be filled.
	 * @param       width the width of the oval to be filled.
	 * @param       height the height of the oval to be filled.
	 * @see         java.awt.Graphics#drawOval
	 * @since       JDK1.0
	 */
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	/**Draws an outlined round-cornered rectangle using this graphics
	 * context's current color. The left and right edges of the rectangle
	 * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
	 * respectively. The top and bottom edges of the rectangle are at
	 * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
	 * @param      x the <i>x</i> coordinate of the rectangle to be drawn.
	 * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
	 * @param      width the width of the rectangle to be drawn.
	 * @param      height the height of the rectangle to be drawn.
	 * @param      arcWidth the horizontal diameter of the arc
	 *                    at the four corners.
	 * @param      arcHeight the vertical diameter of the arc
	 *                    at the four corners.
	 * @see        java.awt.Graphics#fillRoundRect
	 * @since      JDK1.0
	 */
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		//		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);}	//Height and Width must be positive!
		P.setX(x);
		P.setY(y);
		rx = arcWidth;
		ry = arcHeight;
		orderedMethod(width, height, MethodDrawRRect);
	}

	/**Fills the specified rounded corner rectangle with the current color.
	 * The left and right edges of the rectangle
	 * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>,
	 * respectively. The top and bottom edges of the rectangle are at
	 * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
	 * @param       x the <i>x</i> coordinate of the rectangle to be filled.
	 * @param       y the <i>y</i> coordinate of the rectangle to be filled.
	 * @param       width the width of the rectangle to be filled.
	 * @param       height the height of the rectangle to be filled.
	 * @param       arcWidth the horizontal diameter
	 *                     of the arc at the four corners.
	 * @param       arcHeight the vertical diameter
	 *                     of the arc at the four corners.
	 * @see         java.awt.Graphics#drawRoundRect
	 * @since       JDK1.0
	 */
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		//		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);}	//Height and Width must be positive!
		P.setX(x);
		rx = arcWidth;
		P.setY(y);
		ry = arcHeight;
		orderedMethod(width, height, MethodFillRRect);
	}

	//////////////////////////////////////////////////////////////////////////
	//Ellipse and Circle Methods: all those Methods are no longer delegated
	//to drawing the respective Polygons!

	/**Draws a circle or ellipse that fits within the
	 * rectangle specified by the <code>Line</code> argument.
	 * <p>
	 * @param       L the <i>Diagonal</i> through the upper left
	 *                     and lower right corner of the oval.
	 * @see         java.awt.Graphics#fillOval
	 * @since       JDK1.0
	 */
	public void drawEllipse(Line2D L) {
		Point2D Start = L.getStart();
		Point2D Width = L.getWidth();
		g.drawOval(Start.getX() - Width.getX(), Start.getY() - Width.getY(), Width.getX() << 1, Width.getY() << 1);
		//Height and Width must be positive!
	}

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public void drawEllipse(int r) {
		g.drawOval(-r, -r, r << 1, r << 1);
	}

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public void drawEllipse(Point2D M, int r) {
		g.drawOval(M.getX() - r, M.getY() - r, r << 1, r << 1);
	}

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public void drawEllipse(Point2D M, Point2D R) {
		g.drawOval(M.getX() - R.getX(), M.getY() - R.getY(), R.getX() << 1, R.getY() << 1);
	}

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void drawEllipse(Point2D R) {
		g.drawOval(-R.getX(), -R.getY(), R.getX() << 1, R.getY() << 1);
	}

	/**Draws an Arc with Radiuses R and the Start and End Angles in W	 */
	public void drawArc(Point2D R, Point2D W) {
		//		g.drawOval(-R.x, -R.y, R.x << 1, R.y << 1); }
		g.drawArc(0, 0, R.getX(), R.getY(), W.getX(), W.getY());
	}

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void drawArc(Point2D M, Point2D R, Point2D W) {
		g.drawArc(M.getX(), M.getY(), R.getX(), R.getY(), W.getX(), W.getY());
	}

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	//	public void drawSector (Point2D M, Point2D R, Point2D W) {
	//		drawPolygon(PolyTrigon.Sector (M, R, W), true);}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void drawRoundRect(Line2D L, int r) {
		Point2D Start = L.getStart();
		Point2D Width = L.getWidth();
		//		g.drawRoundRect(Start.x, Start.y, Width.x, Width.y, r, r);	//Height and Width must be positive!
		P.setX(Start.getX());
		P.setY(Start.getY());
		rx = r;
		ry = r;
		orderedMethod(Width.getX(), Width.getY(), MethodDrawRRect);
	}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void drawRoundRect(Line2D L, Point2D R) {
		Point2D Start = L.getStart();
		Point2D Stop = L.getStop();
		//		g.drawRoundRect(Start.x, Start.y, Width.x, Width.y, R.x, R.y);	//Height and Width must be positive!
		P.setX(Start.getX());
		P.setY(Start.getY());
		rx = R.getX();
		ry = R.getY();
		orderedMethod(Stop.getX(), Stop.getY(), MethodDrawRRect);
	}

	//filler Routines:

	/**Draws a circle or ellipse that fits within the
	 * rectangle specified by the <code>Line</code> argument.
	 * <p>
	 * @param       L the <i>Diagonal</i> through the upper left
	 *                     and lower right corner of the oval.
	 * @see         java.awt.Graphics#fillOval
	 * @since       JDK1.0
	 */
	public void fillEllipse(Line2D L) {
		Point2D Start = L.getStart();
		Point2D Width = L.getWidth();
		g.fillOval(Start.getX() - Width.getX(), Start.getY() - Width.getY(), Width.getX() << 1, Width.getY() << 1);
	}

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public void fillEllipse(int r) {
		g.fillOval(-r, -r, r << 1, r << 1);
	}

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public void fillEllipse(Point2D M, int r) {
		g.fillOval(M.getX() - r, M.getY() - r, r << 1, r << 1);
	}

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public void fillEllipse(Point2D M, Point2D R) {
		g.fillOval(M.getX() - R.getX(), M.getY() - R.getY(), R.getX() << 1, R.getY() << 1);
	}

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void fillEllipse(Point2D R) {
		g.fillOval(-R.getX(), -R.getY(), R.getX() << 1, R.getY() << 1);
	}

	/**Draws an Arc with Radiuses R and the Start and End Angles in W	 */
	public void fillArc(Point2D R, Point2D W) {
		g.fillArc(0, 0, R.getX(), R.getY(), W.getX(), W.getY());
	}

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void fillArc(Point2D M, Point2D R, Point2D W) {
		g.fillArc(M.getX(), M.getY(), R.getX(), R.getY(), W.getX(), W.getY());
	}

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	//	public void fillSector (Point2D M, Point2D R, Point2D W) {
	//		fillPolygon(PolyTrigon.Sector (M, R, W));}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void fillRoundRect(Line2D L, int r) {
		Point2D Start = L.getStart();
		Point2D Width = L.getWidth();
		//		g.fillRoundRect(Start.x, Start.y, Width.x, Width.y, r, r);	//Height and Width must be positive!
		P.setX(Start.getX());
		P.setY(Start.getY());
		rx = r;
		ry = r;
		orderedMethod(Width.getX(), Width.getY(), MethodFillRRect);
	}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void fillRoundRect(Line2D L, Point2D R) {
		Point2D Start = L.getStart();
		Point2D Width = L.getWidth();
		//		g.fillRoundRect(Start.x, Start.y, Width.x, Width.y, R.x, R.y);	//Height and Width must be positive!
		P.setX(Start.getX());
		P.setY(Start.getY());
		rx = R.getX();
		ry = R.getY();
		orderedMethod(Width.getX(), Width.getY(), MethodFillRRect);
	}

	/**Returns the bounding rectangle of the current clipping area.
	 * The coordinates in the rectangle are relative to the coordinate
	 * system origin of this graphics context.
	 * @return      the bounding rectangle of the current clipping area.
	 * @see         java.awt.Graphics#getClip
	 * @see         java.awt.Graphics#clipRect
	 * @see         java.awt.Graphics#setClip(int, int, int, int)
	 * @see         java.awt.Graphics#setClip(Shape)
	 * @since       JDK1.1
	 */
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	//Draw the Axis
	//Draw the Raster
	//Draw the Scale
	//Color Conversions

}
