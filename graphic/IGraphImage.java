/*
 * File Name: IGraphImage.java
 * Created on: 14.09.2003
 *
 */
package graphic;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 * Collects all methods related to painting an image, to structure
 * {@link java.awt.Graphics}'s interface.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see graphic.IGraphics known subclass
 * @see graphic.AGraphImage known use
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:34Z
 * digest: 4fe135cfc993ee8a46020020168662818e23c21cbe94ee1ab5ec5639f8ca89a8
 * stale: false
 * tags: [code/graphics, code/image_processing]
 * concepts: [Image Rendering Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IGraphImage extends IGraphText {

	/**
	 * Draws the whole image at its natural size with its top-left corner at
	 * (dx1, dy1).
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final ImageObserver observer);

	/**
	 * Draws the whole image scaled to the given width and height, with its
	 * top-left corner at (dx1, dy1).
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final ImageObserver observer);

	/**
	 * Draws the whole image at its natural size, filling transparent areas
	 * with the given background color.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final Color bgcolor, final ImageObserver observer);

	/**
	 * Draws the whole image scaled to the given width and height, filling
	 * transparent areas with the given background color.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final Color bgcolor, final ImageObserver observer);

	/**
	 * Draws the source rectangle (sx1, sy1)-(sx2, sy2) of the image scaled
	 * into the destination rectangle (dx1, dy1)-(dx2, dy2), filling
	 * transparent areas with the given background color.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer);

	/**
	 * Draws the source rectangle (sx1, sy1)-(sx2, sy2) of the image scaled
	 * into the destination rectangle (dx1, dy1)-(dx2, dy2).
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer);

	/**
	 * @param img the Image to draw. 
	 * @param x the Top Left Corner of the Image
	 * @param y the Top Left Corner of the Image
	 * @param width the Width of the Image
	 * @param height the Height of the Image
	 */
//	public void drawImage(Image img, int x, int y, int width, int height) {}

	/**
	 * @param imgUrl the URL of the Image to draw. 
	 * @param x the Top Left Corner of the Image
	 * @param y the Top Left Corner of the Image
	 * @param width the Width of the Image
	 * @param height the Height of the Image
	 */
/*	public void drawImage(
		String imgUrl,
		int x,
		int y,
		int width,
		int height,
		Frame fr)
		throws MalformedURLException {}

*/

}
