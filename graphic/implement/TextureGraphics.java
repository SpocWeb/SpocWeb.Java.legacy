package graphic.implement;

import graphic.Graph2D;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;

/**
 * TextureGraphics defines a Texture derived from an Image Object
 * as the basis for setting Pixels, drawing Lines and filling Polygons.
 *
 * The easies way is to map the Pattern Pixels directly to Screen Pixels.
 * Of course this is only correct for Polygons with the same Distance.
 *
 * A refined proposition uses an inherent Coordinate System in the Polygon
 * to rescale the Pattern (the Factor is the same in x and y Direction ?!)
 *
 * Design Decisions:
 * MemoryImageSource and drawImage cannot be used,
 * because they are fitted only to map Rectangles to Rectangles
 * and is possible slower, because it doesn't use cached Information.
 * Using it to draw Pixels and Lines is a waste.
 * Using it to do parketting of Polygons is too complicated.
 *
 * <h2>Collaborators</h2>
 * <table>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link Graph2D}</td><td>superclass; supplies the pixel drawing primitive this class textures</td></tr>
 * </table>
 * @see Graph2D
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:06Z
 * digest: e10dfa76fb3e07d59d7957c94c39d6b5f9e07f3c080e7f98b00f899a6ccb0b2f
 * stale: false
 * tags: [code/graphics]
 * concepts: [Texture-Sampling Color Strategy]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class TextureGraphics
//	extends AGraphText
	extends Graph2D
{
	/**Memory Image	 */
	protected int[] Pixels;

	/**Height of the Memory Image	 */
	protected int Height;

	/**Width of the Memory Image	 */
	protected int Width;

	/** Source Image the {@link #Pixels} buffer was grabbed from. */
	protected Image Pattern;

	/**Scaling Factor	 */
	protected int Scale = 64;

	/**Initializing Constructor	 */
	public TextureGraphics(Image Pattern, Graphics g)
	{
		super(g);
		Width  = Pattern.getWidth (null);
		Height = Pattern.getHeight(null);
		Pixels = new int[Height*Width];
		PixelGrabber PG = new PixelGrabber(Pattern, 0, 0, Width, Height, Pixels, 0, Width);
		try{PG.grabPixels();}	//throws InterruptedException
		catch(InterruptedException x) {}
//		PG.startGrabbing();	//doesn't complete!
//		boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
	}

	//Optimizations: use MemoryImageSource and draw the Image

	/**Draws a horizontal Line not using setPixel(),
	 * does not clip anymore!
	 * Scaling is done before rolling over! */
/*	public void drawHLine    (int x1)
	{
		int xt = x1; if (P.x > x1) {x1 = P.x; P.x = xt;}
		int dy  = P.y;
		int dx1 = P.x;
		int dx2 = x1;
		int dfx = (P.x / Width) * Width;
		int sy  = P.y%Height;

		int sx1 = P.x - dfx;
		int sx2 = x1  - dfx;
		while (sx1 < sx2)
		{

			g.drawImage(Pattern, dx1, dy, dx2, dy, sx1, sy, sx2, sy, null);
			sx1 = 0
			dx1 += Width;
			dx2
//		g.drawImage(Pattern, dx1, dy, dx2, dy, sx1, sy, sx2, sy, null);
/*		int Start = Width*(P.y%Height);
		int Stop  = Start + x1; P.x--;	//one off because of pre-incrementing
		int Count = P.x%Width;
		while (++P.x <= x1)
		{
			if (++Count >= Width) Count = 0;
			setColor(Pixels[Start + Count]);
			super.setPixel();
		}
		P.x = xt;	//Now the Endpoint is the actual Coordinate
	}
*/

	/**Sets a Pixel in the current Color at the current Position.
	 * Clipping is done by setClipPixel()! 	 */
	public void setPixel()
	{
		int Px = P.getX();
		int Py = P.getY();
		if (Scale != 16) //16 == 2^4 = SqRt(256) for the Distance is Boxed in -1 to -127
		{
			Px = (Px << 4) / Scale;
			Py = (Py << 4) / Scale;
		}
		setColor(Pixels[Px%Width+Width*(Py%Height)]);
		super.setPixel();
	}

}
