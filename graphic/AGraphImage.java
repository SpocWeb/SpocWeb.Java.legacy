/*
 * File Name: AGraphImage.java
 * Created on: 05.07.2003
 *
 */
package graphic;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 * Implements the convenience overloads of {@link IGraphImage} in terms of
 * the one full-parameter {@code drawImage} overload subclasses must supply.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see IGraphImage
 * @see AGraphText
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:53:02Z
 * digest: 5020fab31d37f207583a574a24fc9ee9a77efa9e5127ca6782f19b1b94f24307
 * stale: false
 * tags: [code/graphics, code/image_processing]
 * concepts: [Image Rendering Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public abstract class AGraphImage 
extends AGraphText 
implements IGraphImage {

	/**
	 * Creates an instance with no clipping rectangle.
	 */
	public AGraphImage() {
		super();
	}

	/**
	 * Creates an instance clipped to the rectangle between the two given
	 * corners.
	 *
	 * @param ClipTL_ top-left corner of the clip rectangle
	 * @param ClipBR_ bottom-right corner of the clip rectangle
	 */
	public AGraphImage(Point2D ClipTL_, Point2D ClipBR_) {
		super(ClipTL_, ClipBR_);
	}

	/**
	 * Draws the whole image at its natural size with its top-left corner at
	 * (dx1, dy1).
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+sx2, dy1+sx2, 0, 0, sx2, sy2, observer);
	}

	/**
	 * Draws the whole image scaled to the given width and height, with its
	 * top-left corner at (dx1, dy1).
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+width, dy1+height, 0, 0, sx2, sy2, observer);
	}

	/**
	 * Draws the whole image at its natural size, filling transparent areas
	 * with the given background color.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final Color bgcolor, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+sx2, dy1+sx2, 0, 0, sx2, sy2, bgcolor, observer);
	}

	/**
	 * Draws the whole image scaled to the given width and height, filling
	 * transparent areas with the given background color.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+width, dy1+height, 0, 0, sx2, sy2, bgcolor, observer);
	}

	/**
	 * Fills the destination rectangle with the background color, then draws
	 * the source rectangle of the image into it.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
		setColor(bgcolor); // TODO This can be optimized by setting a Default Color
		fillRect(dx1, dy1, dx2, dy2);
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	/**
	 * Draws the source rectangle (sx1, sy1)-(sx2, sy2) of the image scaled
	 * into the destination rectangle (dx1, dy1)-(dx2, dy2). Left to
	 * subclasses to implement without an intermediate background fill.
	 *
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public abstract boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer);
	
}
