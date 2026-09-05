package graphic;

import java.awt.Color;

/**
 * A graphics context that can be both written and read, adding non-recursive
 * flood fill on top of {@link AGraphImage} and {@link IGraph2DIn}.
 *
 * <p>The whole picture would have to be loaded into an integer array; the
 * highest byte of the integer is good for determining the z coordinate.
 *
 * TODO: add a z Raster in the highest Byte of the int Pixels.
 *		to enable limited z-Buffering.
 *		Use the Extent of the whole Picture for this.
 *
 *		Add Painting with transparent Planes to indicate hidden Bodies.
 *
 * @see AGraphImage
 * @see IGraph2DIn
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:55:18Z
 * digest: 1589100bcafed1aee8ae9bef8223ae6d23ea4ffa6f5add55d6151bdb8adffb86
 * stale: false
 * tags: [code/graphics, code/image_processing]
 * concepts: [Flood Fill Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
 */
public abstract class AGraphRead
	extends AGraphImage
	implements IGraph2DIn
{
	/** Constructor that defines a Clipping Area	 */
	public AGraphRead(Point2D ClipTL_, Point2D ClipBR_) {
		super(ClipTL_, ClipBR_); }

	/**Starts from the current Point in the current Painting Color	 */
	public void fillFlood(int Border) {
		fillFlood (P.getX(), P.getX(), P.getY(), +1, Border);}

	/**Fills the Area from the current Position on	 */
	public void fillFlood(int Border, Color Col) {
		setColor (Col);
		fillFlood (P.getX(), P.getX(), P.getY(), +1, Border);
	}

	/** Fills the area from the given starting point on, in the current painting color. */
	public void fillFlood(Point2D StartPoint, int Border) {
		fillFlood (StartPoint.getX(), StartPoint.getX(), StartPoint.getY(), +1, Border);}

	/** Fills the area from the given starting point on, in the given color. */
	public void fillFlood(Point2D StartPoint, int Border, Color Col) {
		setColor (Col);
		fillFlood (StartPoint.getX(), StartPoint.getX(), StartPoint.getY(), +1, Border);
	}

	/**Fills the Area in the current Color
	 * non-recursive Solution. The recursive Solution is easier to understand,
	 * but much slower, less effective and it eats up Stack Space.	 */
	public void fillFlood (int xl, int xr, int y, final int dir, final int borderColor)
	{	//Clipping
		int xlm,xrm,HP;
		int Color = getColor().getRGB();
		if (xr < xl) {xlm = xl; xl = xr; xr = xlm;}
		if (xl < ClipTL.getX()) xl = ClipTL.getX();
		if (xr < ClipTL.getX()) xr = ClipTL.getX();
		if (xl > ClipBR.getX()) xl = ClipBR.getX();
		if (xr > ClipBR.getX()) xr = ClipBR.getX();
		if (y  < ClipTL.getY()) y  = ClipTL.getY();
		if (y  > ClipBR.getY()) y  = ClipBR.getY();
		boolean found;	// Start:
		int col; --xl;	//increase the left border up to the right one or the Border Color
		while ((found = (((col = getPixel (++xl,y)) == borderColor) || (col == Color))) && (xl < xr));
		if (!found) return;	//{keine Luecke gefunden}
	    xlm = xl; xrm = xl; //{Luecke markieren}
	    HP = xrm;	//increase the right Border up to the Aufloesung or the Border Color
	    while (((col = getPixel (++xrm, y)) != borderColor) && (col != Color) && (xrm <= ClipBR.getX()));
		if (--xrm > HP) drawHLine (HP, xrm, y);
	    HP = xlm;	//Decrease the left Border up to the Aufloesung or the Border Color
	    while (((col = getPixel (--xlm, y)) != borderColor) && (col != Color) && (xlm >= ClipTL.getX()));
	    if (++xlm <= HP) drawHLine (HP, xlm, y);
	    if (xrm <= xr)	fillFlood (xrm, xr   ,y    , +dir, borderColor);	//naechste rechte Luecke
	    else			fillFlood (xr , xrm-1,y+dir, -dir, borderColor);	//sonst nach oben/unten weiter und Rueckkehr setzen
	    if (++xlm <=  xl) {
			fillFlood (xlm, xl   , y+dir, -dir, borderColor);	//{oben/unten am linken Rand der Luecke schauen}
			fillFlood (xlm, xrm-1, y-dir, +dir, borderColor);	//{unter der Luecke fuellen}
		}
	}

	/** Returns the Color at the current Position
	 * @see java.awt.image.BufferedImage#getRGB(int, int)
	 */
	public int getPixel() { return getPixel(P.getX(), P.getY()); }

	/** Returns the Color at the given Position
	 * @see java.awt.image.BufferedImage#getRGB(int, int)
	 */
	public int getPixel(Point2D P) { return getPixel(P.getX(), P.getY()); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IGraph2DOut: Implementation
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Not implemented; always throws.
	 *
	 * @throws RuntimeException always
	 * @see graphic.IGraphics#setPaintMode()
	 */
	public void setPaintMode() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented!");
	}

	/**
	 * Not implemented; always throws.
	 *
	 * @throws RuntimeException always
	 * @see graphic.IGraphics#setXORMode(java.awt.Color)
	 */
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented!");
	}

	/**
	 * Copies each pixel of the given rectangle to the location offset by
	 * (dx, dy), one pixel at a time via {@link #getPixel(int, int)} and
	 * {@link #setPixel(int, int, int)}.
	 *
	 * @see graphic.IGraphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
		for (int currY = y + height; --currY > y;) {
			for (int currX = x + width; --currX > x;) {
				setPixel(currX, currY, getPixel(currX, currY)); 
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Color at the given Position	 */
	public abstract int getPixel(int x, int y); // { return 0; }


}
