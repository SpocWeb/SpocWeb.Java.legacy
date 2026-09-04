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
 * Title: IGraphImage<p>
 * Description:
 * Purpose:
 * Collects all Methods related to painting an Image
 * to structure the Interface of @see java.awt.Graphics
 *
 * Known SubClasses: 
 * @see graphic.IGraphics
 *
 * Known Uses: 
 * @see graphic.AGraphImage
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IGraphImage extends IGraphText {

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final ImageObserver observer);

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final ImageObserver observer);

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final Color bgcolor, final ImageObserver observer);

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int width, final int height, final Color bgcolor, final ImageObserver observer);

	/**
	 * @see graphic.IGraphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer);

	/**
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
