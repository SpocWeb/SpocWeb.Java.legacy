/*
 * File Name: GraphicsAdapter.java
 * Created on: 11.09.2003
 *
 */
package graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

/**
 * Adapts an {@link IGraphImage} to a wrapped {@link Graphics} instance, delegating
 * nearly every call to it.
 * <p>
 * Only setting a single pixel and drawing an image would strictly be required, but the
 * faster {@link Graphics} implementations are chosen where available. This class is
 * final to allow the JVM to optimize call sites that declare a {@link GraphicsAdapter}
 * explicitly instead of the {@link IGraphImage} interface.
 *
 * @see graphic.JavaGraphic does about the same!
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
 * mtime: 2026-09-05T12:16:31Z
 * digest: ea801da2823a14485f9febd5483a339c679da6130729d5a3433f642b78eccc6b
 * stale: false
 * tags: [code/graphics, code/adapter]
 * concepts: [AWT Graphics Delegate Adapter]
 * facets: {layer: infrastructure, status: broken, complexity: high}
 * -->
 */
final public class GraphicsAdapter
extends AGraphText //AGraphTurtle //
//extends Graphics //lets this appear like a Graphics Object
implements IGraphImage {

	/** Reference to the Graphics Object being wrapped and delegated to */
	protected Graphics g;

	/**
	 * Wraps the given graphics context for delegation.
	 */
	public GraphicsAdapter(Graphics g_) {
		this.g = g_;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	///#region: @see java.awt.Graphics
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Always returns {@code null} instead of a copy of the underlying graphics context.
	 * @see java.awt.Graphics#create()
	 */
	public Graphics create() {
		// TODO: LOGIC: Graphics#create() is documented to return a new Graphics
		// object that is a copy of this one; returning null instead means any
		// caller that follows the normal Graphics contract and invokes a method
		// on the result throws NullPointerException.
		return null;
	}

	/**
	 * Disposes of the underlying graphics context.
	 * @see java.awt.Graphics#dispose()
	 */
	public void dispose() {
		g.dispose();
	}

	/**
	 * Translates the origin of the underlying graphics context.
	 * @see java.awt.Graphics#translate(int, int)
	 */
	public void translate(final int x, final int y) {
		g.translate(x, y);
	}

	/**
	 * Switches the underlying graphics context to paint (overwrite) mode.
	 * @see java.awt.Graphics#setPaintMode()
	 */
	public void setPaintMode() {
		g.setPaintMode();
	}

	/**
	 * Switches the underlying graphics context to XOR mode against the given color.
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 */
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	/**
	 * Returns the font of the underlying graphics context.
	 * @see java.awt.Graphics#getFont()
	 */
	public Font getFont() {
		return g.getFont();
	}

	/**
	 * Sets the font of the underlying graphics context.
	 * @see java.awt.Graphics#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		g.setFont(font);
	}

	/**
	 * Returns the font metrics for the given font from the underlying graphics context.
	 * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
	 */
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	/**
	 * Returns the clipping bounds of the underlying graphics context.
	 * @see java.awt.Graphics#getClipBounds()
	 */
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	/**
	 * Intersects the current clip with the given rectangle.
	 * @see java.awt.Graphics#clipRect(int, int, int, int)
	 */
	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
	}

	/**
	 * Sets the current clip to the given rectangle.
	 * @see java.awt.Graphics#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
	}

	/**
	 * Returns the current clip shape.
	 * @see java.awt.Graphics#getClip()
	 */
	public Shape getClip() {
		return g.getClip();
	}

	/**
	 * Sets the current clip to the given shape.
	 * @see java.awt.Graphics#setClip(java.awt.Shape)
	 */
	public void setClip(Shape clip) {
		g.setClip(clip);
	}

	/**
	 * Copies a rectangular area of the underlying graphics context by the given offset.
	 * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	/**
	 * Draws a line between the two given points.
	 * @see java.awt.Graphics#drawLine(int, int, int, int)
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	/**
	 * Fills the given rectangle with the current color.
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 */
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	/**
	 * Clears the given rectangle using the background color.
	 * @see java.awt.Graphics#clearRect(int, int, int, int)
	 */
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	/**
	 * Draws a rectangle with rounded corners.
	 * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
	 */
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	/**
	 * Fills a rectangle with rounded corners.
	 * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
	 */
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	/**
	 * Draws an oval inscribed in the given bounding rectangle.
	 * @see java.awt.Graphics#drawOval(int, int, int, int)
	 */
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	/**
	 * Fills an oval inscribed in the given bounding rectangle.
	 * @see java.awt.Graphics#fillOval(int, int, int, int)
	 */
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	/**
	 * Draws an arc inscribed in the given bounding rectangle.
	 * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
	 */
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * Fills an arc inscribed in the given bounding rectangle.
	 * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
	 */
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * Draws a sequence of connected line segments.
	 * @see java.awt.Graphics#drawPolyline(int[], int[], int)
	 */
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	/**
	 * Draws the outline of a polygon.
	 * @see java.awt.Graphics#drawPolygon(int[], int[], int)
	 */
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolygon(xPoints, yPoints, nPoints);
	}

	/** Fills a polygon. @see java.awt.Graphics#fillPolygon(int[], int[], int)	 */
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		//super.fillPolygon(xPoints, yPoints, nPoints);
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	/**
	 * Draws the given text at the given position.
	 * @see java.awt.Graphics#drawString(java.lang.String, int, int)
	 */
	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	/**
	 * Draws the given attributed text at the given position.
	 * @see java.awt.Graphics#drawString(java.text.AttributedCharacterIterator, int, int)
	 */
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.drawString(iterator, x, y);
	}

	/**
	 * Draws the given image at the given position, notifying the observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return g.drawImage(img, x, y, observer);
	}

	/**
	 * Draws the given image scaled into the given width and height, notifying the
	 * observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int x,
		int y,
		int width,
		int height,
		ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, observer);
	}

	/**
	 * Draws the given image at the given position over the given background color,
	 * notifying the observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x, y, bgcolor, observer);
	}

	/**
	 * Draws the given image scaled into the given width and height over the given
	 * background color, notifying the observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int x,
		int y,
		int width,
		int height,
		Color bgcolor,
		ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, bgcolor, observer);
	}

	/**
	 * Draws the given source rectangle of the image into the given destination
	 * rectangle, notifying the observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int dx1,
		int dy1,
		int dx2,
		int dy2,
		int sx1,
		int sy1,
		int sx2,
		int sy2,
		ImageObserver observer) {
		return g.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	/**
	 * Draws the given source rectangle of the image into the given destination
	 * rectangle over the given background color, notifying the observer as it loads.
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int dx1,
		int dy1,
		int dx2,
		int dy2,
		int sx1,
		int sy1,
		int sx2,
		int sy2,
		Color bgcolor,
		ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
	}

	/**
	 * Draws a 3-D highlighted rectangle.
	 * @see java.awt.Graphics#draw3DRect(int, int, int, int, boolean)
	 */
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g.draw3DRect(x, y, width, height, raised);
	}

	/**
	 * Draws the given bytes as text starting at the given position.
	 * @see java.awt.Graphics#drawBytes(byte[], int, int, int, int)
	 */
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		g.drawBytes(data, offset, length, x, y);
	}

	/**
	 * Draws the given characters as text starting at the given position.
	 * @see java.awt.Graphics#drawChars(char[], int, int, int, int)
	 */
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		g.drawChars(data, offset, length, x, y);
	}

	/**
	 * Draws the outline of the given polygon.
	 * @see java.awt.Graphics#drawPolygon(java.awt.Polygon)
	 */
	public void drawPolygon(Polygon p) {
		g.drawPolygon(p);
	}

	/**
	 * Draws the outline of the given rectangle.
	 * @see java.awt.Graphics#drawRect(int, int, int, int)
	 */
	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	/**
	 * Fills a 3-D highlighted rectangle.
	 * @see java.awt.Graphics#fill3DRect(int, int, int, int, boolean)
	 */
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		g.fill3DRect(x, y, width, height, raised);
	}

	/**
	 * Fills the given polygon.
	 * @see java.awt.Graphics#fillPolygon(java.awt.Polygon)
	 */
	public void fillPolygon(Polygon p) {
		g.fillPolygon(p);
	}

	/**
	 * Returns the clipping bounds, storing them into the given rectangle when possible.
	 * @see java.awt.Graphics#getClipBounds(java.awt.Rectangle)
	 */
	public Rectangle getClipBounds(Rectangle r) {
		return g.getClipBounds(r);
	}

	/**
	 * Returns the clipping rectangle.
	 * @see java.awt.Graphics#getClipRect()
	 * @deprecated
	 */
	public Rectangle getClipRect() {
		return g.getClipRect();
	}

	/**
	 * Returns the font metrics for the current font.
	 * @see java.awt.Graphics#getFontMetrics()
	 */
	public FontMetrics getFontMetrics() {
		return g.getFontMetrics();
	}

	/**
	 * Checks whether the given rectangle intersects the current clip.
	 * @see java.awt.Graphics#hitClip(int, int, int, int)
	 */
	public boolean hitClip(int x, int y, int width, int height) {
		return g.hitClip(x, y, width, height);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	///#region: addtional Methods from @see graphic.IGraph2DOut
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Allows to optimize the Setting of the Color in setClippedPixel,
	 * because always identical to the Color of the Delegate!
	 */
	private Color gColor;

	/**Sets the color for the next painting Action	 */
	public void setColor(Color color_) {
		g.setColor(col = gColor = color_);
	}

	/**
	 * Sets a Pixel in the current Color at the current Position
	 * @see graphic.IGraph2DOut#setPixel(java.awt.Color)
	 */
	public void setClippedPixel(final Color color_) {
		if ((gColor != color_) //Optimization!
			//&& !gColor.equals(color_) //maybe slower than actually setting it!
			) {
			g.setColor(gColor = color_);
		}
		g.drawLine(P.getX(), P.getY(), P.getX(), P.getY());
	}

	/////////////////////////////////////////////////////////////////////////////////////
	///#region: addtional Methods from @see graphic.IGraphAddtl
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Draws the given character at the current position {@link #P}.
	 * @see graphic.IGraphText#drawChar(char)
	 */
	public void drawChar(char c) {
		g.drawString(new String(new char[]{c}), P.x, P.y);
	}

	/**
	 * Draws the given character at the given position.
	 * @see graphic.IGraphText#drawChar(char, graphic.Point2D)
	 */
	public void drawChar(char c, Point2D Pt) {
		g.drawString(new String(new char[]{c}), Pt.x, Pt.y);
	}

	/**
	 * Draws the given text at the current position {@link #P}.
	 * @see graphic.IGraphText#drawString(java.lang.String)
	 */
	public void drawString(String S) {
		g.drawString(S, P.x, P.y);
	}

	/**
	 * Draws the given text at the given position.
	 * @see graphic.IGraphText#drawString(java.lang.String, graphic.Point2D)
	 */
	public void drawString(String S, Point2D Pt) {
		g.drawString(S, Pt.x, Pt.y);
	}

	/**
	 * Sets the pixel at the current position using the current color {@link #col}.
	 * @see graphic.IGraphShape#setClipPixel()
	 */
	public void setClipPixel() {
		setClippedPixel(col);
	}

	/**
	 * Sets the pixel at the current position; the given color is ignored.
	 * @see graphic.IGraph2DOut#setPixel(java.awt.Color)
	 */
	public void setPixel(Color color) {
		// TODO: LOGIC: the "color" parameter is never used; this always paints
		// with the field "col" (the previously-set current color) instead of
		// the color the caller explicitly passed in.
		setClippedPixel(col);
	}

	/**
	 * Draws a line from the current position {@link #P} to the given point, then
	 * moves the current position there.
	 * @see graphic.ITurtle#drawLine(graphic.Point2D)
	 */
	public void drawLine(Point2D P1) {
		g.drawLine(P.x, P.y, P1.x, P1.y);
		P.copyAt(P1);
	}

	/**
	 * Draws a horizontal line from the current position to the given x-coordinate,
	 * then updates the current x-position.
	 * @see graphic.IGraphAddtl#drawHLine(int)
	 */
	public void drawHLine(int x1) {
		g.drawLine(P.x, P.y, x1, P.y);
		P.setX(x1);
	}

	/**
	 * Draws a horizontal line at the given y-coordinate between the two given
	 * x-coordinates, then updates the current position.
	 * @see graphic.IGraphAddtl#drawHLine(int, int, int)
	 */
	public void drawHLine(int x0, int x1, int y_) {
		g.drawLine(x0, y_, x1, y_);
		P.copyAt(x1, y_);
	}

	/**
	 * Draws a horizontal line from the given point to the given x-coordinate, then
	 * updates the current position.
	 * @see graphic.IGraphAddtl#drawHLine(graphic.Point2D, int)
	 */
	public void drawHLine(Point2D P_, int x1) {
		g.drawLine(P_.x, P_.y, x1, P_.y);
		P.copyAt(x1, P_.y);
	}

	/**
	 * Draws a vertical line from the current position to the given y-coordinate,
	 * then updates the current y-position.
	 * @see graphic.IGraphAddtl#drawVLine(int)
	 */
	public void drawVLine(int y1) {
		g.drawLine(P.x, P.y, P.x, y1);
		P.setY(y1);
	}

	/**
	 * Draws a vertical line at the given x-coordinate between the two given
	 * y-coordinates, then updates the current position.
	 * @see graphic.IGraphAddtl#drawVLine(int, int, int)
	 */
	public void drawVLine(int x_, int y0, int y1) {
		g.drawLine(x_, y0, x_, y1);
		P.copyAt(x_, y1);
	}

	/**
	 * Draws a vertical line from the given point to the given y-coordinate, then
	 * updates the current position.
	 * @see graphic.IGraphAddtl#drawVLine(graphic.Point2D, int)
	 */
	public void drawVLine(Point2D P_, int y1) {
		g.drawLine(P_.x, P_.y, P_.x, y1);
		P.copyAt(P_.x, y1);
	}

	/**
	 * Draws the given line segment.
	 * @see graphic.IGraphAddtl#drawLine(graphic.Line2D)
	 */
	public void drawLine(Line2D L) {
		drawLine(L.getStart(), L.getStop());
	}

	/**
	 * Draws a line between the two given points, then updates the current position
	 * to the second point.
	 * @see graphic.IGraphAddtl#drawLine(graphic.Point2D, graphic.Point2D)
	 */
	public void drawLine(Point2D P0, Point2D P1) {
		g.drawLine(P0.x, P0.y, P1.x, P1.y);
		P.copyAt(P1.x, P1.y);
	}

	/**
	 * Draws a line from the current position to the given point, then updates the
	 * current position.
	 * @see graphic.IGraphAddtl#drawLine(int, int)
	 */
	public void drawLine(int x1, int y1) {
		g.drawLine(P.x, P.y, x1, y1);
		P.copyAt(x1, y1);
	}

	/**
	 * Fills the rectangle spanned by the given line's endpoints.
	 * @see graphic.IGraphAddtl#fillRect(graphic.Line2D)
	 */
	public void fillRect(Line2D L) {
		fillRect(L.getStart(), L.getStop());
	}

	/**
	 * Fills the rectangle spanned by the two given points.
	 * @see graphic.IGraphAddtl#fillRect(graphic.Point2D, graphic.Point2D)
	 */
	public void fillRect(Point2D P0, Point2D P1) {
		g.fillRect(P0.x, P0.y, P1.x, P1.y);
	}

	/**
	 * Fills the rectangle spanned by the current position and the given point.
	 * @see graphic.IGraphAddtl#fillRect(graphic.Point2D)
	 */
	public void fillRect(Point2D P1) {
		fillRect(P, P1);
	}

	/**
	 * Fills the rectangle spanned by the current position and the given point.
	 * @see graphic.IGraphAddtl#fillRect(int, int)
	 */
	public void fillRect(int x1, int y1) {
		g.fillRect(P.x, P.y, x1, y1);
	}

	/**
	 * Draws the outline of the rectangle spanned by the given line's endpoints.
	 * @see graphic.IGraphAddtl#drawRect(graphic.Line2D)
	 */
	public void drawRect(Line2D L) {
		drawRect(L.getStart(), L.getStop());
	}

	/**
	 * Draws the outline of the rectangle spanned by the current position and the
	 * given point.
	 * @see graphic.IGraphAddtl#drawRect(int, int)
	 */
	public void drawRect(int x1, int y1) {
		g.drawRect(P.x, P.y, x1, y1);
	}

	/**
	 * Draws the outline of the rectangle spanned by the two given points.
	 * @see graphic.IGraphAddtl#drawRect(graphic.Point2D, graphic.Point2D)
	 */
	public void drawRect(Point2D P0, Point2D P1) {
		g.drawRect(P0.x, P0.y, P1.x, P1.y);
	}

	/**
	 * Draws the outline of the rectangle spanned by the current position and the
	 * given point.
	 * @see graphic.IGraphAddtl#drawRect(graphic.Point2D)
	 */
	public void drawRect(Point2D P1) {
		drawRect(P, P1);
	}

	/**
	 * Draws a 3-D highlighted rectangle spanned by the two given points.
	 * @see graphic.IGraphAddtl#draw3DRect(graphic.Point2D, graphic.Point2D, boolean)
	 */
	public void draw3DRect(Point2D P0, Point2D P1, boolean raised) {
		g.draw3DRect(P0.x, P0.y, P1.x-P0.x, P1.y-P0.y, raised);
	}

	/**
	 * Fills a 3-D highlighted rectangle spanned by the two given points.
	 * @see graphic.IGraphAddtl#fill3DRect(graphic.Point2D, graphic.Point2D, boolean)
	 */
	public void fill3DRect(Point2D P0, Point2D P1, boolean raised) {
		g.fill3DRect(P0.x, P0.y, P1.x-P0.x, P1.y-P0.y, raised);
	}

	/**
	 * Draws or fills a 3-D highlighted rectangle, depending on {@code filled}.
	 * @see graphic.IGraphAddtl#Rect3D(int, int, int, int, boolean, boolean)
	 */
	public void Rect3D(int x, int y, int width, int height, boolean raised, boolean filled) {
		if (filled) {
			g.fill3DRect(x, y, width, height, raised);
		} else {
			g.draw3DRect(x, y, width, height, raised);
		}
	}

	/**
	 * Draws a closed polygon or an open polyline over the full length of the given
	 * coordinate arrays.
	 * @see graphic.IGraphAddtl#drawPolygon(int[], int[], boolean)
	 */
	public void drawPolygon(final int[] xP, final int[] yP, final boolean closed) {
		drawPolygon(xP, yP, closed, Math.min(xP.length, yP.length));
	}

	/**
	 * Draws either a closed polygon or an open polyline over the first {@code len}
	 * coordinates.
	 * @see graphic.IGraphAddtl#drawPolygon(int[], int[], boolean, int)
	 */
	public void drawPolygon(final int[] xP, final int[] yP, final boolean closed, final int len) {
		if (closed) {
			g.drawPolygon(xP, yP, len);
		} else {
			g.drawPolyline(xP, yP, len);
		}
	}

	/**
	 * Draws a triangle using the current position {@link #P} as the third corner.
	 * @see graphic.IGraphAddtl#drawTriangle(graphic.Point2D, graphic.Point2D)
	 */
	public void drawTriangle(Point2D P1, Point2D P2) {
		drawTriangle(P, P1, P2);
	}

	/**
	 * Draws the triangle spanned by the three given points.
	 * @see graphic.IGraphAddtl#drawTriangle(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
	public void drawTriangle(Point2D P0, Point2D P1, Point2D P2) {
		drawTriangle(P0.x, P1.x, P2.x, P0.y, P1.y, P2.y);
	}

	/**
	 * Draws a triangle using the current position as one corner and the two given
	 * x/y-coordinate pairs as the others.
	 * @see graphic.IGraphAddtl#drawTriangle(int, int, int, int)
	 */
	public void drawTriangle(int x1, int x2, int y1, int y2) {
		drawTriangle(P.x, x1, x2, P.y, y1, y2);
	}

	/**
	 * Draws the triangle spanned by the three given (x, y) coordinate pairs.
	 * @see graphic.IGraphAddtl#drawTriangle(int, int, int, int, int, int)
	 */
	public void drawTriangle(final int x0, final int x1, final int x2, final int y0, final int y1, final int y2) {
		g.drawLine(x0, y0, x1, y1);
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x2, y2, x0, y0);
	}

	/**
	 * Fills the polygon's interior with {@code BorderColor} and draws its outline
	 * with {@code InnerColor}.
	 * @see graphic.IGraphAddtl#fillPolygon(int[], int[], java.awt.Color, java.awt.Color)
	 */
	public void fillPolygon(int[] xP, int[] yP, Color BorderColor, Color InnerColor) {
		// TODO: LOGIC: the parameter names imply "BorderColor" strokes the
		// outline and "InnerColor" fills the interior, but the calls below do
		// the opposite - the interior is filled with BorderColor and the
		// outline is drawn with InnerColor, i.e. the two colors are swapped
		// relative to their names.
		final int len = Math.min(xP.length, yP.length);
		setColor(BorderColor);
		g.fillPolygon(xP, yP, len);
		setColor(InnerColor);
		g.drawPolygon(xP, yP, len);
	}

	/**
	 * Fills the polygon per {@link #fillPolygon(int[], int[], Color, Color)}, using
	 * the current color {@link #col} as the second color argument.
	 * @see graphic.IGraphAddtl#fillPolygon(int[], int[], java.awt.Color)
	 */
	public void fillPolygon(int[] xP, int[] yP, Color BorderColor) {
		fillPolygon(xP, yP, BorderColor, col);
	}

	/**
	 * Fills the polygon defined by the given coordinate arrays with the current color.
	 * @see graphic.IGraphAddtl#fillPolygon(int[], int[])
	 */
	public void fillPolygon(int[] xP, int[] yP) {
		final int len = Math.min(xP.length, yP.length);
		g.fillPolygon(xP, yP, len);
	}

	/**
	 * Draws an ellipse inscribed in the rectangle spanned by the given line's
	 * endpoints.
	 * @see graphic.IGraphAddtl#drawEllipse(graphic.Line2D)
	 */
	public void drawEllipse(Line2D L) {
		drawEllipse(L.getStart(), L.getStop());
	}

	/**
	 * Draws an ellipse using the current position {@link #P} as the bounding box's
	 * corner, with both sides set to the given radius.
	 * @see graphic.IGraphAddtl#drawEllipse(int)
	 */
	public void drawEllipse(int r) {
		g.drawOval(P.x, P.y, r, r);
	}

	/**
	 * Draws an ellipse using M as the bounding box's corner, with both sides set to
	 * the given radius.
	 * @see graphic.IGraphAddtl#drawEllipse(graphic.Point2D, int)
	 */
	public void drawEllipse(Point2D M, int r) {
		// TODO: LOGIC: IGraphAddtl#drawEllipse(Point2D, int) documents "Center
		// in M, Radius r", but drawOval(x, y, width, height) takes the
		// top-left corner and a full width/height, not a center and a radius;
		// this passes M and r directly, so the ellipse is neither centered on
		// M nor sized by 2*r as the contract requires.
		g.drawOval(M.x, M.y, r, r);
	}

	/**
	 * Draws an ellipse using M as the bounding box's corner, with the given point's
	 * coordinates used directly as width and height.
	 * @see graphic.IGraphAddtl#drawEllipse(graphic.Point2D, graphic.Point2D)
	 */
	public void drawEllipse(Point2D M, Point2D R) {
		// TODO: LOGIC: same contract mismatch as drawEllipse(Point2D, int) -
		// IGraphAddtl documents "Center in M and Radiuses R", but this passes
		// M and R straight into drawOval(x, y, width, height), so the ellipse
		// is not centered on M and R is used as a full width/height rather
		// than as radii.
		g.drawOval(M.x, M.y, R.x, R.y);
	}

	/**
	 * Draws an ellipse centered at the current position {@link #P} with the given
	 * radii, per {@link #drawEllipse(Point2D, Point2D)}.
	 * @see graphic.IGraphAddtl#drawEllipse(graphic.Point2D)
	 */
	public void drawEllipse(Point2D R) {
		drawEllipse(P, R);
	}

	/**
	 * Draws an arc centered at the current position {@link #P} with radii R and the
	 * start/extent angles in W.
	 * @see graphic.IGraphAddtl#drawArc(graphic.Point2D, graphic.Point2D)
	 */
	public void drawArc(Point2D R, Point2D W) {
		drawArc(P, R, W);
	}

	/**
	 * Draws an arc centered at M with radii R between the start and extent angles
	 * given in W.
	 * @see graphic.IGraphAddtl#drawArc(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
	public void drawArc(Point2D M, Point2D R, Point2D W) {
		g.drawArc(M.x, M.y, R.x, R.y, W.x, W.y);
	}

	/**
	 * Draws a rounded rectangle spanned by the given line's endpoints, with corner
	 * radius r.
	 * @see graphic.IGraphAddtl#drawRoundRect(graphic.Line2D, int)
	 */
	public void drawRoundRect(Line2D L, int r) {
		Point2D P1 = L.getStart();
		Point2D P2 = L.getStop();
		g.drawRoundRect(P1.x, P1.y, P2.x-P1.x, P2.y-P1.y, r, r);
	}

	/**
	 * Draws a rounded rectangle spanned by the given line's endpoints, with corner
	 * radii R.
	 * @see graphic.IGraphAddtl#drawRoundRect(graphic.Line2D, graphic.Point2D)
	 */
	public void drawRoundRect(Line2D Li, Point2D R) {
		drawRoundRect(Li.getStart(), Li.getStop(), R);
	}

	/**
	 * Draws a rounded rectangle spanned by P1 and P2, with corner radii R.
	 * @see graphic.IGraphAddtl#drawRoundRect(graphic.Line2D, graphic.Point2D)
	 */
	public void drawRoundRect(Point2D P1, Point2D P2, Point2D R) {
		g.drawRoundRect(P1.x, P1.y, P2.x-P1.x, P2.y-P1.y, R.x, R.y);
	}

	/**
	 * Fills an ellipse inscribed in the rectangle spanned by the given line's
	 * endpoints.
	 * @see graphic.IGraphAddtl#fillEllipse(graphic.Line2D)
	 */
	public void fillEllipse(Line2D L) {
		fillEllipse(L.getStart(), L.getStop());
	}

	/**
	 * Fills an ellipse at the current position with the given radius, per
	 * {@link #fillEllipse(Point2D, int)}.
	 * @see graphic.IGraphAddtl#fillEllipse(int)
	 */
	public void fillEllipse(int r) {
		fillEllipse(P, r);
	}

	/**
	 * Fills an ellipse using M as the bounding box's corner, with both sides set to
	 * the given radius.
	 * @see graphic.IGraphAddtl#fillEllipse(graphic.Point2D, int)
	 */
	public void fillEllipse(Point2D M, int r) {
		// TODO: LOGIC: same contract mismatch as drawEllipse(Point2D, int) -
		// IGraphAddtl documents "Center in M, Radius r", but fillOval(x, y,
		// width, height) takes a top-left corner and full width/height; M and
		// r are passed straight through without centering or doubling.
		g.fillOval(M.x, M.y, r, r);
	}

	/**
	 * Fills an ellipse using M as the bounding box's corner, with the given point's
	 * coordinates used directly as width and height.
	 * @see graphic.IGraphAddtl#fillEllipse(graphic.Point2D, graphic.Point2D)
	 */
	public void fillEllipse(Point2D M, Point2D R) {
		// TODO: LOGIC: same contract mismatch as drawEllipse(Point2D, Point2D)
		// - IGraphAddtl documents "Center in M and Radiuses R", but M and R
		// are passed straight into fillOval(x, y, width, height) without
		// centering on M or doubling R into a width/height.
		g.fillOval(M.x, M.y, R.x, R.y);
	}

	/**
	 * Fills an ellipse centered at the current position with the given radii, per
	 * {@link #fillEllipse(Point2D, Point2D)}.
	 * @see graphic.IGraphAddtl#fillEllipse(graphic.Point2D)
	 */
	public void fillEllipse(Point2D R) {
		fillEllipse(P, R);
	}

	/**
	 * Fills an arc centered at the current position with radii R between the
	 * start/extent angles in W.
	 * @see graphic.IGraphAddtl#fillArc(graphic.Point2D, graphic.Point2D)
	 */
	public void fillArc(Point2D R, Point2D W) {
		fillArc(P, R, W);
	}

	/**
	 * Fills an arc centered at M with radii R between the start and extent angles
	 * given in W.
	 * @see graphic.IGraphAddtl#fillArc(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
	public void fillArc(Point2D M, Point2D R, Point2D W) {
		g.fillArc(M.x, M.y, R.x, R.y, W.x, W.y);
	}

	/**
	 * Fills a rounded rectangle spanned by the given line's endpoints, with corner
	 * radius r.
	 * @see graphic.IGraphAddtl#fillRoundRect(graphic.Line2D, int)
	 */
	public void fillRoundRect(Line2D L, int r) {
		fillRoundRect(L.getStart(), L.getStop(), r, r);
	}

	/**
	 * Fills a rounded rectangle spanned by the given line's endpoints, with corner
	 * radii R.
	 * @see graphic.IGraphAddtl#fillRoundRect(graphic.Line2D, graphic.Point2D)
	 */
	public void fillRoundRect(Line2D Li, Point2D R) {
		fillRoundRect(Li.getStart(), Li.getStop(), R);
	}

	/**
	 * Fills a rounded rectangle spanned by P1 and P2, with corner radii R.
	 * @see graphic.IGraphAddtl#fillRoundRect(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
	public void fillRoundRect(Point2D P1, Point2D P2, Point2D R) {
		fillRoundRect(P1, P2, R.x, R.y);
	}

	/**
	 * Fills a rounded rectangle whose bounding box's width/height are taken directly
	 * from P2's coordinates, with corner radii rx/ry.
	 * @see graphic.IGraphAddtl#fillRoundRect(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
	public void fillRoundRect(Point2D P1, Point2D P2, int rx, int ry) {
		// TODO: LOGIC: fillRoundRect(int,int,int,int,int,int) expects
		// (x, y, width, height, arcWidth, arcHeight), but P2.x/P2.y are passed
		// directly as width/height instead of "P2.x-P1.x"/"P2.y-P1.y" as the
		// sibling drawRoundRect(Point2D, Point2D, Point2D) correctly computes
		// a few overloads above; the rectangle is sized and positioned wrong
		// whenever P1 is not the origin.
		g.fillRoundRect(P1.x, P1.y, P2.x, P2.y, rx, ry);
	}

	/**
	 * @see graphic.IGraphAddtl#drawPolygon(graphic.Point2D[], boolean)
	 */
//	public void drawPolygon(Point2D[] Points, boolean closed) {	}

	/**
	 * @see graphic.IGraphAddtl#drawPolygons(graphic.Point2D[][], boolean)
	 */
//	public void drawPolygons(Point2D[][] Polygons, boolean closed) {	}

	/**
	 * @see graphic.IGraphAddtl#drawPolygon(graphic.Point2D[], int[], boolean)
	 */
//	public void drawPolygon(Point2D[] Points, int[] Polygon, boolean closed) {	}

	/**
	 * @see graphic.IGraphAddtl#drawPolygons(graphic.Point2D[], int[][], boolean)
	 */
//	public void drawPolygons(Point2D[] Points, int[][] Polygons, boolean closed) {	}

	/**
	 * @see graphic.IGraphAddtl#drawPolygon(int[][], boolean)
	 */
//	public void drawPolygon(int[][] Pol, boolean closed) {	}

	/**
	 * @see graphic.IGraphAddtl#fillTriangle(graphic.Point2D, graphic.Point2D)
	 */
//	public void fillTriangle(Point2D P1, Point2D P2) {	}

	/**
	 * @see graphic.IGraphAddtl#fillTriangle(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
//	public void fillTriangle(Point2D P0, Point2D P1, Point2D P2) {	}

	/**
	 * @see graphic.IGraphAddtl#fillTriangle(int, int, int, int)
	 */
//	public void fillTriangle(int x1, int x2, int y1, int y2) {	}

	/**
	 * @see graphic.IGraphAddtl#fillTriangle(int, int, int, int, int, int)
	 */
//	public void fillTriangle(int x0, int x1, int x2, int y0, int y1, int y2) {	}

	/**
	 * @see graphic.IGraphAddtl#fillPolygon(graphic.Point2D[], java.awt.Color)
	 */
//	public void fillPolygon(Point2D[] P0, Color BorderColor) {	}

	/**
	 * @see graphic.IGraphAddtl#fillPolygon(graphic.Point2D[])
	 */
//	public void fillPolygon(Point2D[] P0) {	}

	/**
	 * @see graphic.IGraphAddtl#fillPolygon(graphic.Point2D[], java.awt.Color, java.awt.Color)
	 */
//	public void fillPolygon(Point2D[] P0, Color BorderColor, Color InnerColor) {	}

	/**
	 * @see graphic.IGraphAddtl#drawRegPoly(int, graphic.Point2D, graphic.Point2D)
	 */
//	public void drawRegPoly(int n, Point2D R, Point2D W) {	}

	/**
	 * @see graphic.IGraphAddtl#fillRegPoly(int, graphic.Point2D, graphic.Point2D)
	 */
//	public void fillRegPoly(int n, Point2D R, Point2D W) {	}

	/**
	 * @see graphic.IGraphAddtl#drawSector(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
/*	public void drawSector(Point2D M, Point2D R, Point2D W) {
		//g.drawArc(M.x, M.y, R.x, R.y, W.x, W.y);
		//g.drawLine()
	}

	/**
	 * @see graphic.IGraphAddtl#fillSector(graphic.Point2D, graphic.Point2D, graphic.Point2D)
	 */
//	public void fillSector(Point2D M, Point2D R, Point2D W) {	}

}
