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
 * Title: AGraphImage<p>
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
public abstract class AGraphImage 
extends AGraphText 
implements IGraphImage {

	/**
	 * 
	 */
	public AGraphImage() {
		super();
	}

	/**
	 * @param ClipTL_
	 * @param ClipBR_
	 */
	public AGraphImage(Point2D ClipTL_, Point2D ClipBR_) {
		super(ClipTL_, ClipBR_);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+sx2, dy1+sx2, 0, 0, sx2, sy2, observer);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+width, dy1+height, 0, 0, sx2, sy2, observer);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final Color bgcolor, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+sx2, dy1+sx2, 0, 0, sx2, sy2, bgcolor, observer);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
		final int sx2 = img.getWidth (observer);
		final int sy2 = img.getHeight(observer);
		return drawImage(img, dx1, dy1, dx1+width, dy1+height, 0, 0, sx2, sy2, bgcolor, observer);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
		setColor(bgcolor); // TODO This can be optimized by setting a Default Color 
		fillRect(dx1, dy1, dx2, dy2);
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public abstract boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer); 
	
}
